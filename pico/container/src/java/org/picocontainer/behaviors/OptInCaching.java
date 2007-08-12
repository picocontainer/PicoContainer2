/*****************************************************************************
 * Copyright (c) PicoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the LICENSE.txt file.                                                     *
 *                                                                           *
 * Idea by Rachel Davies, Original code by Aslak Hellesoy and Paul Hammant   *
 *****************************************************************************/

package org.picocontainer.behaviors;

import org.picocontainer.ComponentAdapter;
import org.picocontainer.Parameter;
import org.picocontainer.PicoCompositionException;
import org.picocontainer.Characteristics;
import org.picocontainer.ComponentMonitor;
import org.picocontainer.behaviors.Cached;
import org.picocontainer.behaviors.AbstractBehaviorFactory;
import org.picocontainer.LifecycleStrategy;

import java.util.Properties;

/**
 * @author Aslak Helles&oslash;y
 * @author <a href="Rafal.Krzewski">rafal@caltha.pl</a>
 */
public class OptInCaching extends AbstractBehaviorFactory {

    public ComponentAdapter createComponentAdapter(ComponentMonitor componentMonitor, LifecycleStrategy lifecycleStrategy, Properties componentProperties, Object componentKey, Class componentImplementation, Parameter... parameters)
            throws PicoCompositionException {
        if (AbstractBehaviorFactory.removePropertiesIfPresent(componentProperties, Characteristics.CACHE)) {
            return new Cached(super.createComponentAdapter(componentMonitor,
                                                                                        lifecycleStrategy,
                                                                                        componentProperties,
                                                                                        componentKey,
                                                                                        componentImplementation,
                                                                                        parameters));
        }
        AbstractBehaviorFactory.removePropertiesIfPresent(componentProperties, Characteristics.NO_CACHE);
        return super.createComponentAdapter(componentMonitor, lifecycleStrategy,
                                            componentProperties, componentKey, componentImplementation, parameters);
    }


    public ComponentAdapter addComponentAdapter(ComponentMonitor componentMonitor,
                                                LifecycleStrategy lifecycleStrategy,
                                                Properties componentProperties,
                                                ComponentAdapter adapter) {
        if (AbstractBehaviorFactory.removePropertiesIfPresent(componentProperties, Characteristics.CACHE)) {
            return new Cached(super.addComponentAdapter(componentMonitor,
                                                                 lifecycleStrategy,
                                                                 componentProperties,
                                                                 adapter));
        }
        AbstractBehaviorFactory.removePropertiesIfPresent(componentProperties, Characteristics.NO_CACHE);
        return super.addComponentAdapter(componentMonitor,
                                         lifecycleStrategy,
                                         componentProperties,
                                         adapter);
    }
}