package org.magetech.paq;

import java.io.InputStream;

/**
 * Created by Aleksander on 16.12.13.
 */
public class ResourceUtils {
    public static InputStream getResourceAsStream(String name) {
        InputStream is = ContextUtils.get().getResourceAsStream(name);
        if(is == null)
            throw new IllegalStateException("resource " + name + " not found");
        return is;
    }
}
