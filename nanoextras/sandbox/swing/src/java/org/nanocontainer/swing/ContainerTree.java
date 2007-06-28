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

import javax.swing.Icon;
import javax.swing.JTree;
import javax.swing.SwingUtilities;
import javax.swing.event.TreeModelEvent;
import javax.swing.event.TreeModelListener;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.TreePath;
import java.util.Arrays;

/**
 * Simple tree that takes a PicoContainer as root object.
 *
 * @author Laurent Etiemble
 * @version $Revision$
 */
public class ContainerTree extends JTree {

    public ContainerTree(ContainerModel containerModel, Icon componentIcon) {
        super(new ContainerTreeModel(containerModel));
        this.setRootVisible(true);
        this.setCellRenderer(new ContainerTreeCellRenderer(componentIcon));

        getModel().addTreeModelListener(new TreeModelListener() {
            public void treeNodesChanged(TreeModelEvent e) {

            }

            public void treeNodesInserted(final TreeModelEvent e) {
            }

            public void treeNodesRemoved(TreeModelEvent e) {

            }

            public void treeStructureChanged(final TreeModelEvent e) {
                SwingUtilities.invokeLater(new Runnable() {
                    public void run() {
                        final Object lastChild = e.getChildren()[e.getChildren().length - 1];
                        TreePath newPath = new TreePath(e.getTreePath().pathByAddingChild(lastChild));
                        setSelectionPath(newPath);
                    }
                });
            }
        });

        addTreeSelectionListener(new TreeSelectionListener() {
            public void valueChanged(TreeSelectionEvent e) {
                Object path = Arrays.asList(e.getPath().getPath());
            }
        });
    }
}
