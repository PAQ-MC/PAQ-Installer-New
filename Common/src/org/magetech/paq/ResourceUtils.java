package org.magetech.paq;

import java.io.InputStream;

/**
 * Created by Aleksander on 16.12.13.
 */
public class ResourceUtils {
    public static InputStream getResourceAsStream(String name) {
        return ContextUtils.get().getResourceAsStream(name);
    }
}
