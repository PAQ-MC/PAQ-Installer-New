package org.magetech.paq;

import java.io.Closeable;

public interface IBackgroundReporter {
    void reset(int max);
    Closeable beginAction(String name);
    void end();

    void warn(String message, String title);
    void error(String message, String title);
}
