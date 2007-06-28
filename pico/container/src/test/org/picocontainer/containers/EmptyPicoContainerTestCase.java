/*****************************************************************************
 * Copyright (C) PicoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the LICENSE.txt file.                                                     *
 *                                                                           *
 * Original code by the committers                                           *
 *****************************************************************************/

package org.picocontainer.containers;

import junit.framework.TestCase;

import org.picocontainer.PicoContainer;
import org.picocontainer.containers.EmptyPicoContainer;

/**
 * @author Mauro Talevi
 * @version $Revision:  $
 */
public class EmptyPicoContainerTestCase extends TestCase {

    public void testReturnValues() {
        PicoContainer pico = new EmptyPicoContainer();
        assertNull(pico.getComponentAdapter(null));
        assertNull(pico.getComponentAdapter(null));
        assertTrue(pico.getComponentAdapters().isEmpty());
        assertTrue(pico.getComponentAdapters(null).isEmpty());
        assertNull(pico.getComponent(null));
        assertNull(pico.getComponent(null));
        assertTrue(pico.getComponents().isEmpty());
        assertTrue(pico.getComponents(null).isEmpty());
        assertNull(pico.getParent());
    }
}
