package org.magetech.paq;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Properties;

/**
 * Created by Aleksander on 13.12.13.
 */
public class Launch {
    public static void jar(File jarFile, String[] args) throws MalformedURLException, ClassNotFoundException, NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        URLClassLoader child = new URLClassLoader(new URL[] {jarFile.toURL()}, Launch.class.getClassLoader());
        String mainClassName = null;
        try(InputStream is = child.getResourceAsStream("META-INF/MANIFEST.MF")) {
            Properties p = new Properties();
            p.load(is);
            mainClassName = p.getProperty("Main-Class");
        } catch (IOException e) {
            e.printStackTrace();
        }

        if(mainClassName == null)
            throw new IllegalStateException("No main class found in manifest");

        Class<?> mainClass = Class.forName(mainClassName, true, child);
        Method method = mainClass.getDeclaredMethod("main", String[].class);
        method.invoke(null);
    }
}
