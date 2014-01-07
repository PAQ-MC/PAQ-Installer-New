package org.magetech.paq.installer.client;

import joptsimple.OptionParser;
import joptsimple.OptionSet;
import org.magetech.paq.LogUtils;

import javax.swing.*;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

public class ClientInstaller {
    public static void main(String[] args) throws IOException, ClassNotFoundException, NoSuchMethodException, IllegalAccessException, InvocationTargetException, UnsupportedLookAndFeelException, InstantiationException {
        LogUtils.ensureConfigured();

        OptionParser parser = new OptionParser("m::v::p");
        OptionSet options = parser.parse(args);

        String mod = "PAQ";
        String version = null;
        boolean preview = false;

        if(options.has("m"))
            mod = (String)options.valueOf("m");
        if(options.has("v"))
            version = (String)options.valueOf("v");
        preview = options.has("p");

        UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());

        InstallerWindow.run(mod, version, preview);
    }
}
