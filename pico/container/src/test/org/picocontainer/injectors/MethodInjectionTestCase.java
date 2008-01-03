/*****************************************************************************
 * Copyright (C) PicoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the LICENSE.txt file.                                                     *
 *                                                                           *
 * Original code by                                                          *
 *****************************************************************************/
package org.picocontainer.injectors;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.junit.Test;
import org.picocontainer.Characteristics;
import org.picocontainer.DefaultPicoContainer;
import org.picocontainer.MutablePicoContainer;
import org.picocontainer.PicoBuilder;
import org.picocontainer.lifecycle.NullLifecycleStrategy;
import org.picocontainer.monitors.NullComponentMonitor;

public class MethodInjectionTestCase {

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

    @Test public void testMethodInjection() {
        DefaultPicoContainer pico = new DefaultPicoContainer(new MethodInjection());
        pico.addComponent("hello");
        pico.addComponent(Foo.class);
        pico.addComponent(Bar.class);
        Foo foo = pico.getComponent(Foo.class);
        assertNotNull(foo.bar);
        assertNotNull(foo.string);
        assertEquals("MethodInjector-class org.picocontainer.injectors.MethodInjectionTestCase$Foo", pico.getComponentAdapter(Foo.class).toString());
    }

    @Test public void testMethodInjectionViaCharacteristics() {
        DefaultPicoContainer pico = new DefaultPicoContainer();
        pico.addComponent("hello");
        pico.as(Characteristics.METHOD_INJECTION).addComponent(Foo.class);
        pico.addComponent(Bar.class);
        Foo foo = pico.getComponent(Foo.class);
        assertNotNull(foo.bar);
        assertNotNull(foo.string);
        assertEquals("MethodInjector-class org.picocontainer.injectors.MethodInjectionTestCase$Foo", pico.getComponentAdapter(Foo.class).toString());
    }

    @Test public void testMethodInjectionViaAdapter() {
        DefaultPicoContainer pico = new DefaultPicoContainer(new MethodInjection());
        pico.addComponent("hello");
        pico.addAdapter(new MethodInjector(Foo.class, Foo.class, null, new NullComponentMonitor(), new NullLifecycleStrategy(), "inject", false));
        pico.addComponent(Bar.class);
        Foo foo = pico.getComponent(Foo.class);
        assertNotNull(foo.bar);
        assertNotNull(foo.string);
        assertEquals("MethodInjector-class org.picocontainer.injectors.MethodInjectionTestCase$Foo", pico.getComponentAdapter(Foo.class).toString());
    }

    @Test public void testMethodInjectionByBuilder() {
        MutablePicoContainer pico = new PicoBuilder().withMethodInjection().build();
        pico.addComponent("hello");
        pico.addComponent(Foo.class);
        pico.addComponent(Bar.class);
        Foo foo = pico.getComponent(Foo.class);
        assertNotNull(foo.bar);
        assertNotNull(foo.string);
        assertEquals("MethodInjector-class org.picocontainer.injectors.MethodInjectionTestCase$Foo", pico.getComponentAdapter(Foo.class).toString());
    }

}