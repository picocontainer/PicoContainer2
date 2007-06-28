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

import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;
import org.nanocontainer.guimodel.BeanProperty;

/**
 * Provides texts and images for BeanProperty objects.
 *
 * @author Laurent Etiemble
 * @version $Revision$
 */
public class BeanLabelProvider extends LabelProvider implements ITableLabelProvider {
    /**
     * Default constructor
     */
    public BeanLabelProvider() {
        super();
    }

    /**
     * Returns an image according to the value passed and the column.
     *
     * @param object
     * @param column
     * @return
     */
    public Image getColumnImage(Object object, int column) {
        return null;
    }

    /**
     * Returns a text according to the value passed and the column.
     *
     * @param object
     * @param column
     * @return
     */
    public String getColumnText(Object object, int column) {
        String text = "";
        if (object instanceof BeanProperty) {
            BeanProperty bp = (BeanProperty) object;
            switch (column) {
                case 0:
                    text = bp.getName();
                    break;
                case 1:
                    Object o = bp.getValue();
                    if (o != null) {
                        text = o.toString();
                    }
                    break;
                default :
            }
        }
        return text;
    }
}
