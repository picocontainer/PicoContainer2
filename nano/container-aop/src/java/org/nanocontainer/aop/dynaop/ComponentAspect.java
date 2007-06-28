/*****************************************************************************
 * Copyright (c) PicoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the LICENSE.txt file.                                                     *
 *                                                                           *
 * Idea by Rachel Davies, Original code by various                           *
 *****************************************************************************/
package org.nanocontainer.aop.dynaop;

import dynaop.Aspects;
import org.nanocontainer.aop.ComponentPointcut;

/**
 * Aspect that applies to the set of components matched by a
 * <code>org.nanocontainer.aop.ComponentPointcut</code>.
 *
 * @author Stephen Molitor
 * @version $Revision$
 */
abstract class ComponentAspect {

    private final ComponentPointcut componentPointcut;

    /**
     * Creates a new <code>ComponentAspect</code> with the given addComponent
     * pointcut.
     *
     * @param componentPointcut the component pointcut.
     */
    ComponentAspect(ComponentPointcut componentPointcut) {
        this.componentPointcut = componentPointcut;
    }

    /**
     * Registers this aspect with <code>aspects</code> if the addComponent
     * pointcut passed to the constructor picks the <code>componentKey</code>.
     * Template method that calls <code>doRegisterAspect</code> if the
     * component key matches.
     *
     * @param componentKey the component key to match against.
     * @param aspects      the <code>dynaop.Aspects</code> collection.
     */
    final void registerAspect(Object componentKey, Aspects aspects) {
        if (componentPointcut.picks(componentKey)) {
            doRegisterAspect(componentKey, aspects);
        }
    }

    /**
     * Called by <code>registerAspect</code> to
     *
     * @param componentKey the component key
     * @param aspects aspects to register
     */
    abstract void doRegisterAspect(Object componentKey, Aspects aspects);

}