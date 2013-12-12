package org.magetech.paq.installer.data;

import com.github.zafarkhaja.semver.Version;
import org.magetech.paq.Out;
import org.magetech.paq.YamlUtils;
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
    private final List<ModConfig> _mods;
    private final Version _version;

    private Pack(List<ModConfig> mods, Version version) {
        _mods = mods;
        _version = version;
    }

    public List<ModConfig> getMods() {
        return _mods;
    }

    public Version getVersion() {
        return _version;
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

    private static Pack load(Iterable<Object> allDocs, Version version) {
        ArrayList<ModConfig> mods = new ArrayList<ModConfig>();
        for(Object o : allDocs) {
            mods.add((ModConfig) o);
        }
        return new Pack(mods, version);
    }

    public static Pack load(String in) {
        Version version = getVersion(in);
        Yaml parser = getYaml();
        return load(parser.loadAll(in), version);
    }

    public static Pack load(InputStream in) throws IOException {
        Version version = getVersion(in);
        Yaml parser = getYaml();
        return load(parser.loadAll(in), version);
    }

    private static Version getVersion(String s) {
        Out<Version> version = new Out<Version>();
        String[] lines = s.split("\n");
        for(String line : lines) {
            if(getVersionFromLine(line, version))
                return version.getValue();
        }

        throw new IllegalStateException("Version-string not found");
    }

    private static Version getVersion(InputStream is) throws IOException {
        Out<Version> version = new Out<Version>();
        String line;
        byte[] buffer = new byte[1024];
        while(true) {
            int charIndex = 0;
            while(true) {
                byte c = (byte)is.read();

                if(c == '\n') // eol
                    break;

                if(charIndex < buffer.length)
                    buffer[charIndex++] = c;
            }

            line = new String(buffer, 0, charIndex, "utf-8");

            if(getVersionFromLine(line, version))
                return version.getValue();
        }
    }

    private static boolean getVersionFromLine(String line, Out<Version> version) {
        if(line.startsWith("#version: ")) {
            String versionString = line.substring("#version: ".length());
            version.setValue(Version.valueOf(versionString));
            return true;
        }

        return false;
    }

    public static String getVersionString(Version version) {
        return "#version: " + version.toString();
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
    }
}
