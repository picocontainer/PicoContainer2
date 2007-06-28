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
import org.picocontainer.MutablePicoContainer;
import org.picocontainer.defaults.DefaultPicoContainer;

/**
 * @author Laurent Etiemble
 * @version $Revision$
 */
public class TreeDemo {
    public static void main(String[] args) {
        // Creation of the dummy container
        MutablePicoContainer container1 = new DefaultPicoContainer();
        container1.registerComponent(WebServerImpl.class);
        container1.registerComponent(DefaultWebServerConfig.class);
        MutablePicoContainer container2 = new DefaultPicoContainer();
        container2.registerComponent(WebServerImpl.class);
        container2.registerComponent(DefaultWebServerConfig.class);
        container1.registerComponent(container2);

        // SWT in action
        Display display = new Display();
        Shell shell = new Shell(display);
        shell.setLayout(new GridLayout());

        ContainerTreeViewer viewer = new ContainerTreeViewer(shell, SWT.BORDER | SWT.SINGLE);
        viewer.setContainer(container1);

        shell.setSize(300, 200);
        shell.open();

        while (!shell.isDisposed()) {
            if (!display.readAndDispatch())
                display.sleep();
        }
        display.dispose();
    }
}
