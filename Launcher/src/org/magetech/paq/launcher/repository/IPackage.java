package org.magetech.paq.launcher.repository;

import com.github.zafarkhaja.semver.Version;

import java.io.File;
import java.io.IOException;

/**
 * Created by Aleksander on 06.12.13.
 */
public interface IPackage {
    String getId();
    Version getLastVersion();
    Version getLastVersion(boolean includePreRelease);
    void copyTo(Version latest, File tmp) throws IOException;
}
