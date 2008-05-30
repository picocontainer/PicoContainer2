/*****************************************************************************
 * Copyright (c) PicoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the LICENSE.txt file.                                                     *
 *                                                                           *
 * Idea by Rachel Davies, Original code by various                           *
 *****************************************************************************/
package org.picocontainer.aop.dynaop;

import dynaop.Aspects;
import dynaop.Pointcuts;

import org.picocontainer.aop.ComponentPointcut;

/**
 * Aspect that addes interfaces to a component.
 *
 * @author Stephen Molitor
 */
class InterfacesComponentAspect extends ComponentAspect {

    private final Class[] interfaces;

    InterfacesComponentAspect(ComponentPointcut componentPointcut, Class[] interfaces) {
        super(componentPointcut);
        this.interfaces = interfaces;
    }

    void doRegisterAspect(Object componentKey, Aspects aspects) {
        aspects.interfaces(Pointcuts.ALL_CLASSES, interfaces);
    }

}