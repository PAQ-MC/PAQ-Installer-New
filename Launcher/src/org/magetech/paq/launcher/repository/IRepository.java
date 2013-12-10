package org.magetech.paq.launcher.repository;

import java.io.IOException;
import java.util.List;

/**
 * Created by Aleksander on 06.12.13.
 */
public interface IRepository {
    List<IPackage> getPackages() throws IOException;
}
