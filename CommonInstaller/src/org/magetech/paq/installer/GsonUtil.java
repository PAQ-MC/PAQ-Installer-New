package org.magetech.paq.installer;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

/**
 * Created by Aleksander on 14.12.13.
 */
public class GsonUtil {
    public static Gson make() {
        return new GsonBuilder()
            .setPrettyPrinting()
            .disableHtmlEscaping()
            .create();
    }
}
