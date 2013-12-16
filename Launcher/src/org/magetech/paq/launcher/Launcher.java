package org.magetech.paq.launcher;

import com.github.zafarkhaja.semver.Version;
import org.magetech.paq.ContextUtils;
import org.magetech.paq.DialogBackgroundReporter;
import org.magetech.paq.IBackgroundReporter;
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
        IBackgroundReporter reporter;

        if(args.length == 0) {
            // gui
            reporter = new DialogBackgroundReporter(null, "Downloading") {
            };
        } else {
            // console
            reporter = new IBackgroundReporter() {
                @Override
                public void reset(int max) {
                    // ignored in console
                }

                @Override
                public Closeable beginAction(final String name) {
                    Logger.info("Started " + name);

                    return new Closeable() {
                        @Override
                        public void close() throws IOException {
                            Logger.info("Ended " + name);
                        }
                    };
                }

                @Override
                public void end() {
                    // ignored in console
                }
            };
        }

        Logger.info("Loading list of latest versions");
        IConfigSystem configSystem = ConfiguredConfigSystem.createFromClassPath();
        IUpdateSystem updateSystem = ConfiguredUpdateSystem.createFromClassPath(reporter);
        ILaunchSystem launchSystem = ConfiguredLaunchSystem.createFromClassPath();

        String appId = configSystem.getAppId();
        IPackage pack = updateSystem.findPackage(appId);
        Version latestVersion = pack.getLastVersion();

        if(!launchSystem.hasInstalled(appId, latestVersion)) {
            launchSystem.installLatest(pack);
        }

        reporter.end();
        launchSystem.launch(appId, latestVersion, args);
    }
}
