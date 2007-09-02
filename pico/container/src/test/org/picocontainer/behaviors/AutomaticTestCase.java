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

public class AutomaticTestCase extends TestCase {

    private static String MESSAGE =
        "Foo was instantiated, even though it was not required to be given it was not depended on by anything looked up";

    public static class Foo {
        public Foo(StringBuilder sb) {
            sb.append(MESSAGE);
        }
    }

    public static class Bar {
    }

    public void testAutomaticBehavior() {
        DefaultPicoContainer pico = new DefaultPicoContainer(new Caching().wrap(new Automatic()));
        pico.addComponent(StringBuilder.class);
        pico.addComponent(Foo.class);
        pico.addComponent(Bar.class);
        pico.start();
        assertNotNull(pico.getComponent(Bar.class));
        StringBuilder sb = pico.getComponent(StringBuilder.class);
        assertEquals(MESSAGE, sb.toString());
        assertEquals("Cached:Automated:ConstructorInjector-class org.picocontainer.behaviors.AutomaticTestCase$Foo", pico.getComponentAdapter(Foo.class).toString());
    }

    public void testAutomaticBehaviorViaAdapter() {
        DefaultPicoContainer pico = new DefaultPicoContainer(new Caching().wrap(new Automatic()));
        pico.addComponent(StringBuilder.class);
        pico.addAdapter(new ConstructorInjector(Foo.class, Foo.class, null, new NullComponentMonitor(), new NullLifecycleStrategy(), false));
        pico.addComponent(Bar.class);
        pico.start();
        assertNotNull(pico.getComponent(Bar.class));
        StringBuilder sb = pico.getComponent(StringBuilder.class);
        assertEquals(MESSAGE, sb.toString());
        assertEquals("Cached:Automated:ConstructorInjector-class org.picocontainer.behaviors.AutomaticTestCase$Foo", pico.getComponentAdapter(Foo.class).toString());
    }

    public void testNonAutomaticBehaviorAsContrastToTheAbove() {
        DefaultPicoContainer pico = new DefaultPicoContainer(new Caching());
        pico.addComponent(StringBuilder.class);
        pico.addComponent(Foo.class);
        pico.addComponent(Bar.class);
        pico.start();
        assertNotNull(pico.getComponent(Bar.class));
        StringBuilder sb = pico.getComponent(StringBuilder.class);
        assertEquals("", sb.toString());
    }

    public void testNonAutomaticBehaviorAsContrastToTheAboveViaAdapter() {
        DefaultPicoContainer pico = new DefaultPicoContainer(new Caching());
        pico.addComponent(StringBuilder.class);
        pico.addAdapter(new ConstructorInjector(Foo.class, Foo.class, null, new NullComponentMonitor(), new NullLifecycleStrategy(), false));
        pico.addComponent(Bar.class);
        pico.start();
        assertNotNull(pico.getComponent(Bar.class));
        StringBuilder sb = pico.getComponent(StringBuilder.class);
        assertEquals("", sb.toString());
    }

    public void testAutomaticBehaviorByBuilder() {
        MutablePicoContainer pico = new PicoBuilder().withCaching().withAutomatic().build();
        pico.addComponent(StringBuilder.class);
        pico.addComponent(Foo.class);
        pico.addComponent(Bar.class);
        pico.start();
        assertNotNull(pico.getComponent(Bar.class));
        StringBuilder sb = pico.getComponent(StringBuilder.class);
        assertEquals(MESSAGE, sb.toString());
        assertEquals("Cached:Automated:ConstructorInjector-class org.picocontainer.behaviors.AutomaticTestCase$Foo", pico.getComponentAdapter(Foo.class).toString());
    }

    public void testAutomaticBehaviorByBuilderViaAdapter() {
        MutablePicoContainer pico = new PicoBuilder().withCaching().withAutomatic().build();
        pico.addComponent(StringBuilder.class);
        pico.addAdapter(new ConstructorInjector(Foo.class, Foo.class, null, new NullComponentMonitor(), new NullLifecycleStrategy(), false));
        pico.addComponent(Bar.class);
        pico.start();
        assertNotNull(pico.getComponent(Bar.class));
        StringBuilder sb = pico.getComponent(StringBuilder.class);
        assertEquals(MESSAGE, sb.toString());
        assertEquals("Cached:Automated:ConstructorInjector-class org.picocontainer.behaviors.AutomaticTestCase$Foo", pico.getComponentAdapter(Foo.class).toString());
    }

    public void testAutomaticBehaviorByBuilderADifferentWay() {
        MutablePicoContainer pico = new PicoBuilder().withBehaviors(caching(), automatic()).build();
        pico.addComponent(StringBuilder.class);
        pico.addComponent(Foo.class);
        pico.addComponent(Bar.class);
        pico.start();
        assertNotNull(pico.getComponent(Bar.class));
        StringBuilder sb = pico.getComponent(StringBuilder.class);
        assertEquals(MESSAGE, sb.toString());
        assertEquals("Cached:Automated:ConstructorInjector-class org.picocontainer.behaviors.AutomaticTestCase$Foo", pico.getComponentAdapter(Foo.class).toString());
    }

        public void testAutomaticBehaviorByBuilderADifferentWayViaAdapter() {
        MutablePicoContainer pico = new PicoBuilder().withBehaviors(caching(), automatic()).build();
        pico.addComponent(StringBuilder.class);
        pico.addAdapter(new ConstructorInjector(Foo.class, Foo.class, null, new NullComponentMonitor(), new NullLifecycleStrategy(), false));
        pico.addComponent(Bar.class);
        pico.start();
        assertNotNull(pico.getComponent(Bar.class));
        StringBuilder sb = pico.getComponent(StringBuilder.class);
        assertEquals(MESSAGE, sb.toString());
            assertEquals("Cached:Automated:ConstructorInjector-class org.picocontainer.behaviors.AutomaticTestCase$Foo", pico.getComponentAdapter(Foo.class).toString());
    }

    public void testAutomaticBehaviorWorksForAdaptiveBehaviorToo() {
        MutablePicoContainer pico = new PicoBuilder().withBehaviors(caching(), automatic()).build();
        pico.addComponent(StringBuilder.class);
        pico.as(AUTOMATIC).addComponent(Foo.class);
        pico.addComponent(Bar.class);
        pico.start();
        assertNotNull(pico.getComponent(Bar.class));
        StringBuilder sb = pico.getComponent(StringBuilder.class);
        assertEquals(MESSAGE, sb.toString());
        assertEquals("Cached:Automated:ConstructorInjector-class org.picocontainer.behaviors.AutomaticTestCase$Foo", pico.getComponentAdapter(Foo.class).toString());
    }

    public void testAutomaticBehaviorWorksForAdaptiveBehaviorTooViaAdapter() {
        MutablePicoContainer pico = new PicoBuilder().withBehaviors(caching(), automatic()).build();
        pico.addComponent(StringBuilder.class);
        pico.as(AUTOMATIC).addAdapter(new ConstructorInjector(Foo.class, Foo.class, null, new NullComponentMonitor(), new NullLifecycleStrategy(), false));
        pico.addComponent(Bar.class);
        pico.start();
        assertNotNull(pico.getComponent(Bar.class));
        StringBuilder sb = pico.getComponent(StringBuilder.class);
        assertEquals(MESSAGE, sb.toString());
        assertEquals("Cached:Automated:ConstructorInjector-class org.picocontainer.behaviors.AutomaticTestCase$Foo", pico.getComponentAdapter(Foo.class).toString());
    }

}
