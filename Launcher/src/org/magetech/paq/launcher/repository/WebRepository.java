package org.magetech.paq.launcher.repository;

import com.github.zafarkhaja.semver.Version;
import org.apache.commons.io.FilenameUtils;
import org.magetech.paq.Assert;
import org.magetech.paq.IBackgroundReporter;
import org.magetech.paq.Out;
import org.magetech.paq.StreamUtils;
import org.magetech.paq.launcher.data.Repository;

import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by Aleksander on 06.12.13.
 */
public class WebRepository implements IRepository {
    private final URL _url;
    private final IBackgroundReporter _reporter;
    private Repository _repository;

    public WebRepository(URL url, IBackgroundReporter reporter) {
        Assert.notNull(url, "url");
        Assert.notNull(reporter, "reporter");

        _url = url;
        _reporter = reporter;
    }

    @Override
    public List<IPackage> getPackages() throws IOException {
        List<IPackage> packages = new ArrayList<IPackage>();

        ensureRepository();
        for(Repository.RepositoryPackage packageConfig : _repository.getApps()) {
            Out<IPackage> pack = new Out<IPackage>();
            if(WebPackage.load(_url, packageConfig, _reporter, pack))
                packages.add(pack.getValue());
        }
        return packages;
    }

    private void ensureRepository() throws IOException {
        if(_repository == null) {
            _repository = loadRepository(_url);
        }
    }

    private Repository loadRepository(URL url) throws IOException {
        _reporter.reset(1);
        try(Closeable ignored = _reporter.beginAction("Downloading application list");
            InputStream in = _url.openStream()) {
            return Repository.load(in);
        }
    }

    @Override
    public void checkUpToDate(Version runningVersion) throws IOException {
        Assert.notNull(runningVersion, "runningVersion");

        ensureRepository();

        if(_repository.getVersion() == null) {
            _reporter.error("Reporitory does not contain version information", "Repository error");
            System.exit(0);
        }

        if(runningVersion.lessThan(_repository.getVersion())) {
            _reporter.error("The following launcher is outdated, please download a new launcher", "Launcher outdated");
            System.exit(0);
        }
    }

    static class WebPackage implements IPackage {
        private final String _id;
        private final List<Version> _versions;
        private final URL _url;
        private final IBackgroundReporter _reporter;

        public static boolean load(URL url, Repository.RepositoryPackage config, IBackgroundReporter reporter, Out<IPackage> pack) {
            try {
                String id = config.getId();
                List<Version> versions = config.getVersions();
                pack.setValue(new WebPackage(url, id, versions, reporter));
                return true;
            }
            catch (ClassCastException e) {
                return false;
            }
        }

        private WebPackage(URL repositoryUrl, String id, List<Version> versions, IBackgroundReporter reporter) {
            Assert.notNull(repositoryUrl, "repositoryUrl");
            Assert.notNull(id, "id");
            Assert.notNull(reporter, "reporter");
            Assert.notNullItems(versions, "version");

            _id = id;
            _url = repositoryUrl;
            _reporter = reporter;
            _versions = new ArrayList<Version>(versions);
            Collections.sort(_versions);
        }

        @Override
        public String getId() {
            return _id;
        }

        @Override
        public Version getLastVersion() {
            return getLastVersion(false);
        }

        @Override
        public Version getLastVersion(boolean includePreRelease) {
            for(int i = _versions.size() - 1; i >= 0; i--) {
                Version v = _versions.get(i);
                String preReleaseVersion = v.getPreReleaseVersion();
                boolean isPreRelease = preReleaseVersion == null || !preReleaseVersion.isEmpty();
                if(!isPreRelease || includePreRelease)
                    return v;
            }
            return _versions.get(_versions.size() - 1); // if none is released, use last pre-release
        }

        @Override
        public void copyTo(Version version, File file) throws IOException {
            String path = _url.getPath();
            int separatorIndex = FilenameUtils.indexOfLastSeparator(path);
            path = path.substring(0, separatorIndex + 1) + _id + "/" +  _id + "-" + version + ".jar";
            URL url = new URL(_url.getProtocol(), _url.getHost(), _url.getPort(), path);

            _reporter.reset(1);
            try(Closeable ignored = _reporter.beginAction("Downloading app");
                InputStream in = url.openStream()) {
                StreamUtils.saveTo(in, file);
            }
        }
    }
}
