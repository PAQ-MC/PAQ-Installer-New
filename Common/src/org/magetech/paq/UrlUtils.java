package org.magetech.paq;

/**
 * Created by Aleksander on 12.12.13.
 */
public class UrlUtils {
    public static String relativeTo(String path, String relative) {
        String base = path;
        if(!path.endsWith("/")) {
            int indexOfLastSeparator = path.lastIndexOf('/');
            if(indexOfLastSeparator == -1) {
                base = path + "/";
            } else {
                base = path.substring(0, indexOfLastSeparator + 1);
            }
        }

        if(relative.startsWith("/")) {
            relative = relative.substring(1);
        }

        return base + relative;
    }
}
