/*****************************************************************************
 * Copyright (C) PicoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the LICENSE.txt file.                                                     *
 *                                                                           *
 * Original code by the committers                                           *
 *****************************************************************************/
package org.picocontainer.containers;

import org.picocontainer.ComponentAdapter;
import org.picocontainer.ComponentCharacteristics;
import org.picocontainer.MutablePicoContainer;
import org.picocontainer.Parameter;
import org.picocontainer.PicoContainer;
import org.picocontainer.PicoException;
import org.picocontainer.PicoVisitor;
import org.picocontainer.PicoCompositionException;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;

/**
 * @author Paul Hammant
 * @version $Revision$
 */
public abstract class AbstractDelegatingMutablePicoContainer implements MutablePicoContainer, Serializable {

    private MutablePicoContainer delegate;

    public AbstractDelegatingMutablePicoContainer(MutablePicoContainer delegate) {
        if (delegate == null) {
            throw new NullPointerException("MutablePicoContainer delegate must not be null");
        }
        this.delegate = delegate;
    }

    protected MutablePicoContainer getDelegate() {
        return delegate;
    }

    public MutablePicoContainer addComponent(Object componentKey,
                                             Object componentImplementationOrInstance,
                                             Parameter... parameters) throws PicoCompositionException {
        return delegate.addComponent(componentKey, componentImplementationOrInstance, parameters);
    }

    public MutablePicoContainer addComponent(Object implOrInstance) throws PicoCompositionException {
        return delegate.addComponent(implOrInstance);
    }

    public MutablePicoContainer addAdapter(ComponentAdapter componentAdapter) throws PicoCompositionException {
        return delegate.addAdapter(componentAdapter);
    }

    public ComponentAdapter removeComponent(Object componentKey) {
        return delegate.removeComponent(componentKey);
    }

    public ComponentAdapter removeComponentByInstance(Object componentInstance) {
        return delegate.removeComponentByInstance(componentInstance);
    }

    public Object getComponent(Object componentKeyOrType) {
        return delegate.getComponent(componentKeyOrType);
    }

    public <T> T getComponent(Class<T> componentType) {
        return componentType.cast(getComponent((Object)componentType));
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

    public <T> ComponentAdapter<T> getComponentAdapter(Class<T> componentType) {
        return delegate.getComponentAdapter(componentType);
    }

    public Collection<ComponentAdapter<?>> getComponentAdapters() {
        return delegate.getComponentAdapters();
    }

    public <T> List<ComponentAdapter<T>> getComponentAdapters(Class<T> componentType) {
        return delegate.getComponentAdapters(componentType);
    }

    public void start() {
        delegate.start();
    }

    public void stop() {
        delegate.stop();
    }

    public void dispose() {
        delegate.dispose();
    }

    public MutablePicoContainer addChildContainer(PicoContainer child) {
        return delegate.addChildContainer(child);
    }

    public boolean removeChildContainer(PicoContainer child) {
        return delegate.removeChildContainer(child);
    }

    public void accept(PicoVisitor visitor) {
        delegate.accept(visitor);
    }

    public <T> List<T> getComponents(Class<T> type) throws PicoException {
        return delegate.getComponents(type);
    }

    public boolean equals(Object obj) {
        // required to make it pass on both jdk 1.3 and jdk 1.4. Btw, what about overriding hashCode()? (AH)
        return delegate.equals(obj) || this == obj;
    }

    public MutablePicoContainer change(ComponentCharacteristics... characteristics) {
        return delegate.change(characteristics);
    }

    public MutablePicoContainer as(ComponentCharacteristics... characteristics) {
        return delegate.as(characteristics);
    }
}
