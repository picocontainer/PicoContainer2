package org.nanocontainer.swing;

import javax.swing.GrayFilter;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Aslak Helles&oslash;y
 * @version $Revision$
 */
public class IconHelper {
    public static final String DEFAULT_COMPONENT_ICON = "/org/nanocontainer/swing/icons/defaultcomponent.gif";
    public static final String PICO_CONTAINER_ICON = "/org/nanocontainer/swing/icons/picocontainer.gif";

    private static Map images = new HashMap();

    public static Icon getIcon(String path, boolean gray) {
        ImageIcon icon = (ImageIcon) images.get(path);
        if (icon == null) {
            URL url = IconHelper.class.getResource(path);
            if (url == null) {
                System.err.println("PicoContainer GUI: Couldn't load resource: " + path);
                return null;
            }
            icon = new ImageIcon(url);
            images.put(path, icon);
        }
        if (gray) {
            icon = new ImageIcon(GrayFilter.createDisabledImage(icon.getImage()));
        }
        return icon;
    }
}