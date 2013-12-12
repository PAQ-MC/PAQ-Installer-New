package org.magetech.paq.launcher.repository;

import com.github.zafarkhaja.semver.Version;
import org.apache.commons.io.FilenameUtils;
import org.magetech.paq.Assert;
import org.magetech.paq.Out;
import org.magetech.paq.StreamUtils;
import org.magetech.paq.launcher.data.Repository;

import java.io.*;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by Aleksander on 06.12.13.
 */
public class WebRepository implements IRepository {
    private final URL _url;

    public WebRepository(URL url) {
        _url = url;
    }

    @Override
    public List<IPackage> getPackages() throws IOException {
        List<IPackage> packages = new ArrayList<IPackage>();
        try(InputStream in = _url.openStream()) {
            Repository r = Repository.load(in);
            for(Repository.RepositoryPackage packageConfig : r.getPackages()) {
                Out<IPackage> pack = new Out<IPackage>();
                if(WebPackage.load(_url, packageConfig, pack))
                    packages.add(pack.getValue());
            }
        }
        return packages;
    }

    static class WebPackage implements IPackage {
        private final String _id;
        private final List<Version> _versions;
        private final URL _url;

        public static boolean load(URL url, Repository.RepositoryPackage config, Out<IPackage> pack) {
            try {
                String id = config.getId();
                List<Version> versions = config.getVersions();
                pack.setValue(new WebPackage(url, id, versions));
                return true;
            }
            catch (ClassCastException e) {
                return false;
            }
        }

        private WebPackage(URL repositoryUrl, String id, List<Version> versions) {
            Assert.notNull(repositoryUrl, "repositoryUrl");
            Assert.notNull(id, "id");
            Assert.notNullItems(versions, "version");

            _id = id;
            _url = repositoryUrl;
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

            try(InputStream in = url.openStream()) {
                StreamUtils.saveTo(in, file);
            }
        }
    }
}
