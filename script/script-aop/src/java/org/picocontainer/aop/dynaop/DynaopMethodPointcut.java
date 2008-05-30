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

import org.picocontainer.aop.MethodPointcut;

import java.lang.reflect.Method;

/**
 * Adapts a <code>dynaop.MethodPointcut</code> to the
 * <code>org.picocontainer.aop.MethodPointcut</code> interface.
 *
 * @author Stephen Molitor
 */
class DynaopMethodPointcut implements dynaop.MethodPointcut, MethodPointcut {

    private final dynaop.MethodPointcut delegate;

    /**
     * Creates a new <code>DynaopMethodPointcut</code> that will delegate to
     * the given <code>dynaop.MethodPointcut</code>.
     *
     * @param delegate the <code>dynaop.MethodPointcut</code> to delegate to.
     */
    DynaopMethodPointcut(dynaop.MethodPointcut delegate) {
        this.delegate = delegate;
    }

    /**
     * Returns true if the delegate <code>dynaop.MethodPointcut</code> passed
     * to the constructor picks <code>method</code>.
     *
     * @param method the method to match against.
     * @return true if this pointcut picks <code>method</code>, else false.
     */
    public boolean picks(Method method) {
        return delegate.picks(method);
    }

}