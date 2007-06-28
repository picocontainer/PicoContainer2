/*****************************************************************************
 * Copyright (C) NanoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the LICENSE.txt file.                                                     *
 *                                                                           *
 * Original code by                                                          *
 *****************************************************************************/
package org.nanocontainer.swt;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;
import org.picocontainer.PicoContainer;

/**
 * Provides texts and images for a tree full of PicoContainer.
 *
 * @author Laurent Etiemble
 * @version $Revision$
 */
public class ContainerTreeLabelProvider extends LabelProvider {
    private final Image picocontainer = ImageDescriptor.createFromFile(getClass(), "/picocontainer.gif").createImage();
    private final Image defaultComponentIcon = ImageDescriptor.createFromFile(getClass(), "/defaultcomponent.gif").createImage();

    /**
     * Default constructor
     */
    public ContainerTreeLabelProvider() {
        super();
    }

    /**
     * Returns an image according to the value passed.
     *
     * @param value
     * @return
     */
    public Image getImage(Object value) {
        Image result = null;

        if (value instanceof PicoContainer) {
            result = picocontainer;
        } else {
            result = defaultComponentIcon;
        }

        return result;
    }

    /**
     * Returns a text according to the value passed.
     *
     * @param value
     * @return
     */
    public String getText(Object value) {
        String result = null;

        if (value instanceof PicoContainer) {
            result = "PicoContainer";
        } else {
            result = value.toString();
        }

        return result;
    }
}
