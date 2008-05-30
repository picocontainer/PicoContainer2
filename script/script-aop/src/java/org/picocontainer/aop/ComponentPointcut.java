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

/**
 * Pointcut that picks component keys.
 *
 * @author Stephen Molitor
 */
public interface ComponentPointcut {

    /**
     * Returns true if the component key satisfies this pointcut.
     *
     * @param componentKey the component key.
     * @return true if the pointcut is satisfied, else false.
     */
    boolean picks(Object componentKey);

}
