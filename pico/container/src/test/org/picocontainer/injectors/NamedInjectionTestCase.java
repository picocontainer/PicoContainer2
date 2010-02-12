/*****************************************************************************
 * Copyright (C) PicoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the LICENSE.txt file.                                                     *
 *                                                                           *
 *****************************************************************************/
package org.picocontainer.injectors;

import org.junit.Before;
import org.junit.Test;
import org.picocontainer.ComponentFactory;
import org.picocontainer.DefaultPicoContainer;
import org.picocontainer.containers.EmptyPicoContainer;
import org.picocontainer.lifecycle.NullLifecycleStrategy;
import org.picocontainer.tck.AbstractComponentFactoryTest;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * @author Paul Hammant
 */
public class NamedInjectionTestCase extends AbstractComponentFactoryTest {

	@Before
    public void setUp() throws Exception {
        picoContainer = new DefaultPicoContainer(createComponentFactory(), new NullLifecycleStrategy(), new EmptyPicoContainer());
    }

    protected ComponentFactory createComponentFactory() {
        return new NamedMethodInjection();
    }

    public static class Bean {
        private String something;

        public void setSomething(String blah) {
            this.something = blah;
        }
    }

    @Test public void testContainerMakesNamedMethodInjector() {
        picoContainer.addComponent(Bean.class);
        picoContainer.addConfig("something", "hello there");
        assertTrue(picoContainer.getComponentAdapter(Bean.class) instanceof NamedMethodInjector);
        assertEquals("hello there", picoContainer.getComponent(Bean.class).something);
    }
}