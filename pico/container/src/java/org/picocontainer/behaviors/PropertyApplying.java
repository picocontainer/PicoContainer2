/*****************************************************************************
 * Copyright (C) PicoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the LICENSE.txt file.                                                     *
 *                                                                           *
 * Original code by                                                          *
 *****************************************************************************/
package org.picocontainer.behaviors;


import org.picocontainer.ComponentAdapter;
import org.picocontainer.Parameter;
import org.picocontainer.PicoCompositionException;
import org.picocontainer.ComponentMonitor;
import org.picocontainer.LifecycleStrategy;
import org.picocontainer.Characteristics;
import org.picocontainer.behaviors.AbstractBehaviorFactory;
import org.picocontainer.behaviors.PropertyApplicator;

import java.util.Properties;

/**
 * A {@link org.picocontainer.ComponentFactory} that creates
 * {@link PropertyApplicator} instances.
 * 
 * @author Aslak Helles&oslash;y
 * @version $Revision$
 */
public final class PropertyApplying extends AbstractBehaviorFactory {

    /**
     * {@inheritDoc}
     */
    public ComponentAdapter createComponentAdapter(ComponentMonitor componentMonitor, LifecycleStrategy lifecycleStrategy, Properties componentProperties, Object componentKey, Class componentImplementation, Parameter... parameters) throws PicoCompositionException {
        ComponentAdapter decoratedAdapter = super.createComponentAdapter(componentMonitor, lifecycleStrategy,
                                                                         componentProperties, componentKey, componentImplementation, parameters);
        removePropertiesIfPresent(componentProperties, Characteristics.PROPERTY_APPLYING);
        return new PropertyApplicator(decoratedAdapter);
    }

    public ComponentAdapter addComponentAdapter(ComponentMonitor componentMonitor,
                                                LifecycleStrategy lifecycleStrategy,
                                                Properties componentProperties,
                                                ComponentAdapter adapter) {
        removePropertiesIfPresent(componentProperties, Characteristics.PROPERTY_APPLYING);
        return new PropertyApplicator(super.addComponentAdapter(componentMonitor,
                                         lifecycleStrategy,
                                         componentProperties,
                                         adapter));
    }
}
