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

import dynaop.MixinFactory;
import dynaop.Proxy;

import java.util.Properties;

/**
 * Produces mixin advice from a mixin instance object.
 *
 * @author Stephen Molitor
 * @version $Revision$
 */
public class InstanceMixinFactory implements MixinFactory {

    private final Object instance;

    /**
     * Creates a new <code>InstanceMixinFactory</code> with the given mixin
     * instance.
     *
     * @param instance the mixin instance.
     */
    public InstanceMixinFactory(Object instance) {
        this.instance = instance;
    }

    /**
     * Returns the mixin instance passed to the constructor.
     *
     * @param proxy not used.
     * @return the mixin instance object passed to the constructor.
     */
    public Object create(Proxy proxy) {
        return instance;
    }

    /**
     * Used for debugging.
     *
     * @return a set of properties useful for debugging.
     */
    public Properties getProperties() {
        Properties properties = new Properties();
        properties.setProperty("advice", "instance mixin");
        return properties;
    }

}