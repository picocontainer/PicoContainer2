/*****************************************************************************
 * Copyright (C) PicoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the LICENSE.txt file.                                                     *
 *                                                                           *
 * Original code by                                                          *
 *****************************************************************************/
package org.picocontainer.adapters;

import org.picocontainer.ComponentMonitor;
import org.picocontainer.PicoVisitor;
import org.picocontainer.ComponentAdapter;
import org.picocontainer.ComponentMonitorStrategy;
import org.picocontainer.monitors.AbstractComponentMonitor;
import org.picocontainer.monitors.NullComponentMonitor;

import java.io.Serializable;

/**
 * Base class for a ComponentAdapter with general functionality.
 * This implementation provides basic checks for a healthy implementation of a ComponentAdapter.
 * It does not allow to use <code>null</code> for the component key or the implementation,
 * ensures that the implementation is a concrete class and that the key is assignable from the
 * implementation if the key represents a type.
 *
 * @author Paul Hammant
 * @author Aslak Helles&oslash;y
 * @author Jon Tirs&eacute;n
 */
public abstract class AbstractAdapter<T> implements ComponentAdapter<T>, ComponentMonitorStrategy, Serializable {
    private Object componentKey;
    private Class<T> componentImplementation;
    private ComponentMonitor<T> componentMonitor;


    /**
     * Constructs a new ComponentAdapter for the given key and implementation.
     * @param componentKey the search key for this implementation
     * @param componentImplementation the concrete implementation
     */
    public AbstractAdapter(Object componentKey, Class componentImplementation) {
        this(componentKey, componentImplementation, new AbstractComponentMonitor());
        this.componentMonitor = new NullComponentMonitor<T>();
    }

    /**
     * Constructs a new ComponentAdapter for the given key and implementation.
     * @param componentKey the search key for this implementation
     * @param componentImplementation the concrete implementation
     * @param monitor the component monitor used by this ComponentAdapter
     */
    public AbstractAdapter(Object componentKey, Class componentImplementation, ComponentMonitor monitor) {
        if (monitor == null) {
            throw new NullPointerException("ComponentMonitor==null");
        }
        this.componentMonitor = monitor;
        if (componentImplementation == null) {
            throw new NullPointerException("componentImplementation");
        }
        this.componentKey = componentKey;
        this.componentImplementation = componentImplementation;
        checkTypeCompatibility();
    }

    /**
     * {@inheritDoc}
     * @see org.picocontainer.ComponentAdapter#getComponentKey()
     */
    public Object getComponentKey() {
        if (componentKey == null) {
            throw new NullPointerException("componentKey");
        }
        return componentKey;
    }

    /**
     * {@inheritDoc}
     * @see org.picocontainer.ComponentAdapter#getComponentImplementation()
     */
    public Class<T> getComponentImplementation() {
        return componentImplementation;
    }

    protected void checkTypeCompatibility() {
        if (componentKey instanceof Class) {
            Class<?> componentType = (Class) componentKey;
            if (!componentType.isAssignableFrom(componentImplementation)) {
                throw new ClassCastException(componentImplementation.getName() + " is not a " + componentType.getName());
            }
        }
    }

    /**
     * @return Returns the ComponentAdapter's class name and the component's key.
     * @see java.lang.Object#toString()
     */
    public String toString() {
        return getDescriptor() + getComponentKey();
    }

    public void accept(PicoVisitor visitor) {
        visitor.visitComponentAdapter(this);
    }

    public void changeMonitor(ComponentMonitor monitor) {
        this.componentMonitor = monitor;
    }

    /**
     * Returns the monitor currently used
     * @return The ComponentMonitor currently used
     */
    public ComponentMonitor<T> currentMonitor(){
        return componentMonitor;
    }

    public final ComponentAdapter<T> getDelegate() {
        return null;
    }

    public final <U extends ComponentAdapter> U findAdapterOfType(Class<U> componentAdapterType) {
        return null;
    }
    
    
}
