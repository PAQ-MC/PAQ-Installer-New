package org.magetech.paq.launcher;

import com.github.zafarkhaja.semver.Version;
import org.magetech.paq.launcher.repository.IPackage;

import java.io.IOException;

public interface IUpdateSystem {
    IPackage findPackage(String appId) throws IOException;
    void checkUpToDate(Version launcherVersion) throws IOException;
}
