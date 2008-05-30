/*****************************************************************************
 * Copyright (c) PicoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the LICENSE.txt file.                                                     *
 *                                                                           *
 * Idea by Rachel Davies, Original code by various                           *
 *****************************************************************************/
package org.picocontainer.aop;

import java.lang.reflect.Method;

/**
 * Pointcut that picks methods.
 *
 * @author Stephen Molitor
 */
public interface MethodPointcut {

    /**
     * Tests to see if <code>method</code> satisfies this pointcut.
     *
     * @param method the method to match against.
     * @return true if the pointcut is satisfied, else false.
     */
    boolean picks(Method method);

}