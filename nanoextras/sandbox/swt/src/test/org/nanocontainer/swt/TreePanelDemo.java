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

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.nanocontainer.testmodel.DefaultWebServerConfig;
import org.nanocontainer.testmodel.WebServerImpl;
import org.picocontainer.defaults.DefaultPicoContainer;

/**
 * @author Laurent Etiemble
 * @version $Revision$
 */
public class TreePanelDemo {
    public static void main(String[] args) {
        // Creation of the dummy container
        DefaultPicoContainer container1 = new DefaultPicoContainer();
        DefaultPicoContainer container2 = new DefaultPicoContainer(container1);
        DefaultPicoContainer container3 = new DefaultPicoContainer(container2);

        container1.registerComponent(DefaultWebServerConfig.class);
        container1.registerComponent(WebServerImpl.class);
        container2.registerComponent(WebServerImpl.class);
        container3.registerComponent(WebServerImpl.class);

        // SWT in action
        Display display = new Display();
        final Shell shell = new Shell(display);
        shell.setLayout(new GridLayout());

        ContainerTreePanel panel = new ContainerTreePanel(shell, SWT.VERTICAL | SWT.NULL);
        panel.setContainer(container1);

        shell.setSize(400, 300);
        shell.open();

        while (!shell.isDisposed()) {
            if (!display.readAndDispatch())
                display.sleep();
        }
        display.dispose();
    }
}
