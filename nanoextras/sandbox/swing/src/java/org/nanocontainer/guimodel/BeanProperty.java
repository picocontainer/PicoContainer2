/*****************************************************************************
 * Copyright (C) NanoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the LICENSE.txt file.                                                     *
 *                                                                           *
 * Original code by                                                          *
 *****************************************************************************/
package org.nanocontainer.guimodel;

import java.beans.PropertyDescriptor;
import java.beans.PropertyEditor;
import java.beans.PropertyEditorManager;
import java.util.Map;

/**
 * A simple wrapper around a Bean property.
 *
 * @author Laurent Etiemble
 * @author Aslak Helles&oslashy;
 * @version $Revision$
 */
public class BeanProperty {
    private final Map propertyMap;
    private PropertyDescriptor pd;
    private PropertyEditor editor = null;

    public BeanProperty(Map propertyMap, PropertyDescriptor pd) {
        this.propertyMap = propertyMap;
        this.pd = pd;

        try {
            // Try to get a property editor
            if (pd.getPropertyEditorClass() != null) {
                editor = (PropertyEditor) pd.getPropertyEditorClass().newInstance();
            }
            if (editor == null) {
                editor = PropertyEditorManager.findEditor(pd.getPropertyType());
            }
        } catch (InstantiationException ie) {
            // Do nothing
        } catch (IllegalAccessException iae) {
            // Do nothing
        }
    }

    public PropertyEditor getPropertyEditor() {
        return editor;
    }

    public Object getValue() {
        return propertyMap.get(pd.getName());
    }

    public void setValue(Object value) {
        propertyMap.put(pd.getName(), value);
    }

    public String getName() {
        return pd.getDisplayName();
    }

    public boolean isReadable() {
        return (pd.getReadMethod() != null);
    }

    public boolean isWritable() {
        return (pd.getWriteMethod() != null);
    }
}
