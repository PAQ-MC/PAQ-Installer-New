package org.magetech.paq;

import org.magetech.paq.configuration.ConfiguredData;

import java.io.IOException;

/**
 * Created by Aleksander on 12.12.13.
 */
public class DirUtils {
    public static String getDataDir() throws IOException {
        return ConfiguredData.getDataDir();
    }
}
