package org.magetech.paq.installer;

import com.github.zafarkhaja.semver.Version;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.magetech.paq.DirUtils;
import org.magetech.paq.StreamUtils;
import org.magetech.paq.UrlUtils;
import org.magetech.paq.ZipUtils;
import org.magetech.paq.configuration.Property;
import org.magetech.paq.configuration.PropertyLoader;
import org.magetech.paq.installer.data.ModRepository;
import org.magetech.paq.installer.data.Pack;
import org.magetech.paq.installer.data.PackConfig;
import org.magetech.paq.installer.data.PackRepository;
import org.pmw.tinylog.Logger;

import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Aleksander on 09.12.13.
 */
public class Installer {
    static final String _dir;
    static final String _instDir;
    static final String _repoFile;
    static final String _modsFile;
    static final String _modsDir;
    static final String _packsDir;
    static final String _onlineRepo;
    static final String _onlineModRepo;

    static {
        String onlineRepo = null;
        String onlineModRepo = null;
        String dir = null;
        String instDir = null;

        try {
            dir = DirUtils.getDataDir();
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            instDir = DirUtils.getDataDir("inst");
        } catch(IOException e) {
            e.printStackTrace();
        }

        try {
            onlineRepo = PropertyLoader.load("installer.properties").getProperty(Property.INSTALLER_REPOSITORY);
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            onlineModRepo = PropertyLoader.load("installer.properties").getProperty(Property.INSTALLER_MOD_REPOSITORY);
        } catch (IOException e) {
            e.printStackTrace();
        }

        _onlineRepo = onlineRepo;
        _onlineModRepo = onlineModRepo;
        _dir = dir;
        _instDir = instDir;

        _repoFile = FilenameUtils.concat(_dir, "packs.yml");
        _modsFile = FilenameUtils.concat(_dir, "mods.yml");
        _modsDir = FilenameUtils.concat(_dir, "mods");
        _packsDir = FilenameUtils.concat(_dir, "packs");
    }

    private final IInstallAdapter _adapter;
    public Installer(IInstallAdapter adapter) {
        _adapter = adapter;
    }

    public void updateRepos() throws IOException {
        new File(_dir).mkdirs();

        _adapter.reset(2);
        try (Closeable ignored = _adapter.beginAction("Downloading pack repository");
            InputStream is = new URL(_onlineRepo).openStream()) {
            StreamUtils.saveTo(is, _repoFile);
        }

        try (Closeable ignored = _adapter.beginAction("Downloading mod list");
            InputStream is = new URL(_onlineModRepo).openStream()) {
            StreamUtils.saveTo(is, _modsFile);
        }
    }

    private static Version getInstalledVersion(String repositoryFile) throws IOException {
        Pack packInfo;
        try(InputStream is = new FileInputStream(repositoryFile)) {
            packInfo = Pack.load(is);
        }

        return packInfo.getVersion();
    }

    private String getFile(ModRepository.ModConfig mod) {
        String dir = FilenameUtils.concat(_modsDir, mod.getId());
        String file = FilenameUtils.concat(dir, mod.getId() + "-" + mod.getVersion().toString() + ".download");

        new File(dir).mkdirs();

        return file;
    }

    private void download(ModRepository.ModConfig mod) throws IOException, InvocationTargetException, InterruptedException {
        String file = getFile(mod);

        if(new File(file).exists()) {
            Logger.info(mod.getId() + "-" + mod.getVersion() + " skipped, because it's already downloaded");
            return; // already downloaded
        }

        InputStream is;
        if(mod.getBrowser()) {
            File f = _adapter.downloadManually(mod.getPath(), mod.getFileName());
            f.deleteOnExit();
            is = new FileInputStream(f);
        }
        else {
            is = new URL(mod.getPath()).openStream();
        }

        try(InputStream in = is) {
            StreamUtils.saveTo(is, file);
        }
    }

    private void copy(ModRepository.ModConfig mod, String directory) throws IOException {
        String file = getFile(mod);
        String fileName = mod.getFileName();

        String wantedFile = FilenameUtils.concat(directory, fileName);
        FileUtils.copyFile(new File(file), new File(wantedFile));
    }

    public void install(String pack, boolean isServer) throws IOException, ClassNotFoundException, NoSuchMethodException, InvocationTargetException, IllegalAccessException, InterruptedException {
        updateRepos();
        PackRepository packsRepo;
        ModRepository modRepo;
        try (InputStream is = new FileInputStream(_repoFile)) {
            packsRepo = PackRepository.load(is);
        }
        try (InputStream is = new FileInputStream(_modsFile)) {
            modRepo = ModRepository.load(is);
        }

        String packDir = FilenameUtils.concat(_packsDir, pack);
        String packFile = FilenameUtils.concat(packDir, pack + ".yml");

        String onlinePackFile = UrlUtils.relativeTo(_onlineRepo, pack + "/" + pack + ".yml");

        new File(packDir).mkdirs();

        _adapter.reset(3);
        try (Closeable ignored = _adapter.beginAction("Downloading mod-pack available versions");
            InputStream is = new URL(onlinePackFile).openStream()) {
            StreamUtils.saveTo(is, packFile);
        }

        Version latest;
        try (InputStream is = new FileInputStream(packFile)) {
            latest = PackConfig.load(is).getLatestVersion(false);
        }

        String specificPackFile = FilenameUtils.concat(packDir, pack + "-" + latest.toString() + ".yml");
        String onlineSpecificPackFile = UrlUtils.relativeTo(onlinePackFile, pack + "-" + latest.toString() + ".yml");

        try (Closeable ignored = _adapter.beginAction("Downloading mod-pack info");
            InputStream is = new URL(onlineSpecificPackFile).openStream()) {
            StreamUtils.saveTo(is, specificPackFile);
        }

        Pack packConfig;
        try (InputStream is = new FileInputStream(specificPackFile)) {
            packConfig = Pack.load(is);
        }

        String configFile = FilenameUtils.concat(packDir, pack + "-" + packConfig.getVersion() + "-config.zip");
        try (Closeable ignored = _adapter.beginAction("Downloading mod-pack mod-configs");
            InputStream is = new URL(packConfig.getConfig()).openStream()) {
            StreamUtils.saveTo(is, configFile);
        }

        _adapter.reset(packConfig.getMods().size());

        List<ModRepository.ModConfig> mods = new ArrayList<ModRepository.ModConfig>();
        for(Pack.ModConfig mod : packConfig.getMods()) {
            ModRepository.ModConfig m = modRepo.find(mod.getId(), mod.getVersion());
            if(m == null)
                throw new IllegalStateException("mod " + mod.getId() + "@" + mod.getId() + " does not exist in mods repository.");
            if(m.getSide().equals(ModRepository.Side.Client) && isServer) continue;
            if(m.getSide().equals(ModRepository.Side.Server) && !isServer) continue;

            mods.add(m);

            try(Closeable ignored = _adapter.beginAction("Downloading " + mod.getId() + " " + mod.getVersion())) {
                download(m);
            }
        }

        String forgeVersionId = ForgeInstaller.install(packConfig.getForge(), isServer);
        String instDir = FilenameUtils.concat(_instDir, packConfig.getId());
        String modsDir = FilenameUtils.concat(instDir, "mods");
        String configDir = FilenameUtils.concat(instDir, "config");

        ZipUtils.unzip(configFile, configDir);

        for(ModRepository.ModConfig mod : mods) {
            // TODO: "Install" mod
            copy(mod, modsDir);
        }

        if(!isServer) {
            Minecraft.updatePackLaunchProfile(packConfig.getId(), forgeVersionId, instDir);
        }

        _adapter.end();
    }
}
