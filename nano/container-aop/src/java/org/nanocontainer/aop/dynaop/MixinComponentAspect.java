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
import dynaop.MixinFactory;
import dynaop.Pointcuts;
import org.nanocontainer.aop.ComponentPointcut;

/**
 * Mixin aspect that is applied to the components that match a addComponent
 * pointcut.
 *
 * @author Stephen Molitor
 * @version $Revision$
 */
final class MixinComponentAspect extends ComponentAspect {

    private final Class[] mixinInterfaces;
    private final MixinFactory mixinFactory;

    /**
     * Creates a new <code>MixinComponentAspect</code> from the given
     * component pointcut and mixin class. The aspected component will implement
     * the provided set of mixin interfaces.
     *
     * @param componentPointcut the components to introduce the mixin to.
     * @param mixinInterfaces   the mixin interfaces the aspected component will
     *                          implement.
     * @param mixinFactory      the mixin factory.
     */
    MixinComponentAspect(ComponentPointcut componentPointcut, Class[] mixinInterfaces, MixinFactory mixinFactory) {
        super(componentPointcut);
        this.mixinInterfaces = mixinInterfaces;
        this.mixinFactory = mixinFactory;
    }

    void doRegisterAspect(Object componentKey, Aspects aspects) {
        aspects.mixin(Pointcuts.ALL_CLASSES, mixinInterfaces, mixinFactory);
    }

}