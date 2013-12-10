package org.magetech.paq;


import java.util.List;

/**
 * Created by Aleksander on 06.12.13.
 */
public class Assert {
    public static void notNull(Object object, String name) {
        if(object == null)
            throw new IllegalArgumentException("Parameter " + name + " cannot be null");
    }

    public static void notNullItems(List<?> iterable, String name) {
        Assert.notNull(iterable, name);

        for(Object o : iterable)
            if(o == null)
                throw new IllegalArgumentException("Parameter " + name + " cannot have null items");
    }
}
