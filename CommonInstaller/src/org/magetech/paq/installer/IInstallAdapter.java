package org.magetech.paq.installer;

import org.magetech.paq.IBackgroundReporter;

import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

/**
 * Created by Aleksander on 13.12.13.
 */
public interface IInstallAdapter extends IBackgroundReporter {
    File downloadManually(String url, String fileName) throws IOException, InvocationTargetException, InterruptedException;
}
