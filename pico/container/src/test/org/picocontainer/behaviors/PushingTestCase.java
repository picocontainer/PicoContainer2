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
import static org.picocontainer.Characteristics.*;

import junit.framework.TestCase;

public class PushingTestCase extends TestCase {

    private static String MESSAGE =
        "Foo was instantiated, even though it was not required to be given it was not depended on by anything looked up";

    public static class Foo {
        public Foo(StringBuilder sb) {
            sb.append(MESSAGE);
        }
    }

    public static class Bar {
    }

    public void testPushingBehavior() {
        DefaultPicoContainer pico = new DefaultPicoContainer(new Caching().wrap(new Pushing()));
        pico.addComponent(StringBuilder.class);
        pico.addComponent(Foo.class);
        pico.addComponent(Bar.class);
        pico.start();
        assertNotNull(pico.getComponent(Bar.class));
        StringBuilder sb = pico.getComponent(StringBuilder.class);
        assertEquals(MESSAGE, sb.toString());
    }

    public void testPushingBehaviorViaAdapter() {
        DefaultPicoContainer pico = new DefaultPicoContainer(new Caching().wrap(new Pushing()));
        pico.addComponent(StringBuilder.class);
        pico.addAdapter(new ConstructorInjector(Foo.class, Foo.class, null, new NullComponentMonitor(), new NullLifecycleStrategy()));
        pico.addComponent(Bar.class);
        pico.start();
        assertNotNull(pico.getComponent(Bar.class));
        StringBuilder sb = pico.getComponent(StringBuilder.class);
        assertEquals(MESSAGE, sb.toString());
    }

    public void testNonPushingBehaviorAsContrastToTheAbove() {
        DefaultPicoContainer pico = new DefaultPicoContainer(new Caching());
        pico.addComponent(StringBuilder.class);
        pico.addComponent(Foo.class);
        pico.addComponent(Bar.class);
        pico.start();
        assertNotNull(pico.getComponent(Bar.class));
        StringBuilder sb = pico.getComponent(StringBuilder.class);
        assertEquals("", sb.toString());
    }

    public void testNonPushingBehaviorAsContrastToTheAboveViaAdapter() {
        DefaultPicoContainer pico = new DefaultPicoContainer(new Caching());
        pico.addComponent(StringBuilder.class);
        pico.addAdapter(new ConstructorInjector(Foo.class, Foo.class, null, new NullComponentMonitor(), new NullLifecycleStrategy()));
        pico.addComponent(Bar.class);
        pico.start();
        assertNotNull(pico.getComponent(Bar.class));
        StringBuilder sb = pico.getComponent(StringBuilder.class);
        assertEquals("", sb.toString());
    }

    public void testPushingBehaviorByBuilder() {
        MutablePicoContainer pico = new PicoBuilder().withCaching().withPushing().build();
        pico.addComponent(StringBuilder.class);
        pico.addComponent(Foo.class);
        pico.addComponent(Bar.class);
        pico.start();
        assertNotNull(pico.getComponent(Bar.class));
        StringBuilder sb = pico.getComponent(StringBuilder.class);
        assertEquals(MESSAGE, sb.toString());
    }

    public void testPushingBehaviorByBuilderViaAdapter() {
        MutablePicoContainer pico = new PicoBuilder().withCaching().withPushing().build();
        pico.addComponent(StringBuilder.class);
        pico.addAdapter(new ConstructorInjector(Foo.class, Foo.class, null, new NullComponentMonitor(), new NullLifecycleStrategy()));
        pico.addComponent(Bar.class);
        pico.start();
        assertNotNull(pico.getComponent(Bar.class));
        StringBuilder sb = pico.getComponent(StringBuilder.class);
        assertEquals(MESSAGE, sb.toString());
    }

    public void testPushingBehaviorByBuilderADifferentWay() {
        MutablePicoContainer pico = new PicoBuilder().withBehaviors(caching(), pushing()).build();
        pico.addComponent(StringBuilder.class);
        pico.addComponent(Foo.class);
        pico.addComponent(Bar.class);
        pico.start();
        assertNotNull(pico.getComponent(Bar.class));
        StringBuilder sb = pico.getComponent(StringBuilder.class);
        assertEquals(MESSAGE, sb.toString());
    }

        public void testPushingBehaviorByBuilderADifferentWayViaAdapter() {
        MutablePicoContainer pico = new PicoBuilder().withBehaviors(caching(), pushing()).build();
        pico.addComponent(StringBuilder.class);
        pico.addAdapter(new ConstructorInjector(Foo.class, Foo.class, null, new NullComponentMonitor(), new NullLifecycleStrategy()));
        pico.addComponent(Bar.class);
        pico.start();
        assertNotNull(pico.getComponent(Bar.class));
        StringBuilder sb = pico.getComponent(StringBuilder.class);
        assertEquals(MESSAGE, sb.toString());
    }

    public void testPushingBehaviorWorksForAdaptiveBehaviorToo() {
        MutablePicoContainer pico = new PicoBuilder().withBehaviors(caching(), pushing()).build();
        pico.addComponent(StringBuilder.class);
        pico.as(PUSHING).addComponent(Foo.class);
        pico.addComponent(Bar.class);
        pico.start();
        assertNotNull(pico.getComponent(Bar.class));
        StringBuilder sb = pico.getComponent(StringBuilder.class);
        assertEquals(MESSAGE, sb.toString());
    }

    public void testPushingBehaviorWorksForAdaptiveBehaviorTooViaAdapter() {
        MutablePicoContainer pico = new PicoBuilder().withBehaviors(caching(), pushing()).build();
        pico.addComponent(StringBuilder.class);
        pico.as(PUSHING).addAdapter(new ConstructorInjector(Foo.class, Foo.class, null, new NullComponentMonitor(), new NullLifecycleStrategy()));
        pico.addComponent(Bar.class);
        pico.start();
        assertNotNull(pico.getComponent(Bar.class));
        StringBuilder sb = pico.getComponent(StringBuilder.class);
        assertEquals(MESSAGE, sb.toString());
    }

}
