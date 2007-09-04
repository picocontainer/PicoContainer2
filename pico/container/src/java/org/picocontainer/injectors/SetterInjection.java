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

import java.io.Serializable;
import java.util.Properties;

import org.picocontainer.Characteristics;
import org.picocontainer.ComponentAdapter;
import org.picocontainer.ComponentMonitor;
import org.picocontainer.InjectionFactory;
import org.picocontainer.LifecycleStrategy;
import org.picocontainer.Parameter;
import org.picocontainer.PicoCompositionException;
import org.picocontainer.behaviors.AbstractBehaviorFactory;


/**
 * A {@link org.picocontainer.InjectionFactory} for JavaBeans.
 * The factory creates {@link SetterInjector}.
 *
 * @author J&ouml;rg Schaible
 */
public class SetterInjection implements InjectionFactory, Serializable {

    private final String setterMethodPrefix;

    public SetterInjection(String setterMethodPrefix) {
        this.setterMethodPrefix = setterMethodPrefix;
    }

    public SetterInjection() {
        this("set");
    }

    /**
     * Create a {@link SetterInjector}.
     * 
     * @param componentMonitor
     * @param lifecycleStrategy
     * @param componentProperties
     * @param componentKey The component's key
     * @param componentImplementation The class of the bean.
     * @param parameters Any parameters for the setters. If null the adapter
     *            solves the dependencies for all setters internally. Otherwise
     *            the number parameters must match the number of the setter.
     * @return Returns a new {@link SetterInjector}.
     * @throws PicoCompositionException if dependencies cannot be solved
     * @throws org.picocontainer.PicoCompositionException if the implementation
     *             is an interface or an abstract class.
     */
    public <T> ComponentAdapter<T> createComponentAdapter(ComponentMonitor componentMonitor, LifecycleStrategy lifecycleStrategy, Properties componentProperties, Object componentKey, Class<T> componentImplementation, Parameter... parameters)
            throws PicoCompositionException {
        boolean useNames = AbstractBehaviorFactory.removePropertiesIfPresent(componentProperties, Characteristics.USE_NAMES);
        return new SetterInjector(componentKey, componentImplementation, parameters, componentMonitor, lifecycleStrategy, setterMethodPrefix, useNames);
    }
}
