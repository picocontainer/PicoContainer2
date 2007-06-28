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
import junit.framework.TestCase;

/**
 * @author Stephen Molitor
 */
public class InstanceMixinFactoryTestCase extends TestCase {

    public void testCreate() {
        Object instance = "foo";
        MixinFactory factory = new InstanceMixinFactory(instance);
        assertSame(instance, factory.create(null));
    }

    public void testPropertiesNotNull() {
        MixinFactory factory = new InstanceMixinFactory("foo");
        assertNotNull(factory.getProperties());
    }

}
