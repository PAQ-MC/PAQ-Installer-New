package org.magetech.paq.installer.data;

import com.github.zafarkhaja.semver.Version;
import org.magetech.paq.Out;
import org.magetech.paq.YamlUtils;
import org.yaml.snakeyaml.TypeDescription;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.nodes.ScalarNode;

import java.io.InputStream;
import java.util.Collections;
import java.util.List;

/**
 * Created by Aleksander on 13.12.13.
 */
public class PackConfig {
    String _id;
    List<Version> _versions;

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

    private static Yaml getYaml() {
        YamlUtils.ChainConstructor constructor = new YamlUtils.ChainConstructor(PackConfig.class);

        TypeDescription packageDescription = new TypeDescription(PackConfig.class);
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

    private static PackConfig load(Object doc) {
        return (PackConfig)doc;
    }

    public static PackConfig load(String in) {
        Yaml parser = getYaml();
        return load(parser.load(in));
    }

    public static PackConfig load(InputStream in) {
        Yaml parser = getYaml();
        return load(parser.load(in));
    }

    public Version getVersion(String version) {
        Version wanted = Version.valueOf(version);

        for(int i = _versions.size() - 1; i >= 0; i--) {
            Version v = _versions.get(i);
            if(v.equals(wanted))
                return v;
        }

        throw new IllegalArgumentException("Version not found");
    }

    public Version getLatestVersion(boolean includePreRelease) {
        Collections.sort(_versions);

        for(int i = _versions.size() - 1; i >= 0; i--) {
            Version v = _versions.get(i);
            String preReleaseVersion = v.getPreReleaseVersion();
            boolean isPreRelease = preReleaseVersion == null || !preReleaseVersion.isEmpty();
            if(!isPreRelease || includePreRelease)
                return v;
        }
        return _versions.get(_versions.size() - 1); // if none is released, use last pre-release
    }
}
