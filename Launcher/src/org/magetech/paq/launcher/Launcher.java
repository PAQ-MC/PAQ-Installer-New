package org.magetech.paq.launcher;

import com.github.zafarkhaja.semver.Version;

import org.magetech.paq.ContextUtils;
import org.magetech.paq.DialogBackgroundReporter;
import org.magetech.paq.IBackgroundReporter;
import org.magetech.paq.LogUtils;
import org.magetech.paq.launcher.configuration.ConfiguredConfigSystem;
import org.magetech.paq.launcher.configuration.ConfiguredLaunchSystem;
import org.magetech.paq.launcher.configuration.ConfiguredUpdateSystem;
import org.magetech.paq.launcher.repository.IPackage;
import org.pmw.tinylog.Logger;

import java.io.Closeable;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Enumeration;
import java.util.Map;
import java.util.jar.Attributes;
import java.util.jar.Manifest;

/**
 * Created by Aleksander on 06.12.13.
 */
public class Launcher {
    public static void main(String[] args) throws IOException, ClassNotFoundException, NoSuchMethodException, IllegalAccessException, InvocationTargetException {
        LogUtils.ensureConfigured();
        
        //temp patch code to create log file
        DateFormat dateFormat = new SimpleDateFormat("MM-dd-yyyy");
		Date date = new Date();
        PrintWriter out = new PrintWriter(new FileWriter("log.txt"),true);
        out.print(dateFormat.format(date));
        out.close();
        
        
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

                @Override
                public void warn(String message, String title) {
                    System.out.println(message);
                }

                @Override
                public void error(String message, String title) {
                    System.err.println(message);
                }
            };
        }

        Version launcherVersion = null;
        Enumeration<URL> resources = ContextUtils.get().getResources("META-INF/MANIFEST.MF");

        manifests:
        while(resources.hasMoreElements()) {
            URL resource = resources.nextElement();
            try(InputStream is = resource.openStream()) {
                Manifest mf = new Manifest(is);

                Attributes main = mf.getMainAttributes();
                String versionString = get(main, "Launcher-Version");
                if(versionString != null) {
                    launcherVersion = Version.valueOf(versionString);
                    break manifests;
                }
            }
        }

        if(launcherVersion == null)
        {
            reporter.error("No launcher version found", "Invalid launcher");
            System.exit(1);
        }


        IConfigSystem configSystem = ConfiguredConfigSystem.createFromClassPath();
        IUpdateSystem updateSystem = ConfiguredUpdateSystem.createFromClassPath(reporter);
        ILaunchSystem launchSystem = ConfiguredLaunchSystem.createFromClassPath(reporter, launcherVersion);

        updateSystem.checkUpToDate(launcherVersion);

        String appId = configSystem.getAppId();
        IPackage pack = updateSystem.findPackage(appId);
        Version latestVersion = pack.getLastVersion();

        if(!launchSystem.hasInstalled(appId, latestVersion)) {
            launchSystem.deleteAll(appId);
            launchSystem.installLatest(pack);
        }

        reporter.end();
        launchSystem.launch(appId, latestVersion, args);
    }

    private static String get(Attributes attributes, String wanted) {
        String val = attributes.getValue(wanted);
        if(val != null)
            return val;

        for(Object key : attributes.keySet()) {
            if(key != null && key.equals(wanted))
                return convertString(attributes.get(key));
        }
        return null;
    }

    private static void dump(Manifest mf) {
        System.out.println("-----");
        dump("$", mf.getMainAttributes());
        Map<String, Attributes> set = mf.getEntries();
        for(String k : set.keySet())
            dump(k, set.get(k));
    }

    private static void dump(String name, Attributes attributes) {
        System.out.println("===" + name + "===");
        for(Object key : attributes.keySet()) {
            dump(key);
            System.out.print(": ");
            dump(attributes.get(key));
            System.out.println();
        }
    }

    private static void dump(Object obj) {
        if(obj == null)
            System.out.print("<null>");
        else
            System.out.print(obj.toString());
    }

    private static String convertString(Object obj) {
        if(obj == null)
            return null;

        return obj.toString();
    }
}
