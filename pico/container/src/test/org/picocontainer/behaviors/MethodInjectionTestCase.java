/*****************************************************************************
 * Copyright (C) PicoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the LICENSE.txt file.                                                     *
 *                                                                           *
 * Original code by                                                          *
 *****************************************************************************/
package org.picocontainer.behaviors;

import static org.picocontainer.behaviors.Behaviors.caching;
import static org.picocontainer.behaviors.Behaviors.*;
import org.picocontainer.DefaultPicoContainer;
import org.picocontainer.PicoBuilder;
import org.picocontainer.MutablePicoContainer;
import org.picocontainer.lifecycle.NullLifecycleStrategy;
import org.picocontainer.monitors.NullComponentMonitor;
import org.picocontainer.injectors.ConstructorInjector;
import org.picocontainer.injectors.MethodInjector;
import static org.picocontainer.Characteristics.*;

import junit.framework.TestCase;

public class MethodInjectionTestCase extends TestCase {

    private static String MESSAGE =
        "Foo was instantiated, even though it was not required to be given it was not depended on by anything looked up";

    public static class Foo {
        private Bar bar;
        private String string;

        public void inject(Bar bar, String string) {
            this.bar = bar;
            this.string = string;
        }
    }

    public static class Bar {
    }

    public void testMethodInjection() {
        DefaultPicoContainer pico = new DefaultPicoContainer(new MethodInjection());
        pico.addComponent("hello");
        pico.addComponent(Foo.class);
        pico.addComponent(Bar.class);
        Foo foo = pico.getComponent(Foo.class);
        assertNotNull(foo.bar);
        assertNotNull(foo.string);
        assertEquals("MethodInjector-class org.picocontainer.behaviors.MethodInjectionTestCase$Foo", pico.getComponentAdapter(Foo.class).toString());
    }

    public void testMethodInjectionViaAdapter() {
        DefaultPicoContainer pico = new DefaultPicoContainer(new MethodInjection());
        pico.addComponent("hello");
        pico.addAdapter(new MethodInjector(Foo.class, Foo.class, null, new NullComponentMonitor(), new NullLifecycleStrategy(), "inject"));
        pico.addComponent(Bar.class);
        Foo foo = pico.getComponent(Foo.class);
        assertNotNull(foo.bar);
        assertNotNull(foo.string);
        assertEquals("MethodInjector-class org.picocontainer.behaviors.MethodInjectionTestCase$Foo", pico.getComponentAdapter(Foo.class).toString());
    }

    public void testMethodInjectionByBuilder() {
        MutablePicoContainer pico = new PicoBuilder().withMethodInjection().build();
        pico.addComponent("hello");
        pico.addComponent(Foo.class);
        pico.addComponent(Bar.class);
        Foo foo = pico.getComponent(Foo.class);
        assertNotNull(foo.bar);
        assertNotNull(foo.string);
        assertEquals("MethodInjector-class org.picocontainer.behaviors.MethodInjectionTestCase$Foo", pico.getComponentAdapter(Foo.class).toString());
    }

}