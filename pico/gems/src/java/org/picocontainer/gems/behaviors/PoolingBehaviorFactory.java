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

public class PoolingBehaviorFactory extends AbstractBehaviorFactory {

    private final PoolingBehavior.Context poolContext;

    public PoolingBehaviorFactory(PoolingBehavior.Context poolContext) {
        this.poolContext = poolContext;
    }

    public PoolingBehaviorFactory() {
        poolContext = new PoolingBehavior.DefaultContext();
    }

    public ComponentAdapter createComponentAdapter(ComponentMonitor componentMonitor, LifecycleStrategy lifecycleStrategy, Properties componentProperties, Object componentKey, Class componentImplementation, Parameter... parameters)
            throws PicoCompositionException {
        ComponentAdapter componentAdapter = super.createComponentAdapter(componentMonitor, lifecycleStrategy,
                                                                         componentProperties, componentKey, componentImplementation, parameters);
        PoolingBehavior behavior = new PoolingBehavior(componentAdapter, poolContext);
        //TODO
        //Characteristics.HIDE.setProcessedIn(componentCharacteristics);
        return behavior;
    }

    public ComponentAdapter addComponentAdapter(ComponentMonitor componentMonitor,
                                                LifecycleStrategy lifecycleStrategy,
                                                Properties componentProperties,
                                                ComponentAdapter adapter) {
        return new PoolingBehavior(super.addComponentAdapter(componentMonitor,
                                         lifecycleStrategy,
                                         componentProperties,
                                         adapter), poolContext);
    }
}
