/*****************************************************************************
 * Copyright (c) PicoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the LICENSE.txt file.                                                     *
 *                                                                           *
 * Idea by Rachel Davies, Original code by various                           *
 *****************************************************************************/
package org.nanocontainer.aop.defaults;

import org.nanocontainer.aop.AspectsApplicator;
import org.picocontainer.ComponentAdapter;
import org.picocontainer.Parameter;
import org.picocontainer.PicoCompositionException;
import org.picocontainer.ComponentMonitor;
import org.picocontainer.LifecycleStrategy;
import org.picocontainer.behaviors.AbstractBehaviorFactory;

import java.util.Properties;

/**
 * Produces component adapters that apply aspects to components.
 *
 * @author Stephen Molitor
 * @version $Revision$
 */
public class Aspecting extends AbstractBehaviorFactory {

    private final AspectsApplicator aspectsApplicator;

    /**
     * Creates a new <code>Aspecting</code>. The factory
     * will produce <code>Aspected</code> objects that will use
     * <code>aspectsApplicator</code> to apply aspects to components produced
     * by <code>delegate</code>.
     *
     * @param aspectsApplicator used to apply the aspects.
     */
    public Aspecting(AspectsApplicator aspectsApplicator) {
        this.aspectsApplicator = aspectsApplicator;
    }

    public ComponentAdapter createComponentAdapter(ComponentMonitor componentMonitor, LifecycleStrategy lifecycleStrategy, Properties componentProperties, Object componentKey, Class componentImplementation,
                                                   Parameter... parameters) throws PicoCompositionException {
        return new Aspected(aspectsApplicator, super.createComponentAdapter(componentMonitor, lifecycleStrategy,
                                                                                   componentProperties, componentKey,
                componentImplementation, parameters));
    }

}