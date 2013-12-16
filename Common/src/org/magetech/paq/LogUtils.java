package org.magetech.paq;

import org.pmw.tinylog.Configurator;
import org.pmw.tinylog.LoggingLevel;

import java.util.concurrent.atomic.AtomicBoolean;

public class LogUtils {
    private static AtomicBoolean _configured = new AtomicBoolean(false);

    public static void ensureConfigured() {
        if(_configured.compareAndSet(false, true)) {
            Configurator.defaultConfig()
                .formatPattern("[{date:yyyy-MM-dd HH:mm:ss}] {level}: {message}")
                .level(LoggingLevel.INFO)
                .activate();
        }
    }
}
