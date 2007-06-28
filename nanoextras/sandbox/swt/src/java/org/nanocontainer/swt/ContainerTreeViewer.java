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

import org.eclipse.jface.viewers.AbstractTreeViewer;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.picocontainer.PicoContainer;

import java.util.ArrayList;
import java.util.Collection;

/**
 * Simple TreeViewer that takes a PicoContainer as root object to display it.
 *
 * @author Laurent Etiemble
 * @version $Revision$
 */
public class ContainerTreeViewer extends TreeViewer {

    public ContainerTreeViewer(Composite parent, int flags) {
        super(parent, flags);

        setContentProvider(new ContainerTreeContentProvider(null));
        setLabelProvider(new ContainerTreeLabelProvider());
        setAutoExpandLevel(AbstractTreeViewer.ALL_LEVELS);
        getTree().setLayoutData(new GridData(GridData.FILL_BOTH));
    }

    /**
     * Helper method that allows a big hack.
     * <p>Must be used instead of setInput because if it is used,
     * the PicoContainer is not displayed. Maybe there is a
     * better way to achieve this.</p>
     *
     * @param container
     */
    public void setContainer(PicoContainer container) {
        Collection input = new ArrayList();
        input.add(container);
        setInput(input);
    }
}
