package org.magetech.paq.installer;

import org.magetech.paq.Launch;
import org.magetech.paq.NetUtils;
import org.magetech.paq.StreamUtils;
import org.magetech.paq.configuration.Property;
import org.magetech.paq.configuration.PropertyLoader;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;

/**
 * Created by Aleksander on 13.12.13.
 */
public class ForgeInstaller {
    public static void install(boolean isServer) throws IOException, ClassNotFoundException, NoSuchMethodException, IllegalAccessException, InvocationTargetException {
        String forgeUrl = NetUtils.downloadAsString(PropertyLoader.load("installer.properties").getProperty(Property.INSTALLER_FORGE_INFO_URL)).split("\n")[0];
        File tmp = File.createTempFile("forge", ".jar");
        tmp.deleteOnExit();
        try(InputStream is = new URL(forgeUrl).openStream()) {
            StreamUtils.saveTo(is, tmp);
        }

        String[] args;
        if(isServer)
            args = new String[] { "installServer" };
        else
            args = new String[0];

        Launch.jar(tmp, args);
    }
}
