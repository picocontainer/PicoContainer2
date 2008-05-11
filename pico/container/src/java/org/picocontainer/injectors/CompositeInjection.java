/*****************************************************************************
 * Copyright (C) PicoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the LICENSE.txt file.                                                     *
 *                                                                           *
 *****************************************************************************/
package org.picocontainer.injectors;

import org.picocontainer.*;
import org.picocontainer.behaviors.AbstractBehaviorFactory;

import java.io.Serializable;
import java.util.Properties;

/**
 * A Composite of other types on InjectionFactories - pass them into the varargs constructor.
 * 
 * @author Paul Hammant
 */
public class CompositeInjection extends AbstractInjectionFactory {

    private static final long serialVersionUID = 1962189227810901031L;    

    private final InjectionFactory[] injectionFactories;

    public CompositeInjection(InjectionFactory... injectionFactories) {
        this.injectionFactories = injectionFactories;
    }

    public <T> ComponentAdapter<T> createComponentAdapter(ComponentMonitor componentMonitor,
                                                          LifecycleStrategy lifecycleStrategy,
                                                          Properties componentProperties,
                                                          Object componentKey,
                                                          Class<T> componentImplementation,
                                                          Parameter... parameters) throws PicoCompositionException {

        Injector[] injectors = new Injector[injectionFactories.length];

        for (int i = 0; i < injectionFactories.length; i++) {
            InjectionFactory injectionFactory = injectionFactories[i];
            injectors[i] = (Injector) injectionFactory.createComponentAdapter(componentMonitor,
                    lifecycleStrategy, componentProperties, componentKey, componentImplementation, parameters);
        }

        boolean useNames = AbstractBehaviorFactory.arePropertiesPresent(componentProperties, Characteristics.USE_NAMES);
        return new CompositeInjector(componentKey, componentImplementation, parameters,
                componentMonitor, lifecycleStrategy,
                useNames, injectors);
    }
}