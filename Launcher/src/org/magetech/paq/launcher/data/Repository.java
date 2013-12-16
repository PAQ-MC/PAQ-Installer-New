package org.magetech.paq.launcher.data;

import com.github.zafarkhaja.semver.Version;
import org.magetech.paq.Out;
import org.magetech.paq.YamlUtils;
import org.yaml.snakeyaml.TypeDescription;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.nodes.ScalarNode;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Repository {
    private Version _version;
    private List<RepositoryPackage> _packages;

    public Repository() {}
    public Repository(Version version) { setVersion(version); }

    public List<RepositoryPackage> getApps() {
        if(_packages == null)
            _packages = new ArrayList<RepositoryPackage>();

        return _packages;
    }

    public void setApps(List<RepositoryPackage> packages) {
        _packages = packages;
    }

    public Version getVersion() {
        return _version;
    }

    public void setVersion(Version value) {
        _version = value;
    }

    private static Yaml getYaml() {
        YamlUtils.ChainConstructor constructor = new YamlUtils.ChainConstructor(Repository.class);
        TypeDescription repositoryDescription = new TypeDescription(Repository.class);
        repositoryDescription.putListPropertyType("apps", RepositoryPackage.class);
        TypeDescription packageDescription = new TypeDescription(RepositoryPackage.class);
        packageDescription.putListPropertyType("versions", Version.class);
        constructor.addTypeDescription(packageDescription);
        constructor.addTypeDescription(repositoryDescription);

        constructor.addConstructor(Version.class, new YamlUtils.TConstruct<Version>() {
            @Override
            public boolean parse(ScalarNode node, Out<Version> result) {
                result.setValue(Version.valueOf(node.getValue()));
                return true;
            }
        });
        return new Yaml(constructor);
    }

    private static Repository load(Object repo) {
        return (Repository)repo;
    }

    public static Repository load(String in) {
        Yaml parser = getYaml();
        return load(parser.load(in));
    }

    public static Repository load(InputStream in) {
        Yaml parser = getYaml();
        return load(parser.load(in));
    }

    public static Repository empty(Version version) {
        return new Repository(version);
    }

    public RepositoryPackage add(String id) {
        RepositoryPackage pack = new RepositoryPackage();
        pack.setId(id);
        pack.setVersions(new ArrayList<Version>());
        _packages.add(pack);
        return pack;
    }

    public String dump() {
        StringBuilder sb = new StringBuilder();
        sb.append("version: ").append(getVersion().toString()).append("\n");
        sb.append("apps:\n");
        for(int i = 0; i < _packages.size(); i++) {
            RepositoryPackage p = _packages.get(i);
            sb.append("  -\n");
            sb.append("    id: ").append(p._id).append("\n");
            sb.append("    versions:\n");
            for(Version v : p.getVersions()) {
                sb.append("      - ").append(v.toString()).append("\n");
            }
        }
        return sb.toString();
    }

    public static class RepositoryPackage {
        private String _id;
        private List<Version> _versions;

        public String getId() {
            return _id;
        }

        public void setId(String value) {
            _id = value;
        }

        public List<Version> getVersions() {
            return _versions;
        }

        public void setVersions(List<Version> value) {
            _versions = value;
        }

        public void addVersion(Version version) {
            _versions.add(version);
            Collections.sort(_versions);
        }
    }
}
