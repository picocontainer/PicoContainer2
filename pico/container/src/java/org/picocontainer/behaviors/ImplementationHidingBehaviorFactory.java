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
import org.picocontainer.ComponentCharacteristics;
import org.picocontainer.ComponentMonitor;
import org.picocontainer.LifecycleStrategy;
import org.picocontainer.Characterizations;
import org.picocontainer.behaviors.AbstractBehaviorFactory;

/**
 * @author Aslak Helles&oslash;y
 * @see org.picocontainer.gems.adapters.HotSwappingComponentAdapterFactory for a more feature-rich version of the class
 * @since 1.2, moved from package {@link org.picocontainer.alternatives}
 */
public class ImplementationHidingBehaviorFactory extends AbstractBehaviorFactory {

    public ComponentAdapter createComponentAdapter(ComponentMonitor componentMonitor, LifecycleStrategy lifecycleStrategy, ComponentCharacteristics componentCharacteristics, Object componentKey, Class componentImplementation, Parameter... parameters) throws
                                                                                                                                                                                                                                                         PicoCompositionException
    {
        ComponentAdapter componentAdapter = super.createComponentAdapter(componentMonitor, lifecycleStrategy,
                                                                         componentCharacteristics, componentKey, componentImplementation, parameters);
        Characterizations.HIDE.setAsProcessedIfSoCharacterized(componentCharacteristics);
        return new ImplementationHidingBehavior(componentAdapter);
    }
}
