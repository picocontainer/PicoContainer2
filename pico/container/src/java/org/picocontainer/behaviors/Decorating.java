/*****************************************************************************
 * Copyright (C) NanoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the LICENSE.txt file.                                                     *
 *                                                                           *
 * Original code by Joerg Schaibe                                            *
 *****************************************************************************/

package org.picocontainer.behaviors;

import org.picocontainer.ComponentAdapter;
import org.picocontainer.ComponentMonitor;
import org.picocontainer.LifecycleStrategy;
import org.picocontainer.Parameter;
import org.picocontainer.PicoCompositionException;
import org.picocontainer.behaviors.Decorated;
import org.picocontainer.behaviors.AbstractBehaviorFactory;

import java.util.Properties;


/**
 * Factory for the Assimilated. This factory will create {@link org.picocontainer.gems.behaviors.Assimilated} instances for all
 * {@link org.picocontainer.ComponentAdapter} instances created by the delegate. This will assimilate every component for a specific type.
 *
 * @author J&ouml;rg Schaible
 */
public abstract class Decorating extends AbstractBehaviorFactory implements Decorated.Decorator {

    /**
     * Create a {@link org.picocontainer.gems.behaviors.Assimilated}. This adapter will wrap the returned {@link org.picocontainer.ComponentAdapter} of the
     * deleated {@link org.picocontainer.ComponentFactory}.
     *
     * @see org.picocontainer.ComponentFactory#createComponentAdapter(org.picocontainer.ComponentMonitor, org.picocontainer.LifecycleStrategy, java.util.Properties,Object,Class, org.picocontainer.Parameter...)
     */
    public ComponentAdapter createComponentAdapter(
            ComponentMonitor componentMonitor, LifecycleStrategy lifecycleStrategy, Properties componentProperties, final Object componentKey, final Class componentImplementation, final Parameter... parameters)
            throws PicoCompositionException {
        return new Decorated(
                super.createComponentAdapter(
                        componentMonitor, lifecycleStrategy, componentProperties, componentKey, componentImplementation, parameters),
                this);
    }


    public ComponentAdapter addComponentAdapter(ComponentMonitor componentMonitor,
                                                LifecycleStrategy lifecycleStrategy,
                                                Properties componentProperties,
                                                ComponentAdapter adapter) {
        return super.addComponentAdapter(componentMonitor,
                lifecycleStrategy,
                componentProperties,
                adapter);
    }
}