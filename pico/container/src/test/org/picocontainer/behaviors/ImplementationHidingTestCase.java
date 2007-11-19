/*****************************************************************************
 * Copyright (c) PicoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the LICENSE.txt file.                                                     *
 *                                                                           *
 *****************************************************************************/

package org.picocontainer.behaviors;

import org.picocontainer.Characteristics;
import org.picocontainer.ComponentAdapter;
import org.picocontainer.ComponentFactory;
import org.picocontainer.DefaultPicoContainer;
import org.picocontainer.lifecycle.NullLifecycleStrategy;
import org.picocontainer.monitors.NullComponentMonitor;
import org.picocontainer.adapters.InstanceAdapter;
import org.picocontainer.injectors.AdaptingInjection;
import org.picocontainer.injectors.ConstructorInjection;
import org.picocontainer.injectors.ConstructorInjector;
import org.picocontainer.tck.AbstractComponentFactoryTestCase;

public class ImplementationHidingTestCase extends AbstractComponentFactoryTestCase {



    public void testAddComponentUsesImplementationHidingBehavior() {
        DefaultPicoContainer pico =
            new DefaultPicoContainer(new ImplementationHiding().wrap(new ConstructorInjection()));
        pico.addComponent("foo", String.class);
        ComponentAdapter foo = pico.getComponentAdapter("foo");
        assertEquals(HiddenImplementation.class, foo.getClass());
        assertEquals(ConstructorInjector.class, ((AbstractBehavior) foo).getDelegate().getClass());
    }

    public void testAddComponentUsesImplementationHidingBehaviorWithRedundantHideImplProperty() {
        DefaultPicoContainer pico =
            new DefaultPicoContainer(new ImplementationHiding().wrap(new ConstructorInjection()));
        pico.change(Characteristics.HIDE_IMPL).addComponent("foo", String.class);
        ComponentAdapter foo = pico.getComponentAdapter("foo");
        assertEquals(HiddenImplementation.class, foo.getClass());
        assertEquals(ConstructorInjector.class, ((AbstractBehavior) foo).getDelegate().getClass());
    }

    public void testAddComponentNoesNotUseImplementationHidingBehaviorWhenNoCachePropertyIsSpecified() {
        DefaultPicoContainer pico =
            new DefaultPicoContainer(new ImplementationHiding().wrap(new ConstructorInjection()));
        pico.change(Characteristics.NO_HIDE_IMPL).addComponent("foo", String.class);
        ComponentAdapter foo = pico.getComponentAdapter("foo");
        assertEquals(ConstructorInjector.class, foo.getClass());
    }

    public void testAddAdapterUsesImplementationHidingBehavior() {
        DefaultPicoContainer pico =
            new DefaultPicoContainer(new ImplementationHiding().wrap(new ConstructorInjection()));
        pico.addAdapter(new InstanceAdapter("foo", "bar", new NullLifecycleStrategy(), new NullComponentMonitor()));
        ComponentAdapter foo = pico.getComponentAdapter("foo");
        assertEquals(HiddenImplementation.class, foo.getClass());
        assertEquals(InstanceAdapter.class, ((AbstractBehavior) foo).getDelegate().getClass());
    }

    public void testAddAdapterUsesImplementationHidingBehaviorWithRedundantHideImplProperty() {
        DefaultPicoContainer pico =
            new DefaultPicoContainer(new ImplementationHiding().wrap(new ConstructorInjection()));
        pico.change(Characteristics.HIDE_IMPL).addAdapter(new InstanceAdapter("foo", "bar", new NullLifecycleStrategy(), new NullComponentMonitor()));
        ComponentAdapter foo = pico.getComponentAdapter("foo");
        assertEquals(HiddenImplementation.class, foo.getClass());
        assertEquals(InstanceAdapter.class, ((AbstractBehavior) foo).getDelegate().getClass());
    }

    public void testAddAdapterNoesNotUseImplementationHidingBehaviorWhenNoCachePropertyIsSpecified() {
        DefaultPicoContainer pico =
            new DefaultPicoContainer(new ImplementationHiding().wrap(new ConstructorInjection()));
        pico.change(Characteristics.NO_HIDE_IMPL).addAdapter(new InstanceAdapter("foo", "bar", new NullLifecycleStrategy(), new NullComponentMonitor()));
        ComponentAdapter foo = pico.getComponentAdapter("foo");
        assertEquals(InstanceAdapter.class, foo.getClass());
    }


    private final ComponentFactory implementationHidingComponentFactory =
        new ImplementationHiding().wrap(new AdaptingInjection());

    protected ComponentFactory createComponentFactory() {
        return implementationHidingComponentFactory;
    }


    public static interface NeedsStringBuilder {
        void foo();
    }
    public static class NeedsStringBuilderImpl implements NeedsStringBuilder {
        StringBuilder sb;

        public NeedsStringBuilderImpl(StringBuilder sb) {
            this.sb = sb;
            sb.append("<init>");
        }
        public void foo() {
            sb.append("foo()");
        }
    }
    public static class NeedsNeedsStringBuilder {

        NeedsStringBuilder nsb;

        public NeedsNeedsStringBuilder(NeedsStringBuilder nsb) {
            this.nsb = nsb;
        }
        public void foo() {
            nsb.foo();
        }
    }

    public void testLazyInstantiationSideEffectWhenForceOfDelayedInstantiationOfDependantClass() {
        DefaultPicoContainer pico =
            new DefaultPicoContainer(new ImplementationHiding().wrap(new Caching().wrap(new ConstructorInjection())));
        pico.addComponent(StringBuilder.class);
        pico.addComponent(NeedsStringBuilder.class, NeedsStringBuilderImpl.class);
        pico.addComponent(NeedsNeedsStringBuilder.class);
        NeedsNeedsStringBuilder nnsb = pico.getComponent(NeedsNeedsStringBuilder.class);
        assertNotNull(nnsb);
        StringBuilder sb = pico.getComponent(StringBuilder.class);
        assertEquals("", sb.toString()); // not instantiated yet
        nnsb.foo();
        assertEquals("<init>foo()", sb.toString()); // instantiated
    }


}