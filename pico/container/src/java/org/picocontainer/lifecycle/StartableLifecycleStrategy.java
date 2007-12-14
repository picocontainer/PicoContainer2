/*****************************************************************************
 * Copyright (C) PicoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the LICENSE.txt file.                                                     *
 *****************************************************************************/
package org.picocontainer.lifecycle;

import java.lang.reflect.Method;

import org.picocontainer.ComponentMonitor;
import org.picocontainer.Disposable;
import org.picocontainer.Startable;

/**
 * Startable lifecycle strategy.  Starts and stops component if Startable,
 * and disposes it if Disposable.
 *
 * A subclass of this class can define other intrfaces for Startable/Disposable as well as other method names
 * for start/stop/dispose
 *
 * @author Mauro Talevi
 * @author J&ouml;rg Schaible
 * @see Startable
 * @see Disposable
 */
public class StartableLifecycleStrategy extends AbstractMonitoringLifecycleStrategy {

    /**
	 * Serialization UUID.
	 */
	private static final long serialVersionUID = 4967544912924651398L;


	private transient Method start, stop, dispose;

    public StartableLifecycleStrategy(final ComponentMonitor monitor) {
        super(monitor);
    }

    private void doMethodsIfNotDone() {
        try {
            if (start == null) {
                start = getStartableInterface().getMethod(getStartMethodName());
            }
            if (stop == null) {
                stop = getStartableInterface().getMethod(getStopMethodName());
            }
            if (dispose == null) {
                dispose = getDisposableInterface().getMethod(getDisposeMethodName());
            }
        } catch (NoSuchMethodException e) {
        }
    }

    /**
     * Retrieve the lifecycle method name that represents the dispose method.
     * @return the dispose method name. ('dispose')
     */
    protected String getDisposeMethodName() {
        return "dispose";
    }

    /**
     * Retrieve the lifecycle method name that represents the stop method. 
     * @return the stop method name ('stop')
     */
    protected String getStopMethodName() {
        return "stop";
    }

    /**
     * Retrieve the lifecycle method name that represents the start method. 
     * @return the stop method name ('start')
     */
    protected String getStartMethodName() {
        return "start";
    }


    /** {@inheritDoc} **/
    public void start(final Object component) {
        doMethodsIfNotDone();
        if (component != null && getStartableInterface().isAssignableFrom(component.getClass())) {
            long str = System.currentTimeMillis();
            currentMonitor().invoking(null, null, start, component);
            try {
                startComponent(component);
                currentMonitor().invoked(null, null, start, component, System.currentTimeMillis() - str);
            } catch (RuntimeException cause) {
                currentMonitor().lifecycleInvocationFailed(null, null, start, component, cause); // may re-throw
            }
        }
    }

    protected void startComponent(final Object component) {
        ((Startable) component).start();
    }
    protected void stopComponent(final Object component) {
        ((Startable) component).stop();
    }
    protected void disposeComponent(final Object component) {
        ((Disposable) component).dispose();
    }

    /** {@inheritDoc} **/
    public void stop(final Object component) {
        doMethodsIfNotDone();
        if (component != null && getStartableInterface().isAssignableFrom(component.getClass())) {
            long str = System.currentTimeMillis();
            currentMonitor().invoking(null, null, stop, component);
            try {
                stopComponent(component);
                currentMonitor().invoked(null, null, stop, component, System.currentTimeMillis() - str);
            } catch (RuntimeException cause) {
                currentMonitor().lifecycleInvocationFailed(null, null, stop, component, cause); // may re-throw
            }
        }
    }

    /** {@inheritDoc} **/
    public void dispose(final Object component) {
        doMethodsIfNotDone();
        if (component != null && getDisposableInterface().isAssignableFrom(component.getClass())) {
            long str = System.currentTimeMillis();
            currentMonitor().invoking(null, null, dispose, component);
            try {
                disposeComponent(component);
                currentMonitor().invoked(null, null, dispose, component, System.currentTimeMillis() - str);
            } catch (RuntimeException cause) {
                currentMonitor().lifecycleInvocationFailed(null, null, dispose, component, cause); // may re-throw
            }
        }
    }

    /** {@inheritDoc} **/
    public boolean hasLifecycle(final Class<?> type) {
        return getStartableInterface().isAssignableFrom(type) || getDisposableInterface().isAssignableFrom(type);
    }

    protected Class<Disposable> getDisposableInterface() {
        return Disposable.class;
    }

    protected Class<Startable> getStartableInterface() {
        return Startable.class;
    }
}
