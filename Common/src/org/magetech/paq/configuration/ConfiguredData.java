package org.magetech.paq.configuration;

import com.sun.jna.Platform;
import org.apache.commons.io.FilenameUtils;

import java.io.IOException;
import java.util.Properties;

/**
 * Created by Aleksander on 12.12.13.
 */
public class ConfiguredData {
    public static String getDataDir() throws IOException {
        return getDataDir(PropertyLoader.load("data.properties"));
    }

    private static String getDataDir(Properties properties) {
        String launcherId = properties.getProperty(Property.DATA_ID, null);
        String launcherDir = properties.getProperty(Property.DATA_DIR, null);

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

        return paqDir;
    }
}
