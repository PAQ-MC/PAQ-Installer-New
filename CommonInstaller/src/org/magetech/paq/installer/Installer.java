package org.magetech.paq.installer;

import com.github.zafarkhaja.semver.Version;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.magetech.paq.DirUtils;
import org.magetech.paq.StreamUtils;
import org.magetech.paq.UrlUtils;
import org.magetech.paq.configuration.Property;
import org.magetech.paq.configuration.PropertyLoader;
import org.magetech.paq.installer.data.ModRepository;
import org.magetech.paq.installer.data.Pack;
import org.magetech.paq.installer.data.PackRepository;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

/**
 * Created by Aleksander on 09.12.13.
 */
public class Installer {
    static final String _dir;
    static final String _repoFile;
    static final String _modsFile;
    static final String _modsDir;
    static final String _onlineRepo;
    static final String _onlineModRepo;

    static {
        String onlineRepo = null;
        String onlineModRepo = null;
        String dir = null;

        try {
            dir = DirUtils.getDataDir();
        } catch (IOException e) {
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

        _repoFile = FilenameUtils.concat(_dir, "packs.yml");
        _modsFile = FilenameUtils.concat(_dir, "mods.yml");
        _modsDir = FilenameUtils.concat(_dir, "mods");
    }

    public void updateRepos() throws IOException {
        new File(_dir).mkdirs();

        try(InputStream is = new URL(_onlineRepo).openStream()) {
            StreamUtils.saveTo(is, _repoFile);
        }

        try(InputStream is = new URL(_onlineModRepo).openStream()) {
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
        String file = FilenameUtils.concat(_dir, mod.getId() + "-" + mod.getVersion().toString() + ".download");

        new File(dir).mkdirs();

        return file;
    }

    private void download(ModRepository.ModConfig mod) throws IOException {
        String file = getFile(mod);

        if(new File(file).exists())
            return; // already downloaded

        InputStream is;
        if(mod.getBrowser()) {
            // TODO: Open browser, wait for manual download, assign "is" to downloaded file
            throw new NotImplementedException();
        }
        else {
            is = new URL(mod.getPath()).openStream();
        }

        try(InputStream in = is) {
            StreamUtils.saveTo(is, file);
        }
    }

    public void install(String pack, boolean isServer) throws IOException, ClassNotFoundException, NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        updateRepos();
        PackRepository packsRepo;
        ModRepository modRepo;
        try (InputStream is = new FileInputStream(_repoFile)) {
            packsRepo = PackRepository.load(is);
        }
        try (InputStream is = new FileInputStream(_modsFile)) {
            modRepo = ModRepository.load(is);
        }

        PackRepository.PackConfig packConfig = packsRepo.getPack(pack);

        String modDir = FilenameUtils.concat(_dir, pack);
        String repoFile = FilenameUtils.concat(modDir, "repository.yml");

        Version installedVersion = getInstalledVersion(repoFile);
        if(installedVersion.equals(packConfig.getVersion()))
            return; // no new version

        try(InputStream is = new URL(UrlUtils.relativeTo(_onlineRepo, packConfig.getId() + "/" + packConfig.getId() + "-" + packConfig.getVersion().toString() + ".yml")).openStream()) {
            StreamUtils.saveTo(StreamUtils.append(is, Pack.getVersionString(packConfig.getVersion())), repoFile);
        }

        Pack packInfo;
        try(InputStream is = new FileInputStream(repoFile)) {
            packInfo = Pack.load(is);
        }

        List<ModRepository.ModConfig> mods = new ArrayList<ModRepository.ModConfig>();
        for(Pack.ModConfig mod : packInfo.getMods()) {
            ModRepository.ModConfig m = modRepo.find(mod.getId(), mod.getVersion());

            mods.add(m);
            download(m);
        }

        ForgeInstaller.install(isServer);

        for(ModRepository.ModConfig mod : mods) {
            // TODO: "Install" mod
            throw new NotImplementedException();
        }
    }
}
