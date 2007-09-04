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

import org.picocontainer.ComponentFactory;
import org.picocontainer.ComponentAdapter;
import org.picocontainer.ComponentMonitor;
import org.picocontainer.LifecycleStrategy;
import org.picocontainer.Parameter;
import org.picocontainer.PicoCompositionException;
import org.picocontainer.Characteristics;
import org.picocontainer.behaviors.AbstractBehaviorFactory;

import java.util.Properties;

/**
 * A {@link org.picocontainer.InjectionFactory} for methods.
 * The factory creates {@link MethodInjector}.
 * 
 *  @author Paul Hammant 
 */
public class MethodInjection implements ComponentFactory {

    private final String injectionMethodName;

    public MethodInjection(String injectionMethodName) {
        this.injectionMethodName = injectionMethodName;
    }

    public MethodInjection() {
        this("inject");
    }

    public <T> ComponentAdapter<T> createComponentAdapter(ComponentMonitor componentMonitor, LifecycleStrategy lifecycleStrategy, Properties componentProperties, Object componentKey,
                                                   Class<T> componentImplementation, Parameter... parameters) throws PicoCompositionException {
        boolean useNames = AbstractBehaviorFactory.removePropertiesIfPresent(componentProperties, Characteristics.USE_NAMES);
        return new MethodInjector(componentKey, componentImplementation, parameters, componentMonitor, lifecycleStrategy, injectionMethodName, useNames);
    }

}
