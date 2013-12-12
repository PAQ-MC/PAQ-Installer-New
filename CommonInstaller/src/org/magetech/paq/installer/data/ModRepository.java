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
public class ModRepository {
    private final List<ModConfig> _mods;

    private ModRepository(List<ModConfig> mods) {
        _mods = mods;
    }

    public List<ModConfig> getMods() {
        return _mods;
    }

    private static Yaml getYaml() {
        YamlUtils.ChainConstructor constructor = new YamlUtils.ChainConstructor(ModConfig.class);

        constructor.addConstructor(Version.class, new YamlUtils.TConstruct<Version>() {
            @Override
            public boolean parse(ScalarNode node, Out<Version> result) {
                result.setValue(Version.valueOf(node.getValue()));
                return true;
            }
        });

        return new Yaml(constructor);
    }

    private static ModRepository load(Iterable<Object> allDocs) {
        ArrayList<ModConfig> mods = new ArrayList<ModConfig>();
        for(Object o : allDocs) {
            mods.add((ModConfig) o);
        }
        return new ModRepository(mods);
    }

    public static ModRepository load(String in) {
        Yaml parser = getYaml();
        return load(parser.loadAll(in));
    }

    public static ModRepository load(InputStream in) {
        Yaml parser = getYaml();
        return load(parser.loadAll(in));
    }

    public ModConfig find(String id, Version version) {
        for(ModConfig conf : _mods) {
            if(conf.getId().equals(id) && conf.getVersion().equals(version))
                return conf;
        }

        return null;
    }

    public static class ModConfig {
        private String _id;
        private Version _version;
        private String _path;
        private boolean _browser;
        private String _fileName;

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

        public String getPath() {
            return _path;
        }

        public void setPath(String value) {
            _path = value;
        }

        public boolean getBrowser() {
            return _browser;
        }

        public void setBrowser(boolean value) {
            _browser = value;
        }

        public String getFileName() {
            return _fileName;
        }

        public void setFileName(String value) {
            _fileName = value;
        }
    }
}
