/*****************************************************************************
 * Copyright (C) PicoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the LICENSE.txt file.                                                     *
 *                                                                           *
 *****************************************************************************/
package org.picocontainer.injectors;

import org.picocontainer.PicoContainer;
import org.picocontainer.ComponentMonitor;
import org.picocontainer.ComponentMonitorStrategy;
import org.picocontainer.InjectionFactory;
import org.picocontainer.lifecycle.NullLifecycleStrategy;
import org.picocontainer.monitors.NullComponentMonitor;

import java.lang.reflect.Method;
import java.util.Properties;

/**
 * A Reinjector allows methods on pre-instantiated classes to be invoked,
 * with appropriately scoped parameters.
 */
public class Reinjector {
    
    private final PicoContainer parent;
    private final ComponentMonitor monitor;
    private static NullLifecycleStrategy NO_LIFECYCLE = new NullLifecycleStrategy();
    private static Properties NO_PROPERTIES = new Properties();

    /**
     * Make a reinjector with a parent container from which to pull components to be reinjected to.
     * With this constructor, a NullComponentMonitor is used.
     * @param parentContainer the parent container
     */
    public Reinjector(PicoContainer parentContainer) {
        this(parentContainer, parentContainer instanceof ComponentMonitorStrategy
                ? ((ComponentMonitorStrategy) parentContainer).currentMonitor()
                : new NullComponentMonitor());
    }

    /**
     * Make a reinjector with a parent container from which to pull components to be reinjected to
     * @param parentContainer the parent container
     * @param monitor the monitor to use for 'instantiating' events
     */
    public Reinjector(PicoContainer parentContainer, ComponentMonitor monitor) {
        this.parent = parentContainer;
        this.monitor = monitor;
    }

    /**
     * Reinjecting into a method.
     * @param key the component-key from the parent set of components to inject into
     * @param reinjectionMethod the reflection method to use for injection.
     * @return the result of the reinjection-method invocation.
     */
    public Object reinject(Class<?> key, Method reinjectionMethod) {
        return reinject(key, key, parent.getComponent(key), new MethodInjection(reinjectionMethod));
    }

    /**
     * Reinjecting into a method.
     * @param key the component-key from the parent set of components to inject into
     * @param reinjectionFactory the InjectionFactory to use for reinjection.
     * @return the result of the reinjection-method invocation.
     */
    public Object reinject(Class<?> key, InjectionFactory reinjectionFactory) {
        Object o = reinject(key, key, parent.getComponent(key), reinjectionFactory);
        return o;
    }

    public Object reinject(Class<?> key, Class<?> impl, InjectionFactory reinjectionFactory) {
        return reinject(key, impl, parent.getComponent(key), reinjectionFactory);
    }

    /**
     * Reinjecting into a method.
     * @param key the component-key from the parent set of components to inject into
     * @param implementation the implementation of the component that is going to result.
     * @param instance the object that has the provider method to be invoked
     * @param reinjectionFactory the InjectionFactory to use for reinjection.
     * @return the result of the reinjection-method invocation.
     */
    public Object reinject(Class<?> key, Class implementation, Object instance, InjectionFactory reinjectionFactory) {
        Object o = reinject(key, implementation, instance, NO_PROPERTIES, reinjectionFactory);
        return o;
    }

    /**
     *
     * @param key the component-key from the parent set of components to inject into
     * @param implementation the implementation of the component that is going to result.
     * @param instance the object that has the provider method to be invoked
     * @param properties
     * @param reinjectionFactory the InjectionFactory to use for reinjection.
     * @return the result of the reinjection-method invocation.
     */
    public Object reinject(Class<?> key, Class implementation, Object instance, Properties properties,
                           InjectionFactory reinjectionFactory) {
        Reinjection reinjection = new Reinjection(reinjectionFactory, parent);
        org.picocontainer.Injector injector = (org.picocontainer.Injector) reinjection.createComponentAdapter(
                monitor, NO_LIFECYCLE, properties, key, implementation, null);
        Object o = injector.decorateComponentInstance(parent, null, instance);
        return o;
    }

}
