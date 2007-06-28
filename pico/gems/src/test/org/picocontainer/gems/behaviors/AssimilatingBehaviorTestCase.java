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
import org.picocontainer.gems.behaviors.AssimilatingBehavior;
import org.picocontainer.behaviors.CachingBehavior;
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
public class AssimilatingBehaviorTestCase extends AbstractComponentAdapterTestCase {

    /**
     * Test if an instance can be assimilated.
     */
    public void testInstanceIsBorged() {
        final MutablePicoContainer mpc = new DefaultPicoContainer();
        final ComponentAdapter componentAdapter = new CachingBehavior(new ConstructorInjector(
                CompatibleTouchable.class, CompatibleTouchable.class));
        mpc.addAdapter(new AssimilatingBehavior(Touchable.class, componentAdapter));
        final CompatibleTouchable compatibleTouchable = (CompatibleTouchable)componentAdapter.getComponentInstance(mpc);
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
        final ComponentAdapter componentAdapter = new CachingBehavior(new ConstructorInjector(
                "Touchy", CompatibleTouchable.class));
        mpc.addAdapter(new AssimilatingBehavior(Touchable.class, componentAdapter));
        final CompatibleTouchable compatibleTouchable = (CompatibleTouchable)componentAdapter.getComponentInstance(mpc);
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
        mpc.addAdapter(new AssimilatingBehavior(TestCase.class, new InstanceAdapter(TestCase.class, this, NullLifecycleStrategy.getInstance(),
                                                                        NullComponentMonitor.getInstance())));
        final TestCase self = mpc.getComponent(TestCase.class);
        assertFalse(Proxy.isProxyClass(self.getClass()));
        assertSame(this, self);
    }

    /**
     * Test if proxy generation is omitted, if types are compatible and that the component key is not changed.
     */
    public void testAvoidedProxyDoesNotChangeComponentKey() {
        final MutablePicoContainer mpc = new DefaultPicoContainer();
        mpc.addAdapter(new AssimilatingBehavior(TestCase.class, new InstanceAdapter(getClass(), this, NullLifecycleStrategy.getInstance(),
                                                                        NullComponentMonitor.getInstance())));
        final TestCase self = mpc.getComponent(getClass());
        assertNotNull(self);
        assertSame(this, self);
    }

    /**
     * Test fail-fast for components without interface.
     */
    public void testComponentMustImplementInterface() {
        try {
            new AssimilatingBehavior(SimpleTouchable.class, new InstanceAdapter(TestCase.class, this, NullLifecycleStrategy.getInstance(),
                                                                        NullComponentMonitor.getInstance()));
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
            new AssimilatingBehavior(Touchable.class, new InstanceAdapter(TestCase.class, this, NullLifecycleStrategy.getInstance(),
                                                                        NullComponentMonitor.getInstance()));
            fail("PicoCompositionException expected");
        } catch (final PicoCompositionException e) {
            assertTrue(e.getMessage().endsWith(touch.toString()));
        }
    }

    // -------- TCK -----------

    protected Class getComponentAdapterType() {
        return AssimilatingBehavior.class;
    }

    protected int getComponentAdapterNature() {
        return super.getComponentAdapterNature() & ~(RESOLVING | VERIFYING | INSTANTIATING);
    }

    private ComponentAdapter createComponentAdapterWithTouchable() {
        return new AssimilatingBehavior(Touchable.class, new ConstructorInjector(
                CompatibleTouchable.class, CompatibleTouchable.class));
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
        return new AssimilatingBehavior(Touchable.class, new InstanceAdapter(
                CompatibleTouchable.class, new CompatibleTouchable(), NullLifecycleStrategy.getInstance(),
                                                                        NullComponentMonitor.getInstance()), new CglibProxyFactory());
    }

    protected ComponentAdapter prepSER_isXStreamSerializable(MutablePicoContainer picoContainer) {
        return createComponentAdapterWithTouchable();
    }
}
