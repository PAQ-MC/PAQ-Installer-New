package org.magetech.paq.launcher.configuration;

import com.sun.jna.Platform;
import org.apache.commons.io.FilenameUtils;
import org.magetech.paq.configuration.ConfiguredData;
import org.magetech.paq.configuration.Property;
import org.magetech.paq.configuration.PropertyLoader;
import org.magetech.paq.launcher.DefaultLaunchSystem;
import org.magetech.paq.launcher.ILaunchSystem;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.Properties;

/**
 * Created by Aleksander on 08.12.13.
 */
public class ConfiguredLaunchSystem {
    public static ILaunchSystem createFromClassPath() throws IOException {
        String paqDir = ConfiguredData.getDataDir(null);

        return new DefaultLaunchSystem(paqDir);
    }
}
