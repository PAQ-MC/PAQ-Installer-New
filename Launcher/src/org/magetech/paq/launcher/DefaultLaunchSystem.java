package org.magetech.paq.launcher;

import com.github.zafarkhaja.semver.Version;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.magetech.paq.Assert;
import org.magetech.paq.IBackgroundReporter;
import org.magetech.paq.Launch;
import org.magetech.paq.launcher.data.Repository;
import org.magetech.paq.launcher.repository.IPackage;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.nio.charset.Charset;

/**
 * Created by Aleksander on 08.12.13.
 */
public class DefaultLaunchSystem implements ILaunchSystem {
    private final Object _lock = new Object();
    private final String _confDir;
    private final IBackgroundReporter _reporter;
    private final Version _version;
    private Repository _repository;

    public DefaultLaunchSystem(String paqDir, Version currentVersion, IBackgroundReporter reporter) {
        Assert.notNull(paqDir, "paqDir");
        Assert.notNull(reporter, "reporter");
        Assert.notNull(currentVersion, "currentVersion");

        _confDir = paqDir;
        _reporter = reporter;
        _version = currentVersion;
    }

    private void EnsureRepository() throws IOException {
        if(_repository == null) {
            synchronized (_lock) {
                if(_repository == null) {
                    _repository = loadRepository(_confDir, _version);
                }
            }
        }
    }

    private String getAppPath(String appId, Version version) {
        String appDir = FilenameUtils.concat(_confDir, appId);
        return FilenameUtils.concat(appDir, appId + "-" + version.toString() + ".jar");
    }

    @Override
    public boolean hasInstalled(String appId, Version version) {
        return new File(getAppPath(appId, version)).exists();
    }

    @Override
    public void installLatest(IPackage pack) throws IOException {
        Version version = pack.getLastVersion();
        String id = pack.getId();

        Repository.RepositoryPackage repositoryPackage = find(id);
        if(repositoryPackage == null) {
            repositoryPackage = _repository.add(id);
        }

        ensureVersion(repositoryPackage, version);

        File file = new File(getAppPath(id, version));
        file.getParentFile().mkdirs();
        pack.copyTo(version, file);

        save();
    }

    @Override
    public void deleteAll(String id) throws IOException {
        Repository.RepositoryPackage repositoryPackage = find(id);
        if(repositoryPackage == null) {
            throw new IllegalStateException("package not found");
        }

        repositoryPackage.getVersions().clear();
        File file = new File(getAppPath(id, Version.valueOf("0.0.0")));
        FileUtils.cleanDirectory(file.getParentFile());
    }

    private void save() throws IOException {
        String data = _repository.dump();
        writeRepository(_confDir, data);
    }

    private void ensureVersion(Repository.RepositoryPackage repositoryPackage, Version version) {
        for(Version v : repositoryPackage.getVersions()) {
            if(v.equals(version))
                return;
        }

        repositoryPackage.addVersion(version);
    }

    private Repository.RepositoryPackage find(String id) throws IOException {
        EnsureRepository();

        for(Repository.RepositoryPackage p : _repository.getApps()) {
            if(p.getId().equals(id))
                return p;
        }

        return null;
    }

    @Override
    public void launch(String appId, Version version, String[] args) throws IOException, ClassNotFoundException, NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        File jar = new File(getAppPath(appId, version));
        Launch.jar(jar, args);
    }

    private static Repository loadRepository(String dir, Version version) throws IOException {
        String file = FilenameUtils.concat(dir, "config.yml");
        File f = new File(file);
        if(f.exists()) {
            try(FileInputStream fs = new FileInputStream(file)) {
                Repository repo =  Repository.load(fs);
                if(repo.getVersion().equals(version))
                    return repo;
            }
        }

        if(f.exists())
            f.delete();

        return Repository.empty(version);
    }

    private static void writeRepository(String dir, String content) throws IOException {
        String file = FilenameUtils.concat(dir, "config.yml");
        try(FileOutputStream fs = new FileOutputStream(file)) {
            fs.write(content.getBytes(Charset.forName("UTF-8")));
            fs.flush();
        }
    }
}
