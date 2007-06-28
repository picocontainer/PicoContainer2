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

import org.nanocontainer.guimodel.ContainerModel;
import org.nanocontainer.testmodel.DefaultWebServerConfig;
import org.nanocontainer.testmodel.WebServerImpl;
import org.picocontainer.defaults.DefaultPicoContainer;

import javax.swing.JFrame;
import java.awt.BorderLayout;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

/**
 * @author Laurent Etiemble
 * @version $Revision$
 */
public class TreeDemo {
    public static void main(String[] args) {
        // Creation of the dummy container
        DefaultPicoContainer container1 = new DefaultPicoContainer();
        DefaultPicoContainer container2 = new DefaultPicoContainer(container1);
        DefaultPicoContainer container3 = new DefaultPicoContainer(container2);

        container1.registerComponent(DefaultWebServerConfig.class);
        container1.registerComponent(WebServerImpl.class);
        container2.registerComponent(WebServerImpl.class);
        container3.registerComponent(WebServerImpl.class);

        // Swing in action
        ContainerTree tree = new ContainerTree(new ContainerModel(container1), IconHelper.getIcon(IconHelper.DEFAULT_COMPONENT_ICON, false));

        JFrame frame = new JFrame();
        frame.getContentPane().add(tree, BorderLayout.CENTER);
        frame.pack();
        frame.show();
        frame.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                super.windowClosing(e);
                System.exit(0);
            }
        });
    }
}
