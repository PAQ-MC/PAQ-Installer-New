package org.magetech.paq.installer;

import org.magetech.paq.DialogBackgroundReporter;

import java.awt.*;

/**
 * Created by Aleksander on 16.12.13.
 */
public abstract class DialogInstallAdapter extends DialogBackgroundReporter implements  IInstallAdapter {
    public DialogInstallAdapter(Component parent, String title) {
        super(parent, title);
    }
}
