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

import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.Viewer;
import org.nanocontainer.guimodel.ComponentAdapterModel;
import org.picocontainer.ComponentAdapter;

/**
 * Provides the data (BeanProperty) to be put inside a table.
 *
 * @author Laurent Etiemble
 * @version $Revision$
 */
public class ComponentAdapterContentProvider implements IStructuredContentProvider {
    private ComponentAdapterModel model = null;

    public ComponentAdapterContentProvider() {
        super();
    }

    public Object[] getElements(Object object) {
        Object[] result = null;
        if (model != null) {
            result = model.getProperties();
        }
        return result;
    }

    public void inputChanged(Viewer viewer, Object oldValue, Object newValue) {
        if (newValue != null) {
            ComponentAdapter componentAdapter = (ComponentAdapter) newValue;
            model = ComponentAdapterModel.getInstance(componentAdapter);
        }
    }

    public void dispose() {
        // Do nothing
    }
}
