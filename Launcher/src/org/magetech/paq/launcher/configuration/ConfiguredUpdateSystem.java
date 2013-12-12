package org.magetech.paq.launcher.configuration;

import org.magetech.paq.configuration.Property;
import org.magetech.paq.configuration.PropertyLoader;
import org.magetech.paq.launcher.IUpdateSystem;
import org.magetech.paq.launcher.repository.RepositoryUpdateSystem;
import org.magetech.paq.launcher.repository.WebRepository;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Properties;

/**
 * Created by Aleksander on 06.12.13.
 */
public class ConfiguredUpdateSystem {
    public static IUpdateSystem createFromClassPath() throws IOException {
        return createFromClassPath("launcher.properties");
    }

    public static IUpdateSystem createFromClassPath(String name) throws IOException {
        return createFromProperties(PropertyLoader.load(name));
    }

    public static IUpdateSystem createFromProperties(Properties properties) throws MalformedURLException {
        String address = properties.getProperty(Property.LAUNCHER_APP_REPOSITORY, null);
        if(address == null)
            throw new IllegalStateException("Repository not specified in properties");

        return new RepositoryUpdateSystem(new WebRepository(new URL(address)));
    }
}
