package org.magetech.paq;

import org.magetech.paq.configuration.ConfiguredData;

import java.io.IOException;

/**
 * Created by Aleksander on 12.12.13.
 */
public class DirUtils {
    public static String getBaseDataDir() throws IOException {
        return ConfiguredData.getBaseDataDir();
    }

    public static String getDataDir() throws IOException {
        return ConfiguredData.getDataDir(null);
    }

    public static String getDataDir(String subKey) throws IOException {
        return ConfiguredData.getDataDir(subKey);
    }
}
