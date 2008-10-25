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
import static org.junit.Assert.fail;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.picocontainer.ComponentFactory;
import org.picocontainer.DefaultPicoContainer;
import org.picocontainer.ComponentMonitor;
import org.picocontainer.ComponentAdapter;
import org.picocontainer.PicoContainer;
import org.picocontainer.PicoCompositionException;
import org.picocontainer.monitors.NullComponentMonitor;
import org.picocontainer.behaviors.Caching;
import org.picocontainer.containers.EmptyPicoContainer;
import org.picocontainer.containers.TransientPicoContainer;
import org.picocontainer.tck.AbstractComponentFactoryTest;
import static org.picocontainer.tck.MockFactory.mockeryWithCountingNamingScheme;
import org.jmock.integration.junit4.JMock;
import org.jmock.Mockery;
import org.jmock.Expectations;

import java.lang.reflect.Method;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.annotation.ElementType;

@RunWith(JMock.class)
public class ReinjectionTestCase extends AbstractComponentFactoryTest {

    private Mockery mockery = mockeryWithCountingNamingScheme();

    @Retention(RetentionPolicy.RUNTIME)
    @Target(value={ElementType.METHOD, ElementType.FIELD})
    public @interface Hurrah {
    }

    public static class NeedsShoe {
        private Shoe bar;
        private String string;

        public NeedsShoe(Shoe bar) {
            this.bar = bar;
        }

        @Hurrah
        public int doIt(String s) {
            this.string = s;
            return Integer.parseInt(s) / 2;
        }
    }

    public static class Shoe {
    }

    private static Method DOIT = NeedsShoe.class.getMethods()[0];

    @Test public void testCachedComponentCanBeReflectionMethodReinjectedByATransientChildContainer() {
        cachedComponentCanBeReinjectedByATransientChildContainer(new MethodInjection(DOIT));
    }

    @Test public void testCachedComponentCanBeMethodNameReinjectedByATransientChildContainer() {
        cachedComponentCanBeReinjectedByATransientChildContainer(new MethodInjection("doIt"));
    }
    
    @Test public void testCachedComponentCanBeAnnotatedMethodReinjectedByATransientChildContainer() {
        cachedComponentCanBeReinjectedByATransientChildContainer(new AnnotatedMethodInjection(Hurrah.class, false));
    }

    private void cachedComponentCanBeReinjectedByATransientChildContainer(AbstractInjectionFactory methodInjection) {
        DefaultPicoContainer parent = new DefaultPicoContainer(new Caching().wrap(new ConstructorInjection()));
        parent.addComponent(NeedsShoe.class);
        parent.addComponent(Shoe.class);
        parent.addComponent("12");

        NeedsShoe needsShoe = parent.getComponent(NeedsShoe.class);
        assertNotNull(needsShoe.bar);
        assertTrue(needsShoe.string == null);

        TransientPicoContainer tpc = new TransientPicoContainer(new Reinjection(methodInjection, parent), parent);
        tpc.addComponent(NeedsShoe.class);

        NeedsShoe needsShoe2 = tpc.getComponent(NeedsShoe.class);
        assertSame(needsShoe, needsShoe2);
        assertNotNull(needsShoe2.bar);
        assertNotNull(needsShoe2.string);

        NeedsShoe needsShoe3 = parent.getComponent(NeedsShoe.class);
        assertSame(needsShoe, needsShoe3);
        assertNotNull(needsShoe3.bar);
        assertNotNull(needsShoe3.string);
    }


    @Test public void testCachedComponentCanBeReinjectedByATransientReflectionMethodReinjector() {
        cachedComponentCanBeReinjectedByATransientReinjector(new MethodInjection(DOIT));
    }
    @Test public void testCachedComponentCanBeReinjectedByATransientMethodNameReinjector() {
        cachedComponentCanBeReinjectedByATransientReinjector(new MethodInjection("doIt"));
    }
    @Test public void testCachedComponentCanBeReinjectedByATransientAnnotatedMethodReinjector() {
        cachedComponentCanBeReinjectedByATransientReinjector(new AnnotatedMethodInjection(Hurrah.class, false));
    }

    private void cachedComponentCanBeReinjectedByATransientReinjector(AbstractInjectionFactory methodInjection) {
        final DefaultPicoContainer parent = new DefaultPicoContainer(new Caching().wrap(new ConstructorInjection()));
        parent.setName("parent");
        parent.addComponent(NeedsShoe.class);
        parent.addComponent(Shoe.class);
        parent.addComponent("12");

        final NeedsShoe foo = parent.getComponent(NeedsShoe.class);
        assertNotNull(foo.bar);
        assertTrue(foo.string == null);

        final ComponentMonitor cm = mockery.mock(ComponentMonitor.class);
        Reinjector reinjector = new Reinjector(parent, cm);
        mockery.checking(new Expectations() {{
            one(cm).invoking(with(any(PicoContainer.class)), with(any(ComponentAdapter.class)),
                    with(any(Method.class)), with(any(Object.class)));
        }});

        Object o = reinjector.reinject(NeedsShoe.class, methodInjection);
        int result = (Integer) o;
        assertEquals(6, result);

        NeedsShoe foo3 = parent.getComponent(NeedsShoe.class);
        assertSame(foo, foo3);
        assertNotNull(foo3.bar);
        assertNotNull(foo3.string);
        assertEquals("12", foo3.string);
    }

    @Test public void testOverloadedReinjectMethodsAreIdentical() {
        final DefaultPicoContainer parent = new DefaultPicoContainer(new Caching().wrap(new ConstructorInjection()));
        parent.addComponent(NeedsShoe.class);
        parent.addComponent(Shoe.class);
        parent.addComponent("12");

        final ComponentMonitor cm = new NullComponentMonitor();
        Reinjector reinjector = new Reinjector(parent, cm);

        int result = (Integer) reinjector.reinject(NeedsShoe.class, DOIT);
        assertEquals(6, (int) (Integer) reinjector.reinject(NeedsShoe.class, DOIT));
        assertEquals(6, (int) (Integer) reinjector.reinject(NeedsShoe.class, new MethodInjection(DOIT)));

    }

    protected ComponentFactory createComponentFactory() {
        return new Reinjection(new MethodInjection(DOIT), new EmptyPicoContainer());
    }

    @Test
    public void testRegisterComponent() throws PicoCompositionException {
        try {
            super.testRegisterComponent();
            fail();
        } catch (PicoCompositionException e) {
            assertTrue(e.getMessage().contains("] not on impl "));
        }
    }

    @Test
    public void testUnregisterComponent() throws PicoCompositionException {
        try {
            super.testUnregisterComponent();
            fail();
        } catch (PicoCompositionException e) {
            assertTrue(e.getMessage().contains("] not on impl "));
        }
    }

    @Test
    public void testEquals() throws PicoCompositionException {
        try {
            super.testEquals();
            fail();
        } catch (PicoCompositionException e) {
            assertTrue(e.getMessage().contains("] not on impl "));
        }
    }
}