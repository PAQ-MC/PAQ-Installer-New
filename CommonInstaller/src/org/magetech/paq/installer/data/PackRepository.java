package org.magetech.paq.installer.data;

import com.github.zafarkhaja.semver.Version;
import org.magetech.paq.Out;
import org.magetech.paq.YamlUtils;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.nodes.ScalarNode;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Aleksander on 12.12.13.
 */
public class PackRepository {
    private final List<PackConfig> _packs;

    private PackRepository(List<PackConfig> packs) {
        _packs = packs;
    }

    public List<PackConfig> getPacks() {
        return _packs;
    }

    public PackConfig getPack(String pack) {
        for(PackConfig conf : _packs) {
            if(conf.getId().equals(pack))
                return conf;
        }

        return null;
    }

    private static Yaml getYaml() {
        YamlUtils.ChainConstructor constructor = new YamlUtils.ChainConstructor(PackConfig.class);

        constructor.addConstructor(Version.class, new YamlUtils.TConstruct<Version>() {
            @Override
            public boolean parse(ScalarNode node, Out<Version> result) {
                result.setValue(Version.valueOf(node.getValue()));
                return true;
            }
        });

        return new Yaml(constructor);
    }

    private static PackRepository load(Iterable<Object> allDocs) {
        ArrayList<PackConfig> mods = new ArrayList<PackConfig>();
        for(Object o : allDocs) {
            mods.add((PackConfig) o);
        }
        return new PackRepository(mods);
    }

    public static PackRepository load(String in) {
        Yaml parser = getYaml();
        return load(parser.loadAll(in));
    }

    public static PackRepository load(InputStream in) {
        Yaml parser = getYaml();
        return load(parser.loadAll(in));
    }



    public static class PackConfig {
        private String _id;
        private Version _version;

        public String getId() {
            return _id;
        }

        public void setId(String value) {
            _id = value;
        }

        public Version getVersion() {
            return _version;
        }

        public void setVersion(Version value) {
            _version = value;
        }
    }
}
