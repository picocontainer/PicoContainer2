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

import static junit.framework.Assert.fail;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertEquals;

import junit.framework.Assert;
import org.junit.Test;
import org.picocontainer.DefaultPicoContainer;
import org.picocontainer.MutablePicoContainer;
import org.picocontainer.lifecycle.NullLifecycleStrategy;
import org.picocontainer.monitors.NullComponentMonitor;

public class NamedFieldInjectorTestCase {

    public static class Helicopter {
        private PogoStick pogo;
    }

    public static class Biplane {
        private String wing1;
        private String wing2;
    }

    public static class Monoplane {
        private String wing1;
    }

    public static class PogoStick {
    }

    @Test public void testFieldInjectionByType() {
        MutablePicoContainer pico = new DefaultPicoContainer();
        pico.addAdapter(new NamedFieldInjector(Helicopter.class, Helicopter.class, null,
                                                    new NullComponentMonitor(), " aa bb cc pogo dd "));
        pico.addComponent(PogoStick.class, new PogoStick());
        Helicopter chopper = pico.getComponent(Helicopter.class);
        assertNotNull(chopper);
        assertNotNull(chopper.pogo);
    }

    @Test public void testFieldInjectionByName() {
        MutablePicoContainer pico = new DefaultPicoContainer();
        pico.addAdapter(new NamedFieldInjector(Biplane.class, Biplane.class, null,
                                                    new NullComponentMonitor(), " aa wing1 cc wing2 dd "));
        pico.addConfig("wing1", "hello");
        pico.addConfig("wing2", "goodbye");
        Biplane biplane = pico.getComponent(Biplane.class);
        assertNotNull(biplane);
        assertNotNull(biplane.wing1);
        assertEquals("hello", biplane.wing1);
        assertNotNull(biplane.wing2);
        assertEquals("goodbye", biplane.wing2);
    }


    @Test public void testFieldInjectionByTypeWhereNoMatch() {
        MutablePicoContainer pico = new DefaultPicoContainer();
        pico.setName("parent");
        pico.addAdapter(new NamedFieldInjector(Monoplane.class, Monoplane.class, null,
                new NullComponentMonitor(), " aa wing1 cc wing2 dd "));
        try {
            pico.getComponent(Monoplane.class);
            fail("should have barfed");
        } catch (AbstractInjector.UnsatisfiableDependenciesException e) {
            String expected = "Monoplane has unsatisfied dependency for fields [String.wing1] from parent:1<|";
            String actual = e.getMessage().replace("java.lang.","");
            actual = actual.replace(NamedFieldInjectorTestCase.class.getName() + "$", "");
            Assert.assertEquals(expected, actual);
        }
    }

}