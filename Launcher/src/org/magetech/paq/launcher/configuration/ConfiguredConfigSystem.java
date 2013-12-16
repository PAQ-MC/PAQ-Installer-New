package org.magetech.paq.launcher.configuration;

import org.magetech.paq.Assert;
import org.magetech.paq.IBackgroundReporter;
import org.magetech.paq.configuration.Property;
import org.magetech.paq.configuration.PropertyLoader;
import org.magetech.paq.launcher.IConfigSystem;
import org.magetech.paq.launcher.repository.IRepository;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.Properties;

/**
 * Created by Aleksander on 09.12.13.
 */
public class ConfiguredConfigSystem implements IConfigSystem {
    private final String _id;

    private ConfiguredConfigSystem(String id) {
        Assert.notNull(id, "id");

        _id = id;
    }

    public String getAppId() {
        return _id;
    }

    public static IConfigSystem createFromClassPath() throws IOException {
        return createFromClassPath("launcher.properties");
    }

    public static IConfigSystem createFromClassPath(String name) throws IOException {
        return createFromProperties(PropertyLoader.load(name));
    }

    public static IConfigSystem createFromProperties(Properties properties) throws MalformedURLException {
        IRepository repository;
        String appId = properties.getProperty(Property.LAUNCHER_APP_ID, null);

        return new ConfiguredConfigSystem(appId);
    }
}
