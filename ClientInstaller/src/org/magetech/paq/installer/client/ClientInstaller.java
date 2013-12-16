package org.magetech.paq.installer.client;

import org.magetech.paq.LogUtils;

import javax.swing.*;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

public class ClientInstaller {
    public static void main(String[] args) throws IOException, ClassNotFoundException, NoSuchMethodException, IllegalAccessException, InvocationTargetException, UnsupportedLookAndFeelException, InstantiationException {
        LogUtils.ensureConfigured();

        UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());

        InstallerWindow.run();
    }
}
