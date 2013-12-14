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
    private final List<String> _packs;

    private PackRepository(List<String> packs) {
        _packs = packs;
    }

    public List<String> getPacks() {
        return _packs;
    }

    private static Yaml getYaml() {
        YamlUtils.ChainConstructor constructor = new YamlUtils.ChainConstructor(String.class);

        return new Yaml(constructor);
    }

    private static PackRepository load(Iterable<Object> allDocs) {
        ArrayList<String> mods = new ArrayList<String>();
        for(Object o : allDocs) {
            mods.add((String) o);
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
}
