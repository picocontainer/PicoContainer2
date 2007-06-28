/*****************************************************************************
 * Copyright (C) NanoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the LICENSE.txt file.                                                     *
 *                                                                           *
 * Original code by                                                          *
 *****************************************************************************/
package org.nanocontainer.piccolo;

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
public class ViewPanelDemo {
    public static void main(String[] args) {
/*
        1
          4
          2
            3
              5
                6
        */
        DefaultPicoContainer container1 = new DefaultPicoContainer();
        DefaultPicoContainer container2 = new DefaultPicoContainer(container1);
        DefaultPicoContainer container3 = new DefaultPicoContainer(container2);
        DefaultPicoContainer container4 = new DefaultPicoContainer(container1);
        DefaultPicoContainer container5 = new DefaultPicoContainer(container3);
        DefaultPicoContainer container6 = new DefaultPicoContainer(container5);

        container1.registerComponent(DefaultWebServerConfig.class);
        container1.registerComponent(WebServerImpl.class);
        container2.registerComponent(WebServerImpl.class);
        container3.registerComponent(WebServerImpl.class);
        container4.registerComponent(DefaultWebServerConfig.class);
        container5.registerComponent(DefaultWebServerConfig.class);
        container5.registerComponent(WebServerImpl.class);
        container6.registerComponent(WebServerImpl.class);

        // Piccolo in action
        ContainerViewPanel panel = new ContainerViewPanel(container1);

        JFrame frame = new JFrame();
        frame.getContentPane().add(panel, BorderLayout.CENTER);
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
