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

import org.nanocontainer.aop.ClassPointcut;

/**
 * Adapts a <code>dynaop.ClassPointcut</code> to the
 * <code>org.nanocontainer.aop.ClassPointcut</code> interface.
 *
 * @author Stephen Molitor
 */
class DynaopClassPointcut implements dynaop.ClassPointcut, ClassPointcut {

    private final dynaop.ClassPointcut delegate;

    /**
     * Creates a new <code>DynaoClassPointcut</code> that will delegate to the
     * given <code>dyanop.ClassPointcut</code>.
     *
     * @param delegate the <code>dyanop.ClassPointcut</code> to delegate to.
     */
    DynaopClassPointcut(dynaop.ClassPointcut delegate) {
        this.delegate = delegate;
    }

    /**
     * Returns true if the <code>dynaop.ClassPointcut</code> delegate passed
     * to the constructor picks <code>clazz</code>.
     *
     * @param clazz the class to match against.
     * @return true if this pointcut picks <code>clazz</code>, else false.
     */
    public boolean picks(Class clazz) {
        return delegate.picks(clazz);
    }

}