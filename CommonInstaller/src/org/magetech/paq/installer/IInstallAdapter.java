package org.magetech.paq.installer;

import java.io.File;
import java.io.IOException;

/**
 * Created by Aleksander on 13.12.13.
 */
public interface IInstallAdapter {
    File downloadManually(String url, String fileName) throws IOException;
}
