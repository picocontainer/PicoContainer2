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

import org.nanocontainer.aop.ComponentPointcut;

/**
 * Component pointcut that matches against a component key.
 *
 * @author Stephen Molitor
 * @version $Revision$
 */
public class KeyEqualsComponentPointcut implements ComponentPointcut {

    private final Object componentKey;

    /**
     * Creates a new <code>KeyEqualsComponentPointcut</code> that matches
     * against <code>componentKey</code>.
     *
     * @param componentKey the component key to match against.
     */
    public KeyEqualsComponentPointcut(Object componentKey) {
        if (componentKey == null) {
            throw new NullPointerException("componentKey cannot be null");
        }
        this.componentKey = componentKey;
    }

    /**
     * Tests to see if the <code>componentKey</code> matches the component key
     * passed to the constructor.
     *
     * @param componentKey the candidate component key to match against.
     * @return true if <code>componentKey</code> is equivalent to the
     *         component key passed to the constructor, else false.
     */
    public boolean picks(Object componentKey) {
        return this.componentKey.equals(componentKey);
    }

}