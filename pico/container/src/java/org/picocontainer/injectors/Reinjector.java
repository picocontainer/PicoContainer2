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
import org.picocontainer.lifecycle.NullLifecycleStrategy;
import org.picocontainer.monitors.NullComponentMonitor;

import java.lang.reflect.Method;
import java.util.Properties;

/**
 * Reinjector allows methods on pre-instantiated classes to be invoked,
 * with appropriately scoped parameters.
 */
public class Reinjector {
    
    private final PicoContainer parent;
    private final ComponentMonitor monitor;
    private static NullLifecycleStrategy NO_LIFECYCLE = new NullLifecycleStrategy();
    private static Properties NO_PROPERTIES = new Properties();

    public Reinjector(PicoContainer parentContainer) {
        this(parentContainer, new NullComponentMonitor());
    }

    public Reinjector(PicoContainer parentContainer, ComponentMonitor monitor) {
        this.parent = parentContainer;
        this.monitor = monitor;
    }

    /**
     * Reinjecting into a method.
     * @param clazz the component-key from the parent set of components to inject into
     * @param reinjectionMethod the reflection method to use for injection.
     * @return the result of the reinjection-method invocation.
     */
    public Object reinject(Class<?> clazz, Method reinjectionMethod) {
        Reinjection reinjection = new Reinjection(new MethodInjection(reinjectionMethod), parent);
        org.picocontainer.Injector mi = (org.picocontainer.Injector) reinjection.createComponentAdapter(
                monitor, NO_LIFECYCLE, NO_PROPERTIES, clazz, clazz, Parameter.DEFAULT);
        return mi.decorateComponentInstance(parent, null, parent.getComponent(clazz));
    }
}
