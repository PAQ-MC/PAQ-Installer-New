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

/**
 * Created by Aleksander on 08.12.13.
 */
public class Repository {
    private final List<RepositoryPackage> _packages;

    private Repository(List<RepositoryPackage> packages) {
        _packages = packages;
    }

    public List<RepositoryPackage> getPackages() {
        return _packages;
    }

    private static Yaml getYaml() {
        YamlUtils.ChainConstructor constructor = new YamlUtils.ChainConstructor(RepositoryPackage.class);
        TypeDescription packageDescription = new TypeDescription(RepositoryPackage.class);
        packageDescription.putListPropertyType("versions", Version.class);
        constructor.addTypeDescription(packageDescription);

        constructor.addConstructor(Version.class, new YamlUtils.TConstruct<Version>() {
            @Override
            public boolean parse(ScalarNode node, Out<Version> result) {
                result.setValue(Version.valueOf(node.getValue()));
                return true;
            }
        });
        return new Yaml(constructor);
    }

    private static Repository load(Iterable<Object> allDocs) {
        ArrayList<RepositoryPackage> packages = new ArrayList<RepositoryPackage>();
        for(Object o : allDocs) {
            packages.add((RepositoryPackage)o);
        }
        return new Repository(packages);
    }

    public static Repository load(String in) {
        Yaml parser = getYaml();
        return load(parser.loadAll(in));
    }

    public static Repository load(InputStream in) {
        Yaml parser = getYaml();
        return load(parser.loadAll(in));
    }

    public static Repository empty() {
        return new Repository(new ArrayList<RepositoryPackage>(0));
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
        for(int i = 0; i < _packages.size(); i++) {
            if(i > 0) {
                sb.append("---\n");
            }
            RepositoryPackage p = _packages.get(i);
            sb.append("id: ").append(p._id).append("\n");
            sb.append("versions:\n");
            for(Version v : p.getVersions()) {
                sb.append("    - \"").append(v.toString()).append("\"\n");
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
