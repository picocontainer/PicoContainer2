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

import static org.junit.Assert.assertSame;
import static org.junit.Assert.fail;

import org.junit.Test;
import org.picocontainer.DefaultPicoContainer;
import org.picocontainer.PicoCompositionException;
import org.picocontainer.PicoContainer;
import org.picocontainer.aop.dynaop.ContainerLoader;

/**
 * @author Stephen Molitor
 */
public final class ContainerLoaderTestCase {

    private final ContainerLoader loader = new ContainerLoader();
    private final PicoContainer container = new DefaultPicoContainer();

    @Test public void testContainerSet() {
        loader.setContainer(container);
        assertSame(container, loader.getContainer());
    }

    @Test public void testContainerNotSet() {
        try {
            loader.getContainer();
            fail("PicoCompositionException should have been raised");
        } catch (PicoCompositionException e) {
        }
    }

}