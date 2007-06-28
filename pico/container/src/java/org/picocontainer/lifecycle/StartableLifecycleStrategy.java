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
import org.picocontainer.lifecycle.AbstractMonitoringLifecycleStrategy;

import java.lang.reflect.Method;

/**
 * Startable lifecycle strategy.  Starts and stops component if Startable,
 * and disposes it if Disposable.
 *
 * @author Mauro Talevi
 * @author J&ouml;rg Schaible
 * @see Startable
 * @see Disposable
 */
public class StartableLifecycleStrategy extends AbstractMonitoringLifecycleStrategy {

    private static Method start, stop, dispose;
    {
        try {
            start = Startable.class.getMethod("start", (Class[])null);
            stop = Startable.class.getMethod("stop", (Class[])null);
            dispose = Disposable.class.getMethod("dispose", (Class[])null);
        } catch (NoSuchMethodException e) {
        }
    }

    public StartableLifecycleStrategy(ComponentMonitor monitor) {
        super(monitor);
    }

    public void start(Object component) {
        if (component != null && component instanceof Startable) {
            long str = System.currentTimeMillis();
            currentMonitor().invoking(null, null, start, component);
            try {
                ((Startable) component).start();
                currentMonitor().invoked(null, null, start, component, System.currentTimeMillis() - str);
            } catch (RuntimeException cause) {
                currentMonitor().lifecycleInvocationFailed(null, null, start, component, cause); // may re-throw
            }
        }
    }

    public void stop(Object component) {
        if (component != null && component instanceof Startable) {
            long str = System.currentTimeMillis();
            currentMonitor().invoking(null, null, stop, component);
            try {
                ((Startable) component).stop();
                currentMonitor().invoked(null, null, stop, component, System.currentTimeMillis() - str);
            } catch (RuntimeException cause) {
                currentMonitor().lifecycleInvocationFailed(null, null, stop, component, cause); // may re-throw
            }
        }
    }

    public void dispose(Object component) {
        if (component != null && component instanceof Disposable) {
            long str = System.currentTimeMillis();
            currentMonitor().invoking(null, null, dispose, component);
            try {
                ((Disposable) component).dispose();
                currentMonitor().invoked(null, null, dispose, component, System.currentTimeMillis() - str);
            } catch (RuntimeException cause) {
                currentMonitor().lifecycleInvocationFailed(null, null, dispose, component, cause); // may re-throw
            }
        }
    }

    public boolean hasLifecycle(Class type) {
        return Startable.class.isAssignableFrom(type) || Disposable.class.isAssignableFrom(type);
    }
}
