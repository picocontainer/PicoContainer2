/*****************************************************************************
 * Copyright (C) NanoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the LICENSE.txt file.                                                     *
 *                                                                           *
 * Original code by                                                          *
 *****************************************************************************/
/*
 * Created by IntelliJ IDEA.
 * User: ahelleso
 * Date: 30-Jan-2004
 * Time: 01:51:26
 */
package org.nanocontainer.swing.action;

import org.nanocontainer.swing.ContainerTree;
import org.picocontainer.ComponentAdapter;
import org.picocontainer.MutablePicoContainer;

import java.awt.event.ActionEvent;

public class UnregisterComponentAction extends TreeSelectionAction {
    public UnregisterComponentAction(String iconPath, ContainerTree tree) {
        super("Unregister Component", iconPath, tree);
    }

    public void actionPerformed(ActionEvent e) {
        MutablePicoContainer parent = null;
        Object selectedKey = null;
        ComponentAdapter removed;
        if (selectedContainer != null && selectedAdapter == null) {
            parent = (MutablePicoContainer) selectedContainer.getParent();
            removed = parent.unregisterComponentByInstance(selectedContainer);
        } else {
            parent = selectedContainer;
            selectedKey = selectedAdapter.getComponentKey();
            removed = parent.unregisterComponent(selectedKey);
        }
        containerTreeModel.fire(parent, removed);
    }

    protected void setEnabled() {
        setEnabled(selected != containerTreeModel.getRoot() && selected != null);
    }
}