/*****************************************************************************
 * Copyright (c) PicoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the LICENSE.txt file.                                                     *
 *                                                                           *
 *****************************************************************************/

package org.picocontainer.injectors;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import org.junit.Test;
import org.picocontainer.ComponentFactory;
import org.picocontainer.DefaultPicoContainer;
import org.picocontainer.behaviors.Caching;
import org.picocontainer.containers.EmptyPicoContainer;
import org.picocontainer.containers.TransientPicoContainer;
import org.picocontainer.tck.AbstractComponentFactoryTest;

import java.lang.reflect.Method;

public class ReinjectionTestCase extends AbstractComponentFactoryTest {

    public static class Foo {
        private Bar bar;
        private String string;

        public Foo(Bar bar) {
            this.bar = bar;
        }

        public void doIt(String s) {
            this.string = s;
        }
    }

    public static class Bar {
    }

    private static Method DOIT = Foo.class.getMethods()[0];

    @Test public void testCachedComponentCanBeReinjectedByATransientChildContainer() {
        DefaultPicoContainer parent = new DefaultPicoContainer(new Caching().wrap(new ConstructorInjection()));
        parent.addComponent(Foo.class);
        parent.addComponent(Bar.class);
        parent.addComponent("hi");

        Foo foo = parent.getComponent(Foo.class);
        assertNotNull(foo.bar);
        assertTrue(foo.string == null);

        TransientPicoContainer tpc = new TransientPicoContainer(new Reinjection(new MethodInjection(DOIT), parent), parent);
        tpc.addComponent(Foo.class);

        foo = tpc.getComponent(Foo.class);
        assertNotNull(foo.bar);
        assertNotNull(foo.string);

        foo = parent.getComponent(Foo.class);
        assertNotNull(foo.bar);
        assertNotNull(foo.string);

    }

    protected ComponentFactory createComponentFactory() {
        return new Reinjection(new MethodInjection(DOIT), new EmptyPicoContainer());
    }

}