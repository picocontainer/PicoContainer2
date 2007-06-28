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
import org.picocontainer.PicoContainer;
import org.picocontainer.PicoCompositionException;
import org.picocontainer.behaviors.AbstractBehavior;

/**
 * @author Stephen Molitor
 */
public class AspectsComponentAdapter extends AbstractBehavior {

    private final AspectsApplicator aspectsApplicator;

    public AspectsComponentAdapter(AspectsApplicator aspectsApplicator, ComponentAdapter delegate) {
        super(delegate);
        this.aspectsApplicator = aspectsApplicator;
    }

    public Object getComponentInstance(PicoContainer pico) throws
                                                           PicoCompositionException
    {
        Object component = super.getComponentInstance(pico);
        return aspectsApplicator.applyAspects(getComponentKey(), component, pico);
    }

}