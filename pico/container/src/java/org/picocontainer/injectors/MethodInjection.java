/*****************************************************************************
 * Copyright (c) PicoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the LICENSE.txt file.                                                     *
 *                                                                           *
 * Idea by Rachel Davies, Original code by Aslak Hellesoy and Paul Hammant   *
 *****************************************************************************/
package org.picocontainer.injectors;

import org.picocontainer.Characteristics;
import org.picocontainer.ComponentAdapter;
import org.picocontainer.ComponentMonitor;
import org.picocontainer.LifecycleStrategy;
import org.picocontainer.Parameter;
import org.picocontainer.PicoCompositionException;
import org.picocontainer.behaviors.AbstractBehaviorFactory;

import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.Properties;
import java.util.Set;

/**
 * A {@link org.picocontainer.InjectionFactory} for methods.
 * The factory creates {@link MethodInjector}.
 * 
 *  @author Paul Hammant 
 */
@SuppressWarnings("serial")
public class MethodInjection extends AbstractInjectionFactory {

    private final AbstractInjectionFactory delegate;

    public MethodInjection(String injectionMethodName) {
        delegate = new MethodInjectionByName(injectionMethodName);
    }

    public MethodInjection(String... injectionMethodNames) {
        delegate = new MethodInjectionByName(injectionMethodNames);
    }

    public MethodInjection() {
        this("inject");
    }

    public MethodInjection(Method injectionMethod) {
        delegate = new MethodInjectionByReflectionMethod(injectionMethod);
    }

    public <T> ComponentAdapter<T> createComponentAdapter(ComponentMonitor componentMonitor, LifecycleStrategy lifecycleStrategy, Properties componentProperties, Object componentKey,
                                                   Class<T> componentImplementation, Parameter... parameters) throws PicoCompositionException {
        return delegate.createComponentAdapter(componentMonitor, lifecycleStrategy, componentProperties, componentKey, componentImplementation, parameters);
    }

    public class MethodInjectionByName extends AbstractInjectionFactory {
        private final Set<String> injectionMethodNames = new HashSet<String>();

        public MethodInjectionByName(String... injectionMethodNames) {
            for (String injectionMethodName : injectionMethodNames) {
                this.injectionMethodNames.add(injectionMethodName);
            }
        }

        public MethodInjectionByName(String injectionMethodName) {
            this.injectionMethodNames.add(injectionMethodName);
        }

        public <T> ComponentAdapter<T> createComponentAdapter(ComponentMonitor monitor, LifecycleStrategy lifecycleStrategy, Properties componentProperties, Object componentKey, Class<T> componentImplementation, Parameter... parameters) throws PicoCompositionException {
            boolean useNames = AbstractBehaviorFactory.arePropertiesPresent(componentProperties, Characteristics.USE_NAMES, true);
            return wrapLifeCycle(new MethodInjector.ByMethodName(componentKey, componentImplementation, parameters, monitor, injectionMethodNames, useNames), lifecycleStrategy);
        }
    }

    public class MethodInjectionByReflectionMethod extends AbstractInjectionFactory {
        private final Method injectionMethod;

        public MethodInjectionByReflectionMethod(Method injectionMethod) {
            this.injectionMethod = injectionMethod;
        }

        public <T> ComponentAdapter<T> createComponentAdapter(ComponentMonitor monitor, LifecycleStrategy lifecycleStrategy, Properties componentProperties, Object componentKey, Class<T> componentImplementation, Parameter... parameters) throws PicoCompositionException {
            boolean useNames = AbstractBehaviorFactory.arePropertiesPresent(componentProperties, Characteristics.USE_NAMES, true);
            if (injectionMethod.getDeclaringClass().isAssignableFrom(componentImplementation)) {
                return wrapLifeCycle(monitor.newInjector(new MethodInjector.ByReflectionMethod(componentKey, componentImplementation, parameters, monitor, injectionMethod, useNames)), lifecycleStrategy);
            } else {
                throw new PicoCompositionException("method [" + injectionMethod + "] not on impl " + componentImplementation.getName());
            }
        }
    }

}
