/*****************************************************************************
 * Copyright (c) PicoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the LICENSE.txt file.                                                     *
 *                                                                           *
 * Idea by Rachel Davies, Original code by various                           *
 *****************************************************************************/
package org.picocontainer.aop.defaults;

import org.picocontainer.ComponentAdapter;
import org.picocontainer.PicoContainer;
import org.picocontainer.PicoCompositionException;
import org.picocontainer.aop.AspectsApplicator;
import org.picocontainer.behaviors.AbstractBehavior;

import java.lang.reflect.Type;

/**
 * @author Stephen Molitor
 */
public class Aspected extends AbstractBehavior {

    private final AspectsApplicator aspectsApplicator;

    public Aspected(AspectsApplicator aspectsApplicator, ComponentAdapter delegate) {
        super(delegate);
        this.aspectsApplicator = aspectsApplicator;
    }

    public Object getComponentInstance(PicoContainer pico, Type into) throws PicoCompositionException {
        Object component = super.getComponentInstance(pico, into);
        return aspectsApplicator.applyAspects(getComponentKey(), component, pico);
    }

    public String getDescriptor() {
        return "Aspected";
    }

}