package org.magetech.paq.launcher.configuration;

import com.github.zafarkhaja.semver.Version;
import org.magetech.paq.IBackgroundReporter;
import org.magetech.paq.configuration.ConfiguredData;
import org.magetech.paq.launcher.DefaultLaunchSystem;
import org.magetech.paq.launcher.ILaunchSystem;

import java.io.IOException;

public class ConfiguredLaunchSystem {
    public static ILaunchSystem createFromClassPath(IBackgroundReporter reporter, Version currentVersion) throws IOException {
        String paqDir = ConfiguredData.getDataDir(null);

        return new DefaultLaunchSystem(paqDir, currentVersion, reporter);
    }
}
