/*****************************************************************************
 * Copyright (C) PicoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the LICENSE.txt file.                                                     *
 *                                                                           *
 *****************************************************************************/
package org.picocontainer.injectors;

import org.junit.Test;
import org.picocontainer.DefaultPicoContainer;
import org.picocontainer.containers.EmptyPicoContainer;
import org.picocontainer.lifecycle.NullLifecycleStrategy;

import static junit.framework.Assert.assertNull;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * @author Paul Hammant
 */
public class NamedMethodInjectionTestCase {

    public static class Bean {
        private String something;

        public void setSomething(String blah) {
            this.something = blah;
        }
    }

    @Test
    public void containerShouldMakeUsableNamedMethodInjector() {
        DefaultPicoContainer picoContainer = new DefaultPicoContainer(new NamedMethodInjection(), new NullLifecycleStrategy(), new EmptyPicoContainer());
        picoContainer.addComponent(Bean.class);
        picoContainer.addConfig("something", "hello there");
        assertTrue(picoContainer.getComponentAdapter(Bean.class) instanceof NamedMethodInjector);
        assertEquals("hello there", picoContainer.getComponent(Bean.class).something);
    }

    @Test
    public void containerShouldMakeNamedMethodInjectorThatIsOptionalInUse() {
        DefaultPicoContainer picoContainer = new DefaultPicoContainer(new NamedMethodInjection(true), new NullLifecycleStrategy(), new EmptyPicoContainer());
        picoContainer.addComponent(Bean.class);
        assertTrue(picoContainer.getComponentAdapter(Bean.class) instanceof NamedMethodInjector);
        assertNull(picoContainer.getComponent(Bean.class).something);
    }
}