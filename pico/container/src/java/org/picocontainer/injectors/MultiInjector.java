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

import java.lang.reflect.Constructor;
import java.util.Set;

/** @author Paul Hammant */
public class MultiInjector extends AbstractInjector {

    private final ConstructorInjector constuctorInjector;
    private final SetterInjector setterInjector;

    public MultiInjector(Object componentKey,
                         Class componentImplementation,
                         Parameter[] parameters,
                         ComponentMonitor componentMonitor, LifecycleStrategy lifecycleStrategy, String setterPrefix) {
        super(componentKey, componentImplementation, parameters, componentMonitor, lifecycleStrategy);
        constuctorInjector = new ConstructorInjector(componentKey, componentImplementation, parameters, componentMonitor, lifecycleStrategy);
        setterInjector = new SetterInjector(componentKey, componentImplementation, parameters, componentMonitor, lifecycleStrategy, setterPrefix) {
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
    }

    public Object getComponentInstance(PicoContainer container) throws PicoCompositionException {
        return setterInjector.getComponentInstance(container);
    }

    public void verify(PicoContainer container) throws PicoCompositionException {
        constuctorInjector.verify(container);
        constuctorInjector.verify(container);
    }

}
