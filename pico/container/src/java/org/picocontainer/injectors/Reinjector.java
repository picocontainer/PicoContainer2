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
import org.picocontainer.Parameter;
import org.picocontainer.MutablePicoContainer;
import org.picocontainer.ComponentMonitorStrategy;
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

    public Reinjector(PicoContainer parentContainer) {
        this(parentContainer, parentContainer instanceof ComponentMonitorStrategy
                ? ((ComponentMonitorStrategy) parentContainer).currentMonitor()
                : new NullComponentMonitor());
    }

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
        return reinject(key, reinjectionMethod, key, parent.getComponent(key));
    }

    /**
     * Reinjecting into a method.
     * @param key the component-key from the parent set of components to inject into
     * @param reinjectionMethod the reflection method to use for injection.
     * @param implementation the implementation of the component that is going to result.
     * @param instance the object that has the provider method to be invoked
     * @return the result of the reinjection-method invocation.
     */
    public Object reinject(Class<?> key, Method reinjectionMethod, Class implementation, Object instance) {
        return reinject(key, reinjectionMethod, NO_PROPERTIES, implementation, instance);
    }

    public Object reinject(Class<?> key, Method reinjectionMethod, Properties properties, Class implementation, Object instance) {
        Reinjection reinjection = new Reinjection(new MethodInjection(reinjectionMethod), parent);
        org.picocontainer.Injector mi = (org.picocontainer.Injector) reinjection.createComponentAdapter(
                monitor, NO_LIFECYCLE, properties, key, implementation, null);
        return mi.decorateComponentInstance(parent, null, instance);
    }
}
