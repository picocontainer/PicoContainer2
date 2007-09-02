/*****************************************************************************
 * Copyright (C) NanoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the LICENSE.txt file.                                                     *
 *                                                                           *
 * Original code by Joerg Schaibe                                            *
 *****************************************************************************/

package org.picocontainer.gems.behaviors;

import com.thoughtworks.proxy.factory.CglibProxyFactory;

import junit.framework.TestCase;

import org.picocontainer.ComponentAdapter;
import org.picocontainer.MutablePicoContainer;
import org.picocontainer.PicoCompositionException;
import org.picocontainer.DefaultPicoContainer;
import org.picocontainer.behaviors.Cached;
import org.picocontainer.monitors.NullComponentMonitor;
import org.picocontainer.lifecycle.NullLifecycleStrategy;
import org.picocontainer.injectors.ConstructorInjector;
import org.picocontainer.adapters.InstanceAdapter;
import org.picocontainer.tck.AbstractComponentAdapterTestCase;
import org.picocontainer.testmodel.CompatibleTouchable;
import org.picocontainer.testmodel.SimpleTouchable;
import org.picocontainer.testmodel.Touchable;

import java.lang.reflect.Method;
import java.lang.reflect.Proxy;


/**
 * @author J&ouml;rg Schaible
 */
public class AssimilatedTestCase extends AbstractComponentAdapterTestCase {

    /**
     * Test if an instance can be assimilated.
     */
    public void testInstanceIsBorged() {
        final MutablePicoContainer mpc = new DefaultPicoContainer();
        final ComponentAdapter<CompatibleTouchable> componentAdapter = new Cached<CompatibleTouchable>(new ConstructorInjector(
                CompatibleTouchable.class, CompatibleTouchable.class, null, new NullComponentMonitor(), new NullLifecycleStrategy(), false));
        mpc.addAdapter(new Assimilated(Touchable.class, componentAdapter));
        final CompatibleTouchable compatibleTouchable = componentAdapter.getComponentInstance(mpc);
        final Touchable touchable = mpc.getComponent(Touchable.class);
        assertFalse(compatibleTouchable.wasTouched());
        touchable.touch();
        assertTrue(compatibleTouchable.wasTouched());
        assertTrue(Proxy.isProxyClass(touchable.getClass()));
    }

    /**
     * Test if the component key is preserved if it is not a class type.
     */
    public void testComponentKeyIsPreserved() {
        final MutablePicoContainer mpc = new DefaultPicoContainer();
        final ComponentAdapter<CompatibleTouchable> componentAdapter = new Cached<CompatibleTouchable>(new ConstructorInjector(
                "Touchy", CompatibleTouchable.class, null, new NullComponentMonitor(), new NullLifecycleStrategy(), false));
        mpc.addAdapter(new Assimilated(Touchable.class, componentAdapter));
        final CompatibleTouchable compatibleTouchable = componentAdapter.getComponentInstance(mpc);
        final Touchable touchable = (Touchable)mpc.getComponent("Touchy");
        assertFalse(compatibleTouchable.wasTouched());
        touchable.touch();
        assertTrue(compatibleTouchable.wasTouched());
        assertTrue(Proxy.isProxyClass(touchable.getClass()));
    }

    /**
     * Test if proxy generation is omitted, if types are compatible.
     */
    public void testAvoidUnnecessaryProxy() {
        final MutablePicoContainer mpc = new DefaultPicoContainer();
        mpc.addAdapter(new Assimilated(TestCase.class, new InstanceAdapter<AssimilatedTestCase>(TestCase.class, this, new NullLifecycleStrategy(),
                                                                        new NullComponentMonitor())));
        final TestCase self = mpc.getComponent(TestCase.class);
        assertFalse(Proxy.isProxyClass(self.getClass()));
        assertSame(this, self);
    }

    /**
     * Test if proxy generation is omitted, if types are compatible and that the component key is not changed.
     */
    public void testAvoidedProxyDoesNotChangeComponentKey() {
        final MutablePicoContainer mpc = new DefaultPicoContainer();
        mpc.addAdapter(new Assimilated(TestCase.class, new InstanceAdapter<AssimilatedTestCase>(getClass(), this, new NullLifecycleStrategy(),
                                                                        new NullComponentMonitor())));
        final TestCase self = mpc.getComponent(getClass());
        assertNotNull(self);
        assertSame(this, self);
    }

    /**
     * Test fail-fast for components without interface.
     */
    public void testComponentMustImplementInterface() {
        try {
            new Assimilated(SimpleTouchable.class, new InstanceAdapter<AssimilatedTestCase>(TestCase.class, this, new NullLifecycleStrategy(),
                                                                        new NullComponentMonitor()));
            fail("PicoCompositionException expected");
        } catch (final PicoCompositionException e) {
            assertTrue(e.getMessage().endsWith(SimpleTouchable.class.getName()));
        }
    }

    /**
     * Test fail-fast for components without matching methods.
     * @throws NoSuchMethodException 
     */
    public void testComponentMustHaveMathichMethods() throws NoSuchMethodException {
        final Method touch = Touchable.class.getMethod("touch", (Class[])null);
        try {
            new Assimilated(Touchable.class, new InstanceAdapter<AssimilatedTestCase>(TestCase.class, this, new NullLifecycleStrategy(),
                                                                        new NullComponentMonitor()));
            fail("PicoCompositionException expected");
        } catch (final PicoCompositionException e) {
            assertTrue(e.getMessage().endsWith(touch.toString()));
        }
    }

    // -------- TCK -----------

    protected Class getComponentAdapterType() {
        return Assimilated.class;
    }

    protected int getComponentAdapterNature() {
        return super.getComponentAdapterNature() & ~(RESOLVING | VERIFYING | INSTANTIATING);
    }

    private ComponentAdapter createComponentAdapterWithTouchable() {
        return new Assimilated(Touchable.class, new ConstructorInjector(
                CompatibleTouchable.class, CompatibleTouchable.class, null, new NullComponentMonitor(), new NullLifecycleStrategy(), false));
    }

    protected ComponentAdapter prepDEF_verifyWithoutDependencyWorks(MutablePicoContainer picoContainer) {
        return createComponentAdapterWithTouchable();
    }

    protected ComponentAdapter prepDEF_verifyDoesNotInstantiate(MutablePicoContainer picoContainer) {
        return createComponentAdapterWithTouchable();
    }

    protected ComponentAdapter prepDEF_visitable() {
        return createComponentAdapterWithTouchable();
    }

    protected ComponentAdapter prepSER_isSerializable(MutablePicoContainer picoContainer) {
        return new Assimilated(Touchable.class, new InstanceAdapter<CompatibleTouchable>(
                CompatibleTouchable.class, new CompatibleTouchable(), new NullLifecycleStrategy(),
                                                                        new NullComponentMonitor()), new CglibProxyFactory());
    }

    protected ComponentAdapter prepSER_isXStreamSerializable(MutablePicoContainer picoContainer) {
        return createComponentAdapterWithTouchable();
    }
}
