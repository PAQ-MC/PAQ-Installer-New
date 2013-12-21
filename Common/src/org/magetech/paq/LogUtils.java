package org.magetech.paq;

import org.apache.commons.io.FilenameUtils;
import org.pmw.tinylog.Configurator;
import org.pmw.tinylog.LoggingLevel;
import org.pmw.tinylog.writers.ConsoleWriter;
import org.pmw.tinylog.writers.FileWriter;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicBoolean;

public class LogUtils {
    private static AtomicBoolean _configured = new AtomicBoolean(false);

    public static void ensureConfigured() throws IOException {
        if(_configured.compareAndSet(false, true)) {
            Configurator.defaultConfig()
                .formatPattern("[{date:yyyy-MM-dd HH:mm:ss}] {level}: {message}")
                .level(LoggingLevel.INFO)
                .writer(new ConsoleWriter())
                .writer(new FileWriter(FilenameUtils.concat(DirUtils.getDataDir(), "log.txt")))
                .activate();
        }
    }
}
