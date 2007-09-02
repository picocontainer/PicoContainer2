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

import org.picocontainer.ComponentAdapter;
import org.picocontainer.ComponentMonitor;
import org.picocontainer.Parameter;
import org.picocontainer.PicoCompositionException;
import org.picocontainer.LifecycleStrategy;
import org.picocontainer.ComponentFactory;
import org.picocontainer.InjectionFactory;
import org.picocontainer.Characteristics;
import org.picocontainer.behaviors.AbstractBehaviorFactory;

import java.io.Serializable;
import java.util.Properties;

/**
 * @author Jon Tirs&eacute;n
 */
public class ConstructorInjection implements InjectionFactory, Serializable {


    public ComponentAdapter createComponentAdapter(ComponentMonitor componentMonitor, LifecycleStrategy lifecycleStrategy, Properties properties, Object componentKey,
                                                   Class componentImplementation, Parameter... parameters) throws PicoCompositionException {
        boolean useNames = AbstractBehaviorFactory.removePropertiesIfPresent(properties, Characteristics.USE_NAMES);
        return new ConstructorInjector(componentKey, componentImplementation, parameters, componentMonitor, lifecycleStrategy, useNames);
    }
}
