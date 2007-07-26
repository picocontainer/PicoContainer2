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
import org.picocontainer.Behavior;
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
 * This adapter also supports a {@link Behavior lifecycle manager} and a
 * {@link org.picocontainer.LifecycleStrategy lifecycle strategy} if the delegate does.
 * </p>
 * 
 * @author Jon Tirsen
 * @author Aslak Hellesoy
 * @author Mauro Talevi
 * @version $Revision$
 */
public abstract class AbstractBehavior implements ComponentAdapter, ComponentMonitorStrategy,
                                                  Behavior, LifecycleStrategy, Serializable {

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

    public Object getComponentInstance(PicoContainer container) throws PicoCompositionException {
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

    /**
     * Invokes delegate start method if the delegate is a Behavior
     * {@inheritDoc}
     */
    public void start(PicoContainer container) {
        if ( delegate instanceof Behavior){
            ((Behavior)delegate).start(container);
        }
    }

    /**
     * Invokes delegate stop method if the delegate is a Behavior
     * {@inheritDoc}
     */
    public void stop(PicoContainer container) {
        if ( delegate instanceof Behavior){
            ((Behavior)delegate).stop(container);
        }
    }
    
    /**
     * Invokes delegate dispose method if the delegate is a Behavior
     * {@inheritDoc}
     */
    public void dispose(PicoContainer container) {
        if ( delegate instanceof Behavior){
            ((Behavior)delegate).dispose(container);
        }
    }

    /**
     * Invokes delegate hasLifecycle method if the delegate is a Behavior
     * {@inheritDoc}
     */
    public boolean componentHasLifecycle() {
        if (delegate instanceof Behavior){
            return ((Behavior)delegate).componentHasLifecycle();
        }
        return false;
    }

    // ~~~~~~~~ LifecycleStrategy ~~~~~~~~

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
     * Invokes delegate hasLifecycle(Class) method if the delegate is a LifecycleStrategy
     * {@inheritDoc}
     */
    public boolean hasLifecycle(Class type) {
        return delegate instanceof LifecycleStrategy && ((LifecycleStrategy) delegate).hasLifecycle(type);
    }


    public String toString() {
        return delegate.toString();
    }
}

