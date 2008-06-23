/*****************************************************************************
 * Copyright (C) PicoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the LICENSE.txt file.                                                     *
 *                                                                           *
 * Original code by                                                          *
 *****************************************************************************/
package org.picocontainer.gems.behaviors;

import org.picocontainer.LifecycleStrategy;
import org.picocontainer.ComponentAdapter;
import org.picocontainer.Parameter;
import org.picocontainer.PicoCompositionException;
import org.picocontainer.ComponentMonitor;
import org.picocontainer.Characteristics;
import org.picocontainer.behaviors.AbstractBehaviorFactory;

import java.util.Properties;

public class AsmImplementationHiding extends AbstractBehaviorFactory {

    /**
	 * 
	 */
	private static final long serialVersionUID = -4626804053729853698L;

	@Override
	public ComponentAdapter createComponentAdapter(final ComponentMonitor componentMonitor,
                                                   final LifecycleStrategy lifecycleStrategy,
                                                   final Properties componentProperties,
                                                   final Object componentKey,
                                                   final Class componentImplementation,
                                                   final Parameter... parameters) throws PicoCompositionException {
        if (AbstractBehaviorFactory.removePropertiesIfPresent(componentProperties, Characteristics.NO_HIDE_IMPL)) {
            return super.createComponentAdapter(componentMonitor, lifecycleStrategy,
                                                componentProperties, componentKey, componentImplementation, parameters);
        }
        AbstractBehaviorFactory.removePropertiesIfPresent(componentProperties, Characteristics.HIDE_IMPL);
        ComponentAdapter componentAdapter = super.createComponentAdapter(componentMonitor,
                                                                         lifecycleStrategy,
                                                                         componentProperties,
                                                                         componentKey,
                                                                         componentImplementation,
                                                                         parameters);
        return new HiddenImplementation(componentAdapter);
    }

    @Override
	public ComponentAdapter addComponentAdapter(final ComponentMonitor componentMonitor,
                                                final LifecycleStrategy lifecycleStrategy,
                                                final Properties componentProperties,
                                                final ComponentAdapter adapter) {
        if (AbstractBehaviorFactory.removePropertiesIfPresent(componentProperties, Characteristics.NO_HIDE_IMPL)) {
            return super.addComponentAdapter(componentMonitor,
                                             lifecycleStrategy,
                                             componentProperties,
                                             adapter);
        }
        AbstractBehaviorFactory.removePropertiesIfPresent(componentProperties, Characteristics.HIDE_IMPL);
        return new HiddenImplementation(super.addComponentAdapter(componentMonitor,
                                                                          lifecycleStrategy,
                                                                          componentProperties,
                                                                          adapter));
    }
}