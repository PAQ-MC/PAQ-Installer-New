package org.magetech.paq.installer.client;

import org.magetech.paq.installer.IInstallAdapter;
import org.magetech.paq.installer.Installer;

import javax.swing.*;
import javax.swing.filechooser.FileFilter;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.URI;
import java.util.*;
import java.util.List;

/**
 * Created by Aleksander on 12.12.13.
 */
public class ClientInstaller {
    public static void main(String[] args) throws IOException, ClassNotFoundException, NoSuchMethodException, IllegalAccessException, InvocationTargetException, UnsupportedLookAndFeelException, InstantiationException {
        UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());

        Installer installer = new Installer(new IInstallAdapter() {
            @Override
            public File downloadManually(String url, String fileName) throws IOException {
                return manualDownload(url, fileName);
            }
        });

        installer.install("PAQ", false);
    }

    private static File manualDownload(final String url, final String fileName) throws IOException {
        while(true) {
            Desktop.getDesktop().browse(URI.create(url));
            JFileChooser c = new JFileChooser();
            for(FileFilter f : c.getChoosableFileFilters())
                c.removeChoosableFileFilter(f);

            c.addChoosableFileFilter(new FileFilter() {
                @Override
                public boolean accept(File f) {
                    return f.isDirectory() || f.getPath().endsWith(fileName);
                }

                @Override
                public String getDescription() {
                    return fileName;
                }
            });

            //c.setSelectedFile(new File(fileName));
            c.setDialogTitle("Select downloaded file");
            c.grabFocus();
            int retVal = c.showOpenDialog(null);
            if(retVal == JFileChooser.APPROVE_OPTION) {
                File f = c.getSelectedFile();
                if(f.exists())
                    return f;
                else
                    JOptionPane.showMessageDialog(null, "Non-existing file selected. Retrying.");
            }
        }
    }
}
