package org.magetech.paq.installer.client;

import org.magetech.paq.installer.Installer;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

/**
 * Created by Aleksander on 12.12.13.
 */
public class ClientInstaller {
    public static void main(String[] args) throws IOException, ClassNotFoundException, NoSuchMethodException, IllegalAccessException, InvocationTargetException {
        Installer installer = new Installer();
        installer.install("PAQ", false);
    }
}
