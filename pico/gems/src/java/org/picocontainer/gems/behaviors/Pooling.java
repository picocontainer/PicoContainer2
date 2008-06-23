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

import org.picocontainer.behaviors.AbstractBehaviorFactory;
import org.picocontainer.ComponentAdapter;
import org.picocontainer.ComponentMonitor;
import org.picocontainer.LifecycleStrategy;
import org.picocontainer.Parameter;
import org.picocontainer.PicoCompositionException;

import java.util.Properties;

public class Pooling extends AbstractBehaviorFactory {

    /**
	 * 
	 */
	private static final long serialVersionUID = -7451446445848408124L;
	private final Pooled.Context poolContext;

    public Pooling(final Pooled.Context poolContext) {
        this.poolContext = poolContext;
    }

    public Pooling() {
        poolContext = new Pooled.DefaultContext();
    }

    @Override
	public ComponentAdapter createComponentAdapter(final ComponentMonitor componentMonitor, final LifecycleStrategy lifecycleStrategy, final Properties componentProperties, final Object componentKey, final Class componentImplementation, final Parameter... parameters)
            throws PicoCompositionException {
        ComponentAdapter componentAdapter = super.createComponentAdapter(componentMonitor, lifecycleStrategy,
                                                                         componentProperties, componentKey, componentImplementation, parameters);
        Pooled behavior = new Pooled(componentAdapter, poolContext);
        //TODO
        //Characteristics.HIDE.setProcessedIn(componentCharacteristics);
        return behavior;
    }

    @Override
	public ComponentAdapter addComponentAdapter(final ComponentMonitor componentMonitor,
                                                final LifecycleStrategy lifecycleStrategy,
                                                final Properties componentProperties,
                                                final ComponentAdapter adapter) {
        return new Pooled(super.addComponentAdapter(componentMonitor,
                                         lifecycleStrategy,
                                         componentProperties,
                                         adapter), poolContext);
    }
}
