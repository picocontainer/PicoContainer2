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
 * A {@link org.picocontainer.InjectionFactory} for constructors.
 * The factory creates {@link ConstructorInjector}.
 * 
 * @author Paul Hammant 
 * @author Jon Tirs&eacute;n
 */
public class ConstructorInjection extends AbstractInjectionFactory  {
    private static final long serialVersionUID = -6044681748649376149L;

    private final boolean rememberChosenConstructor;

    public ConstructorInjection(boolean rememberChosenConstructor) {
        this.rememberChosenConstructor = rememberChosenConstructor;
    }

    public ConstructorInjection() {
        this(true);
    }

    public <T> ComponentAdapter<T> createComponentAdapter(ComponentMonitor componentMonitor, LifecycleStrategy lifecycleStrategy, Properties properties, Object componentKey,
                                                   Class<T> componentImplementation, Parameter... parameters) throws PicoCompositionException {
        boolean useNames = AbstractBehaviorFactory.arePropertiesPresent(properties, Characteristics.USE_NAMES);
        return componentMonitor.newInjectionFactory(new ConstructorInjector(componentKey, componentImplementation, parameters, componentMonitor, lifecycleStrategy, useNames, rememberChosenConstructor));
    }
}
