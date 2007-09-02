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

import org.picocontainer.annotations.Inject;
import org.picocontainer.MutablePicoContainer;
import org.picocontainer.DefaultPicoContainer;
import org.picocontainer.lifecycle.NullLifecycleStrategy;
import org.picocontainer.monitors.NullComponentMonitor;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.annotation.ElementType;

import junit.framework.TestCase;

public class AnnotatedFieldInjectorTestCase extends TestCase {

    public static class Helicopter {
        @Inject
        private PogoStick pogo;

        public Helicopter() {
        }
    }

    public static class Helicopter2 {
        private PogoStick pogo;

        public Helicopter2() {
        }
    }

    public static class PogoStick {
    }

    public void testFieldInjection() {
        MutablePicoContainer pico = new DefaultPicoContainer();
        pico.addAdapter(new AnnotatedFieldInjector(Helicopter.class, Helicopter.class, null,
                                                    new NullComponentMonitor(), new NullLifecycleStrategy(), Inject.class, false));
        pico.addComponent(PogoStick.class, new PogoStick());
        Helicopter chopper = pico.getComponent(Helicopter.class);
        assertNotNull(chopper);
        assertNotNull(chopper.pogo);
    }

    public void testFieldInjectionWithoutAnnotationDoesNotWork() {
        MutablePicoContainer pico = new DefaultPicoContainer();
        pico.addAdapter(new AnnotatedFieldInjector(Helicopter2.class, Helicopter2.class, null,
                                                    new NullComponentMonitor(), new NullLifecycleStrategy(), Inject.class, false));
        pico.addComponent(PogoStick.class, new PogoStick());
        Helicopter2 chopper = pico.getComponent(Helicopter2.class);
        assertNotNull(chopper);
        assertNull(chopper.pogo);
    }

    public void testFieldDeosNotHappenWithoutRightInjectorDoesNotWork() {
        MutablePicoContainer pico = new DefaultPicoContainer();
        pico.addAdapter(new SetterInjector(Helicopter.class, Helicopter.class, null,
                                                    new NullComponentMonitor(), new NullLifecycleStrategy(),
                                                    "set", false));
        pico.addComponent(PogoStick.class, new PogoStick());
        Helicopter chopper = pico.getComponent(Helicopter.class);
        assertNotNull(chopper);
        assertNull(chopper.pogo);
    }

    @Retention(RetentionPolicy.RUNTIME)
    @Target(value={ ElementType.METHOD, ElementType.FIELD})
    public @interface AlternativeInject {
    }

    public static class Helicopter3 {
        @AlternativeInject
        private PogoStick pogo;

        public Helicopter3() {
        }
    }

    public void testFieldInjectionWithAlternativeInjectionAnnotation() {
        MutablePicoContainer pico = new DefaultPicoContainer();
        pico.addAdapter(new AnnotatedFieldInjector(Helicopter3.class, Helicopter3.class, null,
                                                    new NullComponentMonitor(), new NullLifecycleStrategy(), AlternativeInject.class, false));
        pico.addComponent(PogoStick.class, new PogoStick());
        Helicopter3 chopper = pico.getComponent(Helicopter3.class);
        assertNotNull(chopper);
        assertNotNull(chopper.pogo);
    }

}