package org.magetech.paq.launcher;

import com.github.zafarkhaja.semver.Version;
import org.apache.commons.io.FilenameUtils;
import org.magetech.paq.Assert;
import org.magetech.paq.launcher.repository.IPackage;

import javax.swing.*;
import javax.swing.filechooser.FileFilter;
import java.io.File;
import java.io.IOException;

/**
 * Created by Aleksander on 06.12.13.
 */
public class Updater implements IUpdater {
    private final String _id;
    private final Version _version;
    private final IPackage _package;

    public Updater(IPackage pack, String id, Version currentVersion) {
        Assert.notNull(pack, "pack");
        Assert.notNull(id, "id");
        Assert.notNull(currentVersion, "currentVersion");

        _package = pack;
        _id = id;
        _version = currentVersion;
    }

    public void updateToLatestVersion() throws IOException {
        Version latest = _package.getLastVersion();
        if(latest.greaterThan(_version)) {
            // There is a new version available
            JFileChooser c = new JFileChooser();
            for(FileFilter f : c.getChoosableFileFilters())
                c.removeChoosableFileFilter(f);

            c.addChoosableFileFilter(new FileFilter() {
                @Override
                public boolean accept(File f) {
                    return f.isDirectory() || FilenameUtils.getExtension(f.getPath()).equals("jar");
                }

                @Override
                public String getDescription() {
                    return "Java Archive";
                }
            });
            c.setSelectedFile(new File("install.jar"));
            JOptionPane.showMessageDialog(null, "A new version of the installer is available. Please select download location.");
            int rVal = c.showSaveDialog(null);
            if(rVal == JFileChooser.APPROVE_OPTION) {
                File f = c.getSelectedFile();
                if(!FilenameUtils.getExtension(f.getPath()).equals("jar"))
                    f = new File(f.getPath() + ".jar");
                System.out.println(">> Downloading new installer.");
                _package.copyTo(latest, c.getSelectedFile());
                JOptionPane.showMessageDialog(null, "New installer has been downloaded. Closing application.");

            }
            System.exit(0);
        }
    }
}
