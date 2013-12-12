package org.magetech.paq.installer.client;

import org.magetech.paq.installer.Installer;

import java.io.IOException;

/**
 * Created by Aleksander on 12.12.13.
 */
public class ClientInstaller {
    public static void main(String[] args) throws IOException {
        Installer installer = new Installer();
        installer.install("PAQ");
    }
}
