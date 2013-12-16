package org.magetech.paq;

import java.io.Closeable;

/**
 * Created by Aleksander on 16.12.13.
 */
public interface IBackgroundReporter {
    void reset(int max);
    Closeable beginAction(String name);
    void end();
}
