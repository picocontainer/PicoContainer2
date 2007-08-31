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

import org.picocontainer.Behavior;
import org.picocontainer.PicoContainer;
import org.picocontainer.LifecycleStrategy;
import org.picocontainer.ComponentMonitor;
import org.picocontainer.PicoCompositionException;
import org.picocontainer.ComponentAdapter;
import org.picocontainer.adapters.AbstractAdapter;
import org.picocontainer.lifecycle.NullLifecycleStrategy;
import org.picocontainer.monitors.NullComponentMonitor;

/**
 * <p>
 * Component adapter which wraps a component instance.
 * </p>
 * <p>
 * This component adapter supports both a {@link Behavior Behavior} and a
 * {@link org.picocontainer.LifecycleStrategy LifecycleStrategy} to control the lifecycle of the component.
 * The lifecycle manager methods simply delegate to the lifecycle strategy methods 
 * on the component instance.
 * </p>
 * 
 * @author Aslak Helles&oslash;y
 * @author Paul Hammant
 * @author Mauro Talevi
 */
public final class InstanceAdapter<T> extends AbstractAdapter<T> implements Behavior<T>, LifecycleStrategy {
    private final T componentInstance;
    private final LifecycleStrategy lifecycleStrategy;

    
    public InstanceAdapter(Object componentKey, T componentInstance, LifecycleStrategy lifecycleStrategy, ComponentMonitor componentMonitor) throws PicoCompositionException {
        super(componentKey, getInstanceClass(componentInstance), componentMonitor);
        this.componentInstance = componentInstance;
        this.lifecycleStrategy = lifecycleStrategy;
    }

    public InstanceAdapter(Object componentKey, T componentInstance) {
    	this(componentKey,componentInstance,new NullLifecycleStrategy(),new NullComponentMonitor());   	
    }

    public InstanceAdapter(Object componentKey, T componentInstance, LifecycleStrategy lifecycleStrategy) {
    	this(componentKey,componentInstance,lifecycleStrategy,new NullComponentMonitor());  
    }

    public InstanceAdapter(Object componentKey, T componentInstance,  ComponentMonitor componentMonitor) {
    	this(componentKey,componentInstance,new NullLifecycleStrategy(),componentMonitor);  
    }
     
     private static Class getInstanceClass(Object componentInstance) {
        if (componentInstance == null) {
            throw new NullPointerException("componentInstance cannot be null");
        }
        return componentInstance.getClass();
    }

    public T getComponentInstance(PicoContainer container) {
        return componentInstance;
    }
    
    public void verify(PicoContainer container) {
    }

    public String getDescriptor() {
        return "Instance-";
    }

    public void start(PicoContainer container) {
        start(componentInstance);
    }

    public void stop(PicoContainer container) {
        stop(componentInstance);
    }

    public void dispose(PicoContainer container) {
        dispose(componentInstance);
    }

    public boolean componentHasLifecycle() {
        return hasLifecycle(componentInstance.getClass());
    }

    // ~~~~~~~~ LifecycleStrategy ~~~~~~~~
    
    public void start(Object component) {
        lifecycleStrategy.start(componentInstance);
    }

    public void stop(Object component) {
        lifecycleStrategy.stop(componentInstance);
    }

    public void dispose(Object component) {
        lifecycleStrategy.dispose(componentInstance);
    }

    public boolean hasLifecycle(Class type) {
        return lifecycleStrategy.hasLifecycle(type);
    }

}
