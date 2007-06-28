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
import org.picocontainer.behaviors.AbstractBehaviorFactory;
import org.picocontainer.behaviors.PropertyApplyingBehavior;

/**
 * A {@link org.picocontainer.ComponentFactory} that creates
 * {@link PropertyApplyingBehavior} instances.
 * 
 * @author Aslak Helles&oslash;y
 * @version $Revision$
 * @since 1.0
 */
public final class PropertyApplyingBehaviorFactory extends AbstractBehaviorFactory {

    /**
     * {@inheritDoc}
     */
    public ComponentAdapter createComponentAdapter(ComponentMonitor componentMonitor, LifecycleStrategy lifecycleStrategy, ComponentCharacteristics componentCharacteristics, Object componentKey, Class componentImplementation, Parameter... parameters) throws
                                                                                                                                                                                                                                                         PicoCompositionException
    {
        ComponentAdapter decoratedAdapter = super.createComponentAdapter(componentMonitor, lifecycleStrategy,
                                                                         componentCharacteristics, componentKey, componentImplementation, parameters);
        return new PropertyApplyingBehavior(decoratedAdapter);
    }

}
