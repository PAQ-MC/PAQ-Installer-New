package org.magetech.paq.configuration;

import org.magetech.paq.ContextUtils;
import org.magetech.paq.Out;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Created by Aleksander on 06.12.13.
 */
public class PropertyLoader {
    public static Properties load(String name) throws IOException {
        Out<Properties> properties = new Out<Properties>();
        if(getPropertiesFromClassPath(ContextUtils.get(), name, properties))
            return properties.getValue();

        throw new IllegalStateException("properties not found");
    }

    private static boolean getPropertiesFromClassPath(ClassLoader loader, String name, Out<Properties> properties) throws IOException {
        try(InputStream stream = getStreamFromClassPath(loader, name)) {
            if(stream != null) {
                properties.setValue(getPropertiesFromStream(stream));
                return true;
            }
        }
        return false;
    }

    private static Properties getPropertiesFromStream(InputStream stream) throws IOException {
        Properties result = new Properties();
        result.load(stream);
        return result;
    }

    private static InputStream getStreamFromClassPath(ClassLoader loader, String name) {
        return loader.getResourceAsStream(name);
    }
}
