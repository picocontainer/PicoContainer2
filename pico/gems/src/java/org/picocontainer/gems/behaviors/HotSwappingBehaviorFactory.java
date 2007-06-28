/*****************************************************************************
 * Copyright (c) PicoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the LICENSE.txt file.                                                     *
 *                                                                           *
 * Idea by Rachel Davies, Original code by Aslak Hellesoy and Paul Hammant   *
 *****************************************************************************/

package org.picocontainer.gems.behaviors;

import org.picocontainer.ComponentAdapter;
import org.picocontainer.Parameter;
import org.picocontainer.PicoCompositionException;
import org.picocontainer.ComponentCharacteristics;
import org.picocontainer.ComponentMonitor;
import org.picocontainer.LifecycleStrategy;
import org.picocontainer.behaviors.AbstractBehaviorFactory;


/**
 * Hides implementation.
 * 
 * @author Paul Hammant
 * @author Aslak Helles&oslash;y
 * @version $Revision$
 * @see HotSwappingBehavior
 */
public class HotSwappingBehaviorFactory extends AbstractBehaviorFactory {

    public ComponentAdapter createComponentAdapter(ComponentMonitor componentMonitor, LifecycleStrategy lifecycleStrategy, ComponentCharacteristics componentCharacteristics, Object componentKey, Class componentImplementation, Parameter... parameters)
            throws PicoCompositionException
    {
        ComponentAdapter componentAdapter = super.createComponentAdapter(componentMonitor, lifecycleStrategy,
                                                                         componentCharacteristics, componentKey, componentImplementation, parameters);
        return new HotSwappingBehavior(componentAdapter);
    }
}
