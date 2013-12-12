package org.magetech.paq.launcher;

import org.magetech.paq.launcher.repository.IPackage;

import java.io.IOException;

/**
 * Created by Aleksander on 06.12.13.
 */
public interface IUpdateSystem {
    IPackage findPackage(String appId) throws IOException;
}
