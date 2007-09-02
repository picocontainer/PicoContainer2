/*****************************************************************************
 * Copyright (C) PicoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the LICENSE.txt file.                                                     *
 *                                                                           *
 *****************************************************************************/
package org.picocontainer.injectors;

import org.picocontainer.Parameter;
import org.picocontainer.ComponentMonitor;
import org.picocontainer.LifecycleStrategy;
import org.picocontainer.PicoContainer;
import org.picocontainer.PicoCompositionException;
import org.picocontainer.annotations.Inject;

import java.lang.reflect.Constructor;
import java.util.Set;

/** @author Paul Hammant */
public class MultiInjector extends AbstractInjector {

    private final ConstructorInjector constuctorInjector;
    private final SetterInjector setterInjector;
    private AnnotatedMethodInjector annotatedMethodInjector;

    public MultiInjector(Object componentKey,
                         Class componentImplementation,
                         Parameter[] parameters,
                         ComponentMonitor componentMonitor, LifecycleStrategy lifecycleStrategy, String setterPrefix, boolean useNames) {
        super(componentKey, componentImplementation, parameters, componentMonitor, lifecycleStrategy, useNames);
        constuctorInjector = new ConstructorInjector(componentKey, componentImplementation, parameters, componentMonitor, lifecycleStrategy, useNames);
        setterInjector = new SetterInjector(componentKey, componentImplementation, parameters, componentMonitor, lifecycleStrategy, setterPrefix, useNames) {
            protected Object getOrMakeInstance(PicoContainer container,
                                               Constructor constructor,
                                               ComponentMonitor componentMonitor) {
                return constuctorInjector.getComponentInstance(container);
            }

            protected Constructor getConstructor() {
                return null;   
            }

            protected void unsatisfiedDependencies(PicoContainer container, Set<Class> unsatisfiableDependencyTypes) {
            }
        };
        annotatedMethodInjector = new AnnotatedMethodInjector(componentKey, componentImplementation, parameters, componentMonitor, lifecycleStrategy, Inject.class, useNames) {
            protected Object getOrMakeInstance(PicoContainer container,
                                               Constructor constructor,
                                               ComponentMonitor componentMonitor)  {
                return setterInjector.getComponentInstance(container);
            }

            protected Constructor getConstructor() {
                return null;
            }

            protected void unsatisfiedDependencies(PicoContainer container, Set<Class> unsatisfiableDependencyTypes) {
            }
        };
    }

    public Object getComponentInstance(PicoContainer container) throws PicoCompositionException {
        return annotatedMethodInjector.getComponentInstance(container);
    }

    public void verify(PicoContainer container) throws PicoCompositionException {
        constuctorInjector.verify(container);
        constuctorInjector.verify(container);
        annotatedMethodInjector.verify(container);
    }

    public String getDescriptor() {
        return "MultiInjector";
    }
}
