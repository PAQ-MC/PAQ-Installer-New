package org.magetech.paq.launcher.repository;

import com.github.zafarkhaja.semver.Version;

import java.io.IOException;
import java.util.List;

public interface IRepository {
    List<IPackage> getPackages() throws IOException;

    void checkUpToDate(Version runningVersion) throws IOException;
}
