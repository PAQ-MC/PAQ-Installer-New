package org.magetech.paq.installer.client;

import org.magetech.paq.ResourceUtils;

import javax.imageio.ImageIO;
import java.awt.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;

/**
 * Created by Aleksander on 16.12.13.
 */
public class Images {
    public static final String BACKGROUND = "PAQ_INSTALLER_BG_1.png";
    public static final String INSTALL = "PAQ_INSTALL_BUTTON_1.png";
    public static final String INSTALL_HOVER = "PAQ_INSTALL_BUTTON_1_HOVER.png";
    public static final String INSTALL_PRESS = "PAQ_INSTALL_BUTTON_1_PRESS.png";
    public static final String EXIT = "PAQ_EXIT_BUTTON_1.png";
    public static final String EXIT_HOVER = "PAQ_EXIT_BUTTON_1_HOVER.png";
    public static final String EXIT_PRESS = "PAQ_EXIT_BUTTON_1_PRESS.png";

    public static final String ICON16 = "PAQ_ICON_16.png";
    public static final String ICON32 = "PAQ_ICON_32.png";
    public static final String ICON64 = "PAQ_ICON_64.png";
    public static final String ICON128 = "PAQ_ICON_128.png";
    public static final String ICON256 = "PAQ_ICON_256.png";
    public static final String ICON512 = "PAQ_ICON_512.png";

    public static java.util.List<Image> getIcons() throws IOException {
        ArrayList<Image> icons = new ArrayList<Image>();
        for(String str : new String[] { ICON16, ICON32, ICON64, ICON128, ICON256, ICON512 }) {
            icons.add(ImageIO.read(ResourceUtils.getResourceAsStream(str)));
        }
        return Collections.unmodifiableList(icons);
    }
}
