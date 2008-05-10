/*****************************************************************************
 * Copyright (c) PicoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the LICENSE.txt file.                                                     *
 *                                                                           *
 * Idea by Rachel Davies, Original code by various                           *
 *****************************************************************************/
package org.nanocontainer.aop;

/**
 * Pointcut that picks classes.
 *
 * @author Stephen Molitor
 */
public interface ClassPointcut {

    /**
     * Returns true if <code>clazz</code> satisfies this pointcut.
     *
     * @param clazz the class to check for a match.
     * @return true if the pointcut is satisfied, else false.
     */
    boolean picks(Class clazz);

}