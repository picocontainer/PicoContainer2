/*****************************************************************************
 * Copyright (C) NanoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the LICENSE.txt file.                                                     *
 *                                                                           *
 * Original code by                                                          *
 *****************************************************************************/
/**
 * @author Aslak Helles&oslash;y
 * @version $Revision$
 */
package org.nanocontainer.swing.action;

import org.nanocontainer.swing.ContainerTree;
import org.picocontainer.ComponentAdapter;

import javax.swing.JOptionPane;
import java.awt.event.ActionEvent;

public class RegisterComponentAction extends TreeSelectionAction {
    private final ClassLoader componentClassLoader;

    private int i;

    public RegisterComponentAction(String iconPath, ContainerTree tree, ClassLoader componentClassLoader) {
        super("Register Component", iconPath, tree);
        this.componentClassLoader = componentClassLoader;
    }

    public void actionPerformed(ActionEvent evt) {
        String className = (String) JOptionPane.showInputDialog(null, "Component Implementation", "Register Component", JOptionPane.OK_CANCEL_OPTION, null, null, null);
        if (className != null) {
            try {
                Class componentImplementation = componentClassLoader.loadClass(className);
                ComponentAdapter ca = selectedContainer.registerComponent("" + i++, componentImplementation);
                containerTreeModel.fire(selectedContainer, ca);
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(null, "Component registration failed with " + e.getClass().getName() + ": " + e.getMessage(), null, JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    protected void setEnabled() {
        setEnabled(selectedContainer != null && selectedAdapter == null);
    }
}