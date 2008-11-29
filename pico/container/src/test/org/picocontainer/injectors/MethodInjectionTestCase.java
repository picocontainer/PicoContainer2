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
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.lang.reflect.Method;

import org.junit.Test;
import org.picocontainer.Characteristics;
import org.picocontainer.DefaultPicoContainer;
import org.picocontainer.MutablePicoContainer;
import org.picocontainer.PicoBuilder;
import org.picocontainer.PicoCompositionException;
import org.picocontainer.annotations.Nullable;
import org.picocontainer.lifecycle.NullLifecycleStrategy;
import org.picocontainer.monitors.NullComponentMonitor;

public class MethodInjectionTestCase {

    public static interface IFoo {
        void inject(Bar bar, String string);
    }
    public static class Foo implements IFoo {
        private Bar bar;
        private String string;

        public void inject(Bar bar, String string) {
            this.bar = bar;
            this.string = string;
        }
    }

    public static class Bar {
        public Bar() {
        }
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

    @Test public void testMethodInjectionViaMethodDef() {
        Method mthd = Foo.class.getMethods()[0];
        DefaultPicoContainer pico = new DefaultPicoContainer(new MethodInjection(mthd));
        pico.addComponent("hello");
        pico.addComponent(Foo.class);
        pico.addComponent(new Bar());
        Foo foo = pico.getComponent(Foo.class);
        assertNotNull(foo.bar);
        assertNotNull(foo.string);
        assertEquals("ReflectionMethodInjector["+mthd+"]-class org.picocontainer.injectors.MethodInjectionTestCase$Foo", pico.getComponentAdapter(Foo.class).toString());
    }

    @Test public void testMethodInjectionViaMethodDefViaInterface() {
        Method mthd = IFoo.class.getMethods()[0];
        DefaultPicoContainer pico = new DefaultPicoContainer(new MethodInjection(mthd));
        pico.addComponent("hello");
        pico.addComponent(Foo.class);
        pico.addComponent(new Bar());
        Foo foo = pico.getComponent(Foo.class);
        assertNotNull(foo.bar);
        assertNotNull(foo.string);
        assertEquals("ReflectionMethodInjector["+mthd+"]-class org.picocontainer.injectors.MethodInjectionTestCase$Foo", pico.getComponentAdapter(Foo.class).toString());
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

    public static class Foo2 implements IFoo {
        private Bar bar;
        private String string;

        public void inject(Bar bar, @Nullable String string) {
            this.bar = bar;
            this.string = string;
        }
    }

    @Test public void testMethodInjectionWithAllowedNullableParam() {
        DefaultPicoContainer pico = new DefaultPicoContainer(new MethodInjection());
        pico.addComponent(Foo2.class);
        pico.addComponent(Bar.class);
        Foo2 foo = pico.getComponent(Foo2.class);
        assertNotNull(foo.bar);
        assertTrue(foo.string == null);
        assertEquals("MethodInjector-class org.picocontainer.injectors.MethodInjectionTestCase$Foo2", pico.getComponentAdapter(Foo2.class).toString());
    }

    @Test public void testMethodInjectionWithDisallowedNullableParam() {
        DefaultPicoContainer pico = new DefaultPicoContainer(new MethodInjection());
        pico.addComponent(Foo.class);
        pico.addComponent(Bar.class);
        try {
            Foo foo = pico.getComponent(Foo.class);
            fail("should have barfed");
        } catch (PicoCompositionException e) {
        }
    }


}