package org.magetech.paq.launcher.configuration;

import com.github.zafarkhaja.semver.Version;
import com.sun.jna.Platform;
import org.apache.commons.io.FilenameUtils;
import org.magetech.paq.launcher.DefaultLaunchSystem;
import org.magetech.paq.launcher.ILaunchSystem;
import org.magetech.paq.launcher.IUpdateSystem;
import org.magetech.paq.launcher.repository.IRepository;
import org.magetech.paq.launcher.repository.RepositoryUpdateSystem;
import org.magetech.paq.launcher.repository.WebRepository;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Properties;

/**
 * Created by Aleksander on 08.12.13.
 */
public class ConfiguredLaunchSystem {
    public static ILaunchSystem createFromClassPath() throws IOException {
        return createFromClassPath("launcher.properties");
    }

    public static ILaunchSystem createFromClassPath(String name) throws IOException {
        return createFromProperties(PropertyLoader.load(name));
    }

    public static ILaunchSystem createFromProperties(Properties properties) throws MalformedURLException {
        IRepository repository;
        String launcherId = properties.getProperty(Property.LAUNCHER_ID, null);
        String launcherDir = properties.getProperty(Property.LAUNCHER_DIR, null);

        String paqDir = null;
        if(Platform.isWindows()) {
            paqDir = WinNative.getAppData();
            paqDir = FilenameUtils.concat(paqDir, launcherId);
        } else if(Platform.isMac()) {
            paqDir = FilenameUtils.concat(System.getProperty("user.home"), "Library/Application Support");
            paqDir = FilenameUtils.concat(paqDir, launcherId);
        }

        if(paqDir == null || paqDir.length() == 0) {
            paqDir = System.getProperty("user.home");
            paqDir = FilenameUtils.concat(paqDir, "." + launcherId);
        }

        paqDir = FilenameUtils.concat(paqDir, launcherDir);

        return new DefaultLaunchSystem(paqDir);
    }
}
