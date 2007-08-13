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

import org.picocontainer.tck.AbstractComponentFactoryTestCase;
import org.picocontainer.injectors.ConstructorInjection;
import org.picocontainer.injectors.ConstructorInjector;
import org.picocontainer.behaviors.OptInCaching;
import org.picocontainer.DefaultPicoContainer;
import org.picocontainer.ComponentFactory;
import org.picocontainer.Characteristics;
import org.picocontainer.ComponentAdapter;
import org.picocontainer.monitors.NullComponentMonitor;
import org.picocontainer.lifecycle.NullLifecycleStrategy;
import org.picocontainer.adapters.InstanceAdapter;

/**
 * @author <a href="Rafal.Krzewski">rafal@caltha.pl</a>
 */
public class OptInCachingTestCase extends AbstractComponentFactoryTestCase {

    protected ComponentFactory createComponentFactory() {
        return new OptInCaching().wrap(new ConstructorInjection());
    }

    public void testAddComponentDoesNotUseCachingBehaviorByDefault() {
        DefaultPicoContainer pico =
            new DefaultPicoContainer(new OptInCaching().wrap(new ConstructorInjection()));
        pico.addComponent("foo", String.class);
        ComponentAdapter foo = pico.getComponentAdapter("foo");
        assertEquals(ConstructorInjector.class, foo.getClass());
    }

    public void testAddComponentUsesImplementationHidingBehaviorWithRedundantCacheProperty() {
        DefaultPicoContainer pico =
            new DefaultPicoContainer(new OptInCaching().wrap(new ConstructorInjection()));
        pico.change(Characteristics.CACHE).addComponent("foo", String.class);
        ComponentAdapter foo = pico.getComponentAdapter("foo");
        assertEquals(Cached.class, foo.getClass());
        assertEquals(ConstructorInjector.class, ((AbstractBehavior) foo).getDelegate().getClass());
    }

    public void testAddComponentNoesNotUseImplementationHidingBehaviorWhenNoCachePropertyIsSpecified() {
        DefaultPicoContainer pico =
            new DefaultPicoContainer(new OptInCaching().wrap(new ConstructorInjection()));
        pico.change(Characteristics.NO_CACHE).addComponent("foo", String.class);
        ComponentAdapter foo = pico.getComponentAdapter("foo");
        assertEquals(ConstructorInjector.class, foo.getClass());
    }

    public void testAddAdapterUsesDoesNotUseCachingBehaviorByDefault() {
        DefaultPicoContainer pico =
            new DefaultPicoContainer(new OptInCaching().wrap(new ConstructorInjection()));
        pico.addAdapter(new InstanceAdapter("foo", "bar", new NullLifecycleStrategy(), new NullComponentMonitor()));
        ComponentAdapter foo = pico.getComponentAdapter("foo");
        assertEquals(InstanceAdapter.class, foo.getClass());
    }

    public void testAddAdapterUsesCachingBehaviorWithHideImplProperty() {
        DefaultPicoContainer pico =
            new DefaultPicoContainer(new OptInCaching().wrap(new ConstructorInjection()));
        pico.change(Characteristics.CACHE).addAdapter(new InstanceAdapter("foo", "bar", new NullLifecycleStrategy(), new NullComponentMonitor()));
        ComponentAdapter foo = pico.getComponentAdapter("foo");
        assertEquals(Cached.class, foo.getClass());
        assertEquals(InstanceAdapter.class, ((AbstractBehavior) foo).getDelegate().getClass());
    }

    public void testAddAdapterNoesNotUseImplementationHidingBehaviorWhenNoCachePropertyIsSpecified() {
        DefaultPicoContainer pico =
            new DefaultPicoContainer(new OptInCaching().wrap(new ConstructorInjection()));
        pico.change(Characteristics.NO_CACHE).addAdapter(new InstanceAdapter("foo", "bar", new NullLifecycleStrategy(), new NullComponentMonitor()));
        ComponentAdapter foo = pico.getComponentAdapter("foo");
        assertEquals(InstanceAdapter.class, foo.getClass());
    }



}