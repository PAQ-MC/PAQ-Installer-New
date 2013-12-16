package org.magetech.paq.launcher;

import com.github.zafarkhaja.semver.Version;
import org.magetech.paq.ContextUtils;
import org.magetech.paq.launcher.configuration.ConfiguredConfigSystem;
import org.magetech.paq.launcher.configuration.ConfiguredLaunchSystem;
import org.magetech.paq.launcher.configuration.ConfiguredUpdateSystem;
import org.magetech.paq.launcher.data.Repository;
import org.magetech.paq.launcher.repository.IPackage;
import org.pmw.tinylog.Logger;
import org.xeustechnologies.jcl.JarClassLoader;
import org.xeustechnologies.jcl.context.DefaultContextLoader;
import org.yaml.snakeyaml.Yaml;

import javax.naming.Context;
import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.List;

/**
 * Created by Aleksander on 06.12.13.
 */
public class Launcher {
    public static void main(String[] args) throws IOException, ClassNotFoundException, NoSuchMethodException, IllegalAccessException, InvocationTargetException {
        Logger.info("Loading list of latest versions");
        IConfigSystem configSystem = ConfiguredConfigSystem.createFromClassPath();
        IUpdateSystem updateSystem = ConfiguredUpdateSystem.createFromClassPath();
        ILaunchSystem launchSystem = ConfiguredLaunchSystem.createFromClassPath();

        String appId = configSystem.getAppId();
        IPackage pack = updateSystem.findPackage(appId);
        Version latestVersion = pack.getLastVersion();

        if(!launchSystem.hasInstalled(appId, latestVersion)) {
            launchSystem.installLatest(pack);
        }

        launchSystem.launch(appId, latestVersion, args);
    }
}
