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
import static org.junit.Assert.assertTrue;import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertEquals;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.picocontainer.ComponentFactory;
import org.picocontainer.DefaultPicoContainer;
import org.picocontainer.ComponentMonitor;
import org.picocontainer.monitors.AbstractComponentMonitor;
import org.picocontainer.behaviors.Caching;
import org.picocontainer.containers.EmptyPicoContainer;
import org.picocontainer.containers.TransientPicoContainer;
import org.picocontainer.tck.AbstractComponentFactoryTest;
import static org.picocontainer.tck.MockFactory.mockeryWithCountingNamingScheme;
import org.jmock.integration.junit4.JMock;
import org.jmock.Mockery;
import org.jmock.Expectations;

import java.lang.reflect.Method;

@RunWith(JMock.class)
public class ReinjectionTestCase extends AbstractComponentFactoryTest {

    private Mockery mockery = mockeryWithCountingNamingScheme();

    public static class Foo {
        private Bar bar;
        private String string;

        public Foo(Bar bar) {
            this.bar = bar;
        }

        public int doIt(String s) {
            this.string = s;
            return Integer.parseInt(s) / 2;
        }
    }

    public static class Bar {
    }

    private static Method DOIT = Foo.class.getMethods()[0];

    @Test public void testCachedComponentCanBeReinjectedByATransientChildContainer() {
        DefaultPicoContainer parent = new DefaultPicoContainer(new Caching().wrap(new ConstructorInjection()));
        parent.addComponent(Foo.class);
        parent.addComponent(Bar.class);
        parent.addComponent("12");

        Foo foo = parent.getComponent(Foo.class);
        assertNotNull(foo.bar);
        assertTrue(foo.string == null);

        TransientPicoContainer tpc = new TransientPicoContainer(new Reinjection(new MethodInjection(DOIT), parent), parent);
        tpc.addComponent(Foo.class);

        Foo foo2 = tpc.getComponent(Foo.class);
        assertSame(foo, foo2);
        assertNotNull(foo2.bar);
        assertNotNull(foo2.string);

        Foo foo3 = parent.getComponent(Foo.class);
        assertSame(foo, foo3);
        assertNotNull(foo3.bar);
        assertNotNull(foo3.string);

    }

    @Test public void testCachedComponentCanBeReinjectedByATransientReinjector() {
        final DefaultPicoContainer parent = new DefaultPicoContainer(new Caching().wrap(new ConstructorInjection()));
        parent.addComponent(Foo.class);
        parent.addComponent(Bar.class);
        parent.addComponent("12");

        Foo foo = parent.getComponent(Foo.class);
        assertNotNull(foo.bar);
        assertTrue(foo.string == null);

        // nothing expected to be called. Yet a least.
        final ComponentMonitor cm = mockery.mock(ComponentMonitor.class);
        Reinjector reinjector = new Reinjector(parent, cm);

        int result = (Integer) reinjector.reinject(Foo.class, DOIT);
        assertEquals(6, result);

        Foo foo3 = parent.getComponent(Foo.class);
        assertSame(foo, foo3);
        assertNotNull(foo3.bar);
        assertNotNull(foo3.string);
        assertEquals("12", foo3.string);

    }

    protected ComponentFactory createComponentFactory() {
        return new Reinjection(new MethodInjection(DOIT), new EmptyPicoContainer());
    }

}