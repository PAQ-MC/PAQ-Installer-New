package org.magetech.paq.launcher;

import com.github.zafarkhaja.semver.Version;
import org.apache.commons.io.FilenameUtils;
import org.magetech.paq.Launch;
import org.magetech.paq.launcher.data.Repository;
import org.magetech.paq.launcher.repository.IPackage;
import org.yaml.snakeyaml.Yaml;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.charset.Charset;
import java.util.Properties;

/**
 * Created by Aleksander on 08.12.13.
 */
public class DefaultLaunchSystem implements ILaunchSystem {
    private final Object _lock = new Object();
    private final String _confDir;
    private Repository _repository;

    public DefaultLaunchSystem(String paqDir) {
        _confDir = paqDir;
    }

    private void EnsureRepository() throws IOException {
        if(_repository == null) {
            synchronized (_lock) {
                if(_repository == null) {
                    _repository = loadRepository(_confDir);
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

        for(Repository.RepositoryPackage p : _repository.getPackages()) {
            if(p.getId().equals(id))
                return p;
        }

        return null;
    }

    @Override
    public void launch(String appId, Version version) throws IOException, ClassNotFoundException, NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        File jar = new File(getAppPath(appId, version));
        Launch.jar(jar, new String[0]);
    }

    private static Repository loadRepository(String dir) throws IOException {
        String file = FilenameUtils.concat(dir, "config.yml");
        if(new File(file).exists()) {
            try(FileInputStream fs = new FileInputStream(file)) {
                return Repository.load(fs);
            }
        }

        return Repository.empty();
    }

    private static void writeRepository(String dir, String content) throws IOException {
        String file = FilenameUtils.concat(dir, "config.yml");
        try(FileOutputStream fs = new FileOutputStream(file)) {
            fs.write(content.getBytes(Charset.forName("UTF-8")));
            fs.flush();
        }
    }
}
