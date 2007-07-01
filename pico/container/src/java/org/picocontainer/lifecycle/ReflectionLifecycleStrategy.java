/*****************************************************************************
 * Copyright (C) PicoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the LICENSE.txt file.                                                     *
 *****************************************************************************/
package org.picocontainer.lifecycle;

import org.picocontainer.ComponentMonitor;
import org.picocontainer.lifecycle.AbstractMonitoringLifecycleStrategy;
import org.picocontainer.lifecycle.ReflectionLifecycleException;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;


/**
 * Reflection lifecycle strategy. Starts, stops, disposes of component if appropriate methods are
 * present. The component may implement only one of the three methods.
 * 
 * @author Paul Hammant
 * @author Mauro Talevi
 * @author J&ouml;rg Schaible
 * @see org.picocontainer.Startable
 * @see org.picocontainer.Disposable
 * @see org.picocontainer.lifecycle.StartableLifecycleStrategy
 */
public final class ReflectionLifecycleStrategy extends AbstractMonitoringLifecycleStrategy {

    private final static int START = 0;
    private final static int STOP = 1;
    private final static int DISPOSE = 2;
    private final String[] methodNames;
    private final transient Map<Class, Method[]> methodMap = new HashMap<Class, Method[]>();

    /**
     * Construct a ReflectionLifecycleStrategy.
     * 
     * @param monitor the monitor to use
     * @throws NullPointerException if the monitor is <code>null</code>
     */
    public ReflectionLifecycleStrategy(ComponentMonitor monitor) {
        this(monitor, "start", "stop", "dispose");
    }

    /**
     * Construct a ReflectionLifecycleStrategy with individual method names. Note, that a lifecycle
     * method does not have any arguments.
     * 
     * @param monitor the monitor to use
     * @param startMethodName the name of the start method
     * @param stopMethodName the name of the stop method
     * @param disposeMethodName the name of the dispose method
     * @throws NullPointerException if the monitor is <code>null</code>
     */
    public ReflectionLifecycleStrategy(
            ComponentMonitor monitor, String startMethodName, String stopMethodName,
            String disposeMethodName) {
        super(monitor);
        methodNames = new String[]{startMethodName, stopMethodName, disposeMethodName};
    }

    public void start(Object component) {
        Method[] methods = init(component.getClass());
        invokeMethod(component, methods[START]);
    }

    public void stop(Object component) {
        Method[] methods = init(component.getClass());
        invokeMethod(component, methods[STOP]);
    }

    public void dispose(Object component) {
        Method[] methods = init(component.getClass());
        invokeMethod(component, methods[DISPOSE]);
    }

    private void invokeMethod(Object component, Method method) {
        if (component != null && method != null) {
            try {
                long str = System.currentTimeMillis();
                currentMonitor().invoking(null, null, method, component);
                method.invoke(component);
                currentMonitor().invoked(null, null, method, component, System.currentTimeMillis() - str);
            } catch (IllegalAccessException e) {
                monitorAndThrowReflectionLifecycleException(method, e, component);
            } catch (InvocationTargetException e) {
                monitorAndThrowReflectionLifecycleException(method, e, component);
            }
        }
    }

    protected void monitorAndThrowReflectionLifecycleException(Method method,
                                                             Exception e,
                                                             Object component) {
        RuntimeException re = new ReflectionLifecycleException(method.getName(), e);
        currentMonitor().lifecycleInvocationFailed(null, null, method, component, re);
        throw re;
    }

    /**
     * {@inheritDoc} The component has a lifecylce if at least one of the three methods is present.
     */
    public boolean hasLifecycle(Class type) {
        Method[] methods = init(type);
        for (Method method : methods) {
            if (method != null) {
                return true;
            }
        }
        return false;
    }

    private Method[] init(Class type) {
        Method[] methods;
        synchronized (methodMap) {
            methods = methodMap.get(type);
            if (methods == null) {
                methods = new Method[methodNames.length];
                for (int i = 0; i < methods.length; i++) {
                    try {
                        methods[i] = type.getMethod(methodNames[i]);
                    } catch (NoSuchMethodException e) {
                        continue;
                    }
                }
                methodMap.put(type, methods);
            }
        }
        return methods;
    }
}
