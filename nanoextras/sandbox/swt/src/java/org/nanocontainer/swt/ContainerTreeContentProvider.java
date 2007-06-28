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

import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;
import org.nanocontainer.guimodel.ContainerModel;
import org.picocontainer.MutablePicoContainer;

import java.util.Collection;

/**
 * Provides the data to be put inside a tree.
 *
 * @author Laurent Etiemble
 * @version $Revision$
 */
public class ContainerTreeContentProvider extends ContainerModel implements ITreeContentProvider {
    public ContainerTreeContentProvider(MutablePicoContainer pico) {
        super(pico);
    }

    public Object[] getChildren(Object parent) {
        return getAllChildren(parent);
    }

    public Object[] getElements(Object root) {
        Object[] result = null;
        if (root instanceof Collection) {
            result = ((Collection) root).toArray();
        }
        return result;
    }

    public Object getParent(Object child) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    public boolean hasChildren(Object node) {
        return getAllChildren(node).length > 0;
    }

    public void inputChanged(Viewer viewer, Object oldValue, Object newValue) {
        // Do nothing
    }

    public void dispose() {
        // Do nothing
    }
}
