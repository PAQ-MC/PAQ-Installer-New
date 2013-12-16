package org.magetech.paq;

import org.pmw.tinylog.Logger;

import javax.swing.*;
import java.awt.*;
import java.io.Closeable;
import java.io.IOException;

public abstract class DialogBackgroundReporter implements IBackgroundReporter {
    private final ProgressMonitor _monitor;
    private final Component _parent;
    private int _progress;

    public DialogBackgroundReporter(Component parent, String title) {
        _monitor = new ProgressMonitor(parent, title, null, 0, 0);
        _monitor.setMillisToPopup(0);
        _monitor.setMillisToDecideToPopup(0);
        _parent = parent;
    }

    @Override
    public void reset(int max) {
        _monitor.setProgress(0);
        _monitor.setMaximum(max);
        _monitor.setNote(null);
        _progress = 0;
    }

    @Override
    public Closeable beginAction(final String name) {
        Logger.info("Started " + name);
        _monitor.setNote(name);

        return new Closeable() {
            @Override
            public void close() throws IOException {
                Logger.info("Ended " + name);
                _monitor.setProgress(++_progress);
            }
        };
    }

    @Override
    public void end() {
        Logger.debug("DialogBackgroundReporter: end");
        _monitor.close();
    }

    @Override
    public void warn(String message, String title) {
        Logger.warn(message);
        JOptionPane.showMessageDialog(_parent, message, title, JOptionPane.WARNING_MESSAGE);
    }

    @Override
    public void error(String message, String title) {
        Logger.error(message);
        JOptionPane.showMessageDialog(_parent, message, title, JOptionPane.ERROR_MESSAGE);
    }
}
