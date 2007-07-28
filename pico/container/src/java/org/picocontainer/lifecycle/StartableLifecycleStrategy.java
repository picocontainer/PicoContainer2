/*****************************************************************************
 * Copyright (C) PicoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the LICENSE.txt file.                                                     *
 *****************************************************************************/
package org.picocontainer.lifecycle;

import org.picocontainer.ComponentMonitor;
import org.picocontainer.Disposable;
import org.picocontainer.Startable;

import java.lang.reflect.Method;

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

    private transient Method start, stop, dispose;

    public StartableLifecycleStrategy(ComponentMonitor monitor) {
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

    protected String getDisposeMethodName() {
        return "dispose";
    }

    protected String getStopMethodName() {
        return "stop";
    }

    protected String getStartMethodName() {
        return "start";
    }


    public void start(Object component) {
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

    protected void startComponent(Object component) {
        ((Startable) component).start();
    }
    protected void stopComponent(Object component) {
        ((Startable) component).stop();
    }
    protected void disposeComponent(Object component) {
        ((Disposable) component).dispose();
    }

    public void stop(Object component) {
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

    public void dispose(Object component) {
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

    public boolean hasLifecycle(Class type) {
        return getStartableInterface().isAssignableFrom(type) || getDisposableInterface().isAssignableFrom(type);
    }

    protected Class<Disposable> getDisposableInterface() {
        return Disposable.class;
    }

    protected Class getStartableInterface() {
        return Startable.class;
    }
}
