/*****************************************************************************
 * Copyright (C) PicoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the LICENSE.txt file.                                                     *
 *                                                                           *
 *****************************************************************************/
package org.picocontainer.injectors;

import org.picocontainer.ComponentFactory;
import org.picocontainer.ComponentMonitor;
import org.picocontainer.PicoContainer;
import org.picocontainer.InjectionFactory;
import org.picocontainer.ComponentAdapter;
import org.picocontainer.LifecycleStrategy;
import org.picocontainer.Parameter;
import org.picocontainer.PicoCompositionException;
import org.picocontainer.Characteristics;
import org.picocontainer.behaviors.AbstractBehaviorFactory;

import java.io.Serializable;
import java.util.Properties;

/** @author Paul Hammant */
public class MultiInjection implements InjectionFactory, Serializable {
    private final String setterPrefix;

    public MultiInjection(String setterPrefix) {
        this.setterPrefix = setterPrefix;
    }

    public MultiInjection() {
        this("set");
    }

    public <T> ComponentAdapter<T> createComponentAdapter(ComponentMonitor componentMonitor,
                                                          LifecycleStrategy lifecycleStrategy,
                                                          Properties componentProperties,
                                                          Object componentKey,
                                                          Class<T> componentImplementation,
                                                          Parameter... parameters) throws PicoCompositionException {
        boolean useNames = AbstractBehaviorFactory.removePropertiesIfPresent(componentProperties, Characteristics.USE_NAMES);
        return new MultiInjector(componentKey, componentImplementation, parameters, componentMonitor, lifecycleStrategy, setterPrefix, useNames);
    }
}
