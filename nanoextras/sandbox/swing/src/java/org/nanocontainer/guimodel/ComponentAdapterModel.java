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

import org.picocontainer.ComponentAdapter;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.MethodDescriptor;
import java.beans.PropertyDescriptor;
import java.util.HashMap;
import java.util.Map;

/**
 * Model around a ComponentAdapter that allows to store additional
 * properties to set on the instance when it is created.
 *
 * @author Laurent Etiemble
 * @version $Revision$
 */
public class ComponentAdapterModel {
    private static final Map models = new HashMap();

    private final PropertyDescriptor[] propertyDescriptors;
    private final MethodDescriptor[] methodDescriptors;

    private Map propertyMap = new HashMap();
    private BeanProperty[] properties;

    private ComponentAdapterModel(ComponentAdapter componentAdapter) {
        try {
            BeanInfo bi = Introspector.getBeanInfo(componentAdapter.getComponentImplementation());
            propertyDescriptors = bi.getPropertyDescriptors();
            methodDescriptors = bi.getMethodDescriptors();
        } catch (IntrospectionException ie) {
            throw new RuntimeException("Can't retrieve property descriptors");
        }
    }

    public PropertyDescriptor[] getPropertyDescriptors() {
        return propertyDescriptors;
    }

    public MethodDescriptor[] getMethodDescriptors() {
        return methodDescriptors;
    }

    public BeanProperty[] getProperties() {
        if (properties == null) {
            properties = new BeanProperty[propertyDescriptors.length];
            for (int i = 0; i < properties.length; i++) {
                properties[i] = new BeanProperty(propertyMap, getPropertyDescriptors()[i]);
            }
        }
        return properties;
    }

    public BeanProperty getProperty(int index) {
        BeanProperty[] properties = getProperties();
        return properties[index];
    }

    public Object getPropertyValue(int index) {
        Object[] properties = getProperties();
        BeanProperty bp = (BeanProperty) properties[index];
        return bp.getValue();
    }

    public void setPropertyValue(int index, Object value) {
        Object[] properties = getProperties();
        BeanProperty bp = (BeanProperty) properties[index];
        bp.setValue(value);
    }

    public static ComponentAdapterModel getInstance(ComponentAdapter componentAdapter) {
        ComponentAdapterModel model = (ComponentAdapterModel) models.get(componentAdapter);
        if (model == null) {
            model = new ComponentAdapterModel(componentAdapter);
            models.put(componentAdapter, model);
        }
        return model;
    }

}
