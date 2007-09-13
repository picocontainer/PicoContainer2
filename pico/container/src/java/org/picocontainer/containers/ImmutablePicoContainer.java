/*****************************************************************************
 * Copyright (C) PicoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the LICENSE.txt file.                                                     *
 *                                                                           *
 * Original code by                                                          *
 *****************************************************************************/
package org.picocontainer.containers;

import org.picocontainer.PicoContainer;
import org.picocontainer.ComponentAdapter;
import org.picocontainer.PicoVisitor;
import org.picocontainer.ParameterName;

import java.util.List;
import java.util.Collection;
import java.io.Serializable;

/**
* wrap pico container to achieve immutability
 * Typically its used to mock a parent container.
 *
 * @author Konstantin Pribluda
 */
public class ImmutablePicoContainer implements PicoContainer, Serializable {

    private final PicoContainer delegate;

    public ImmutablePicoContainer(PicoContainer delegate) {
        if (delegate == null) {
            throw new NullPointerException();
        }
        this.delegate = delegate;
    }

    public Object getComponent(Object componentKeyOrType) {
        return delegate.getComponent(componentKeyOrType);
    }

    public <T> T getComponent(Class<T> componentType) {
        return delegate.getComponent(componentType);
    }

    public List getComponents() {
        return delegate.getComponents();
    }

    public PicoContainer getParent() {
        return delegate.getParent();
    }

    public ComponentAdapter<?> getComponentAdapter(Object componentKey) {
        return delegate.getComponentAdapter(componentKey);
    }

    public <T> ComponentAdapter<T> getComponentAdapter(Class<T> componentType, ParameterName componentParameterName) {
        return delegate.getComponentAdapter(componentType, componentParameterName);  
    }

    public Collection<ComponentAdapter<?>> getComponentAdapters() {
        return delegate.getComponentAdapters();
    }

    public <T> List<ComponentAdapter<T>> getComponentAdapters(Class<T> componentType) {
        return delegate.getComponentAdapters(componentType);
    }

    public <T> List<T> getComponents(Class<T> componentType) {
        return delegate.getComponents(componentType);
    }

    public void accept(PicoVisitor visitor) {
        delegate.accept(visitor);
    }

    public boolean equals(Object obj) {
        return obj == this
               || (obj != null && obj == delegate)
               || (obj instanceof ImmutablePicoContainer && ((ImmutablePicoContainer) obj).delegate == delegate)
            ;
    }


    public int hashCode() {
        return delegate.hashCode();
    }
}
