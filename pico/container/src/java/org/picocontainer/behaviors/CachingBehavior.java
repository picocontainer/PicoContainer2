/*****************************************************************************
 * Copyright (c) PicoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the LICENSE.txt file.                                                     *
 *                                                                           *
 * Idea by Rachel Davies, Original code by Aslak Hellesoy and Paul Hammant   *
 *****************************************************************************/

package org.picocontainer.behaviors;

import org.picocontainer.ComponentAdapter;
import org.picocontainer.LifecycleManager;
import org.picocontainer.PicoContainer;
import org.picocontainer.PicoCompositionException;
import org.picocontainer.LifecycleStrategy;
import org.picocontainer.ObjectReference;
import org.picocontainer.behaviors.AbstractBehavior;

import java.io.Serializable;

/**
 * <p>
 * {@link ComponentAdapter} implementation that caches the component instance.
 * </p>
 * <p>
 * This adapter supports components with a lifecycle, as it is a {@link LifecycleManager lifecycle manager}
 * which will apply the delegate's {@link org.picocontainer.LifecycleStrategy lifecycle strategy} to the cached component instance.
 * The lifecycle state is maintained so that the component instance behaves in the expected way:
 * it can't be started if already started, it can't be started or stopped if disposed, it can't
 * be stopped if not started, it can't be disposed if already disposed.
 * </p>
 *   
 * @author Mauro Talevi
 * @version $Revision$
 */
public final class CachingBehavior extends AbstractBehavior implements LifecycleManager {

    private final ObjectReference instanceReference;
    private boolean disposed;
    private boolean started;
    private final boolean delegateHasLifecylce;

    public CachingBehavior(ComponentAdapter delegate) {
        this(delegate, new SimpleReference());
    }

    public CachingBehavior(ComponentAdapter delegate, ObjectReference instanceReference) {
        super(delegate);
        this.instanceReference = instanceReference;
        this.disposed = false;
        this.started = false;
        this.delegateHasLifecylce = delegate instanceof LifecycleStrategy
                && ((LifecycleStrategy) delegate).hasLifecycle(delegate.getComponentImplementation());
    }

    public Object getComponentInstance(PicoContainer container)
            throws PicoCompositionException
    {
        Object instance = instanceReference.get();
        if (instance == null) {
            instance = super.getComponentInstance(container);
            instanceReference.set(instance);
        }
        return instance;
    }

    /**
     * Flushes the cache.
     * If the component instance is started is will stop and dispose it before
     * flushing the cache.
     */
    public void flush() {
        Object instance = instanceReference.get();
        if ( instance != null && delegateHasLifecylce && started ) {
            stop(instance);
            dispose(instance);
        }
        instanceReference.set(null);
    }

    /**
     * Starts the cached component instance
     * {@inheritDoc}
     */
    public void start(PicoContainer container) {
        if ( delegateHasLifecylce ){
            if (disposed) throw new IllegalStateException("Already disposed");
            if (started) throw new IllegalStateException("Already started");
            start(getComponentInstance(container));
            started = true;
        }
    }

    /**
     * Stops the cached component instance
     * {@inheritDoc}
     */
    public void stop(PicoContainer container) {
        if ( delegateHasLifecylce ){
            if (disposed) throw new IllegalStateException("Already disposed");
            if (!started) throw new IllegalStateException("Not started");
            stop(getComponentInstance(container));
            started = false;
        }
    }

    /**
     * Disposes the cached component instance
     * {@inheritDoc}
     */
    public void dispose(PicoContainer container) {
        if ( delegateHasLifecylce ){
            if (disposed) throw new IllegalStateException("Already disposed");
            dispose(getComponentInstance(container));
            disposed = true;
        }
    }

    public boolean hasLifecycle() {
        return delegateHasLifecylce;
    }

    /**
     * @author Aslak Helles&oslash;y
     * @version $Revision$
     */
    public static class SimpleReference implements ObjectReference, Serializable {
        private Object instance;

        public SimpleReference() {
        }

        public Object get() {
            return instance;
        }

        public void set(Object item) {
            this.instance = item;
        }
    }

    
}
