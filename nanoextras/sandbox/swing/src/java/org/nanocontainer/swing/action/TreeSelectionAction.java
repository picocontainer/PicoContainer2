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
import org.nanocontainer.swing.ContainerTreeModel;
import org.nanocontainer.swing.IconHelper;
import org.picocontainer.ComponentAdapter;
import org.picocontainer.MutablePicoContainer;

import javax.swing.AbstractAction;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.TreePath;

/**
 * Action that listens to tree selections and enables/disables itself accordingly.
 *
 * @author Aslak Helles&oslash;y
 * @version $Revision$
 */
public abstract class TreeSelectionAction extends AbstractAction {
    protected final ContainerTreeModel containerTreeModel;

    protected MutablePicoContainer selectedContainer;
    protected ComponentAdapter selectedAdapter;
    protected Object selected;

    protected TreeSelectionAction(String name, String imagePath, ContainerTree tree) {
        super(name, IconHelper.getIcon(imagePath, false));
        containerTreeModel = (ContainerTreeModel) tree.getModel();
        tree.addTreeSelectionListener(new TreeSelectionListener() {
            public void valueChanged(TreeSelectionEvent e) {
                TreePath treePath = e.getPath();
                selected = treePath.getLastPathComponent();
                if (selected instanceof MutablePicoContainer) {
                    selectedContainer = (MutablePicoContainer) selected;
                    selectedAdapter = null;
                } else {
                    selectedAdapter = (ComponentAdapter) selected;
                    selectedContainer = null;
                    for(int i = treePath.getPathCount(); --i >= 0; ) {
                        final Object element = treePath.getPathComponent(i);
                        if (element instanceof MutablePicoContainer) {
                            selectedContainer = (MutablePicoContainer)element;
                        }
                    }
                }
                setEnabled();
            }
        });
        setEnabled();
    }

    protected abstract void setEnabled();
}