package org.magetech.paq.installer.data;

import com.github.zafarkhaja.semver.Version;
import org.magetech.paq.Out;
import org.magetech.paq.YamlUtils;
import org.yaml.snakeyaml.TypeDescription;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.nodes.ScalarNode;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Aleksander on 12.12.13.
 */
public class Pack {
    private String _id;
    private String _configUrl;
    private String _forgeUrl;
    private List<ModConfig> _mods;
    private Version _version;

    public Pack() {
    }

    public String getId() {
        return _id;
    }

    public void setId(String value) {
        _id = value;
    }

    public List<ModConfig> getMods() {
        return _mods;
    }

    public void setMods(List<ModConfig> value) {
        _mods = value;
    }

    public Version getVersion() {
        return _version;
    }

    public void setVersion(Version value) {
        _version = value;
    }

    public String getConfig() {
        return _configUrl;
    }

    public void setConfig(String value) {
        _configUrl = value;
    }

    public String getForge() {
        return _forgeUrl;
    }

    public void setForge(String value) {
        _forgeUrl = value;
    }

    private static Yaml getYaml() {
        YamlUtils.ChainConstructor constructor = new YamlUtils.ChainConstructor(Pack.class);

        TypeDescription packageDescription = new TypeDescription(Pack.class);
        packageDescription.putListPropertyType("mods", ModConfig.class);
        constructor.addTypeDescription(packageDescription);

        constructor.addConstructor(Version.class, new YamlUtils.TConstruct<Version>() {
            @Override
            public boolean parse(ScalarNode node, Out<Version> result) {
                result.setValue(Version.valueOf(node.getValue()));
                return true;
            }
        });

        constructor.addConstructor(ModConfig.class, new YamlUtils.TConstruct<ModConfig>() {
            @Override
            public boolean parse(ScalarNode node, Out<ModConfig> result) {
                result.setValue(ModConfig.parse(node.getValue()));
                return true;
            }
        });

        return new Yaml(constructor);
    }

    private static Pack load(Object docs) {
        return (Pack)docs;
    }

    public static Pack load(String in) {
        Yaml parser = getYaml();
        return load(parser.load(in));
    }

    public static Pack load(InputStream in) throws IOException {
        Yaml parser = getYaml();
        return load(parser.load(in));
    }

    public static class ModConfig {
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

        public static ModConfig parse(String value) {
            int indexOfLastAt = value.lastIndexOf('@');
            String id = value.substring(0, indexOfLastAt);
            String versionString = value.substring(indexOfLastAt + 1);

            Version version = Version.valueOf(versionString);

            ModConfig conf = new ModConfig();
            conf.setId(id);
            conf.setVersion(version);
            return conf;
        }
    }
}
