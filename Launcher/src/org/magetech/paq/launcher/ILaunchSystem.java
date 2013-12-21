package org.magetech.paq.launcher;

import com.github.zafarkhaja.semver.Version;
import org.magetech.paq.launcher.repository.IPackage;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.MalformedURLException;

/**
 * Created by Aleksander on 08.12.13.
 */
public interface ILaunchSystem {
    boolean hasInstalled(String appId, Version latestVersion);
    void installLatest(IPackage pack) throws IOException;
    void deleteAll(String appId) throws IOException;
    void launch(String appId, Version version, String[] args) throws IOException, ClassNotFoundException, NoSuchMethodException, InvocationTargetException, IllegalAccessException;

}
