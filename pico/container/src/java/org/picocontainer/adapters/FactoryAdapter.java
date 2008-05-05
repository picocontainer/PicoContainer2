/*****************************************************************************
 * Copyright (C) PicoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the LICENSE.txt file.                                                     *
 *                                                                           *
 *****************************************************************************/
package org.picocontainer.adapters;

import org.picocontainer.ComponentAdapter;
import org.picocontainer.PicoCompositionException;
import org.picocontainer.PicoContainer;
import org.picocontainer.PicoVisitor;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

/**
 * <p>
 * A Component adapter which provides an custom instance in a factory style 
 * </p>
 *
 * @author Paul Hammant
 */
public abstract class FactoryAdapter<T> implements ComponentAdapter<T> {
    private Class key;

    public FactoryAdapter() throws PicoCompositionException {
        Type type = this.getClass().getGenericSuperclass();
        key = (Class) ((ParameterizedType) type).getActualTypeArguments()[0];
    }

    public Object getComponentKey() {
        return key;
    }

    public Class<T> getComponentImplementation() {
        return key;
    }

    public void accept(PicoVisitor visitor) {
    }

    public ComponentAdapter<T> getDelegate() {
        return null;
    }

    public <U extends ComponentAdapter> U findAdapterOfType(Class<U> componentAdapterType) {
        return null;
    }

    public T getComponentInstance(PicoContainer container) {
        throw new UnsupportedOperationException();
    }

    public abstract T getComponentInstance(PicoContainer container, Type clazz);

    public void verify(PicoContainer container) {
    }

    public String getDescriptor() {
        return "Factory-";
    }

    public void start(PicoContainer container) {
    }

    public void stop(PicoContainer container) {
    }

    public void dispose(PicoContainer container) {
    }

    public boolean componentHasLifecycle() {
        return false;
    }

}