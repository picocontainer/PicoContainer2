/*****************************************************************************
 * Copyright (C) PicoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the LICENSE.txt file.                                                     *
 *                                                                           *
 * Original code by Jon Tirsen                                               *
 *****************************************************************************/

package org.picocontainer.behaviors;

import java.io.Serializable;

import org.picocontainer.ComponentAdapter;
import org.picocontainer.ComponentMonitor;
import org.picocontainer.LifecycleManager;
import org.picocontainer.PicoContainer;
import org.picocontainer.PicoCompositionException;
import org.picocontainer.PicoVisitor;
import org.picocontainer.ComponentMonitorStrategy;
import org.picocontainer.LifecycleStrategy;

/**
 * <p>
 * Component adapter which decorates another adapter.
 * </p>
 * <p>
 * This adapter supports a {@link org.picocontainer.ComponentMonitorStrategy component monitor strategy}
 * and will propagate change of monitor to the delegate if the delegate itself
 * support the monitor strategy.
 * </p>
 * <p>
 * This adapter also supports a {@link LifecycleManager lifecycle manager} and a
 * {@link org.picocontainer.LifecycleStrategy lifecycle strategy} if the delegate does.
 * </p>
 * 
 * @author Jon Tirsen
 * @author Aslak Hellesoy
 * @author Mauro Talevi
 * @version $Revision$
 */
public abstract class AbstractBehavior implements ComponentAdapter, ComponentMonitorStrategy,
                                                    LifecycleManager, LifecycleStrategy, Serializable {

    private final ComponentAdapter delegate;

    public AbstractBehavior(ComponentAdapter delegate) {
         this.delegate = delegate;
    }
    
    public Object getComponentKey() {
        return delegate.getComponentKey();
    }

    public Class getComponentImplementation() {
        return delegate.getComponentImplementation();
    }

    public Object getComponentInstance(PicoContainer container) throws
                                                                PicoCompositionException
    {
        return delegate.getComponentInstance(container);
    }

    public void verify(PicoContainer container) throws PicoCompositionException {
        delegate.verify(container);
    }

    public ComponentAdapter getDelegate() {
        return delegate;
    }

    public void accept(PicoVisitor visitor) {
        visitor.visitComponentAdapter(this);
        delegate.accept(visitor);
    }

    /**
     * Delegates change of monitor if the delegate supports 
     * a component monitor strategy.
     * {@inheritDoc}
     */
    public void changeMonitor(ComponentMonitor monitor) {
        if ( delegate instanceof ComponentMonitorStrategy ){
            ((ComponentMonitorStrategy)delegate).changeMonitor(monitor);
        }
    }

    /**
     * Returns delegate's current monitor if the delegate supports 
     * a component monitor strategy.
     * {@inheritDoc}
     * @throws PicoCompositionException if no component monitor is found in delegate
     */
    public ComponentMonitor currentMonitor() {
        if ( delegate instanceof ComponentMonitorStrategy ){
            return ((ComponentMonitorStrategy)delegate).currentMonitor();
        }
        throw new PicoCompositionException("No component monitor found in delegate");
    }

    // ~~~~~~~~ LifecylceManager ~~~~~~~~
   
    /**
     * Invokes delegate start method if the delegate is a LifecycleManager
     * {@inheritDoc}
     */
    public void start(PicoContainer container) {
        if ( delegate instanceof LifecycleManager ){
            ((LifecycleManager)delegate).start(container);
        }
    }

    /**
     * Invokes delegate stop method if the delegate is a LifecycleManager
     * {@inheritDoc}
     */
    public void stop(PicoContainer container) {
        if ( delegate instanceof LifecycleManager ){
            ((LifecycleManager)delegate).stop(container);
        }
    }
    
    /**
     * Invokes delegate dispose method if the delegate is a LifecycleManager
     * {@inheritDoc}
     */
    public void dispose(PicoContainer container) {
        if ( delegate instanceof LifecycleManager ){
            ((LifecycleManager)delegate).dispose(container);
        }
    }

    /**
     * Invokes delegate hasLifecylce method if the delegate is a LifecycleManager
     * {@inheritDoc}
     */
    public boolean hasLifecycle() {
        if ( delegate instanceof LifecycleManager ){
            return ((LifecycleManager)delegate).hasLifecycle();
        }
        if ( delegate instanceof LifecycleStrategy ){
            return ((LifecycleStrategy)delegate).hasLifecycle(delegate.getComponentImplementation());
        }
        return false;
    }

    // ~~~~~~~~ LifecylceStrategy ~~~~~~~~

    /**
     * Invokes delegate start method if the delegate is a LifecycleStrategy
     * {@inheritDoc}
     */
    public void start(Object component) {
        if ( delegate instanceof LifecycleStrategy ){
            ((LifecycleStrategy)delegate).start(component);
        }
    }

    /**
     * Invokes delegate stop method if the delegate is a LifecycleStrategy
     * {@inheritDoc}
     */
    public void stop(Object component) {
        if ( delegate instanceof LifecycleStrategy ){
            ((LifecycleStrategy)delegate).stop(component);
        }
    }

    /**
     * Invokes delegate dispose method if the delegate is a LifecycleStrategy
     * {@inheritDoc}
     */
    public void dispose(Object component) {
        if ( delegate instanceof LifecycleStrategy ){
            ((LifecycleStrategy)delegate).dispose(component);
        }
    }

    /**
     * Invokes delegate hasLifecylce(Class) method if the delegate is a LifecycleStrategy
     * {@inheritDoc}
     */
    public boolean hasLifecycle(Class type) {
        return delegate instanceof LifecycleStrategy && ((LifecycleStrategy) delegate).hasLifecycle(type);
    }
    
    public String toString() {
        StringBuffer buffer = new StringBuffer();
        buffer.append("[");
        buffer.append(getPrintableClassName());
        buffer.append(" delegate=");
        buffer.append(delegate);
        buffer.append("]");
        return buffer.toString();
    }
    
    private String getPrintableClassName() {
        String name = getClass().getName();
        name = name.substring(name.lastIndexOf('.')+1);
        if (name.endsWith("ComponentAdapter")) {
            name = name.substring(0, name.length() - "ComponentAdapter".length()) + "CA";
        }
        return name;
    }

}

