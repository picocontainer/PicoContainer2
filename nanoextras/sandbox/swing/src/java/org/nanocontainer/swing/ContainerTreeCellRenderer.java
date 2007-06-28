/*****************************************************************************
 * Copyright (C) NanoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the LICENSE.txt file.                                                     *
 *                                                                           *
 * Original code by                                                          *
 *****************************************************************************/
package org.nanocontainer.swing;

import org.picocontainer.ComponentAdapter;
import org.picocontainer.PicoContainer;
import org.picocontainer.defaults.UnsatisfiableDependenciesException;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JTree;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.TreeModel;
import java.awt.Color;
import java.awt.Component;
import java.awt.Image;
import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;

/**
 * Custom cell-renderer that makes tree nice.
 *
 * @author Laurent Etiemble
 * @version $Revision$
 */
public class ContainerTreeCellRenderer extends DefaultTreeCellRenderer {
    private final Icon picoContainerIcon = IconHelper.getIcon(IconHelper.PICO_CONTAINER_ICON, false);
    private final Icon componentIcon;

    public ContainerTreeCellRenderer(Icon componentIcon) {
        this.componentIcon = componentIcon;
    }

    public Component getTreeCellRendererComponent(JTree tree,
                                                  Object value,
                                                  boolean selected,
                                                  boolean expanded,
                                                  boolean leaf,
                                                  int row,
                                                  boolean hasFocus) {
        JLabel label = (JLabel) super.getTreeCellRendererComponent(tree, value, selected, expanded, leaf, row, hasFocus);

        if (value instanceof PicoContainer) {
            label.setText("PicoContainer");
            this.setIcon(picoContainerIcon);
        } else if (value instanceof ComponentAdapter) {
            ComponentAdapter componentAdapter = (ComponentAdapter) value;
            TreeModel model = tree.getModel();
            if (model.isLeaf(value)) {
                label.setText(componentAdapter.getComponentImplementation().getName());
//                try {
//                    componentAdapter.verify();
                    label.setForeground(Color.black);
//                } catch (UnsatisfiableDependenciesException e) {
//                    label.setForeground(Color.red);
//                }
            } else {
                label.setText(componentAdapter.getClass().getName());
            }
            this.setIcon(componentIcon);
        } else if (value instanceof Class) {
            Class clazz = (Class) value;
            label.setText(clazz.getName());
        } else {
            try {
                BeanInfo beanInfo = Introspector.getBeanInfo(value.getClass());
                Image image = beanInfo.getIcon(BeanInfo.ICON_COLOR_16x16);
                Icon icon;
                if (image != null) {
                    icon = new ImageIcon(image);
                } else {
                    icon = componentIcon;
                }
                this.setIcon(icon);
            } catch (IntrospectionException ie) {
                // Do nothing
            }
        }
        return label;
    }
}
