/*****************************************************************************
 * Copyright (C) PicoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the LICENSE.txt file.                                                     *
 *                                                                           *
 * Idea by Rachel Davies, Original code by Aslak Hellesoy and Paul Hammant   *
 *****************************************************************************/
package org.picocontainer.injectors;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.Properties;

import org.junit.Before;
import org.junit.Test;
import org.picocontainer.ComponentFactory;
import org.picocontainer.DefaultPicoContainer;
import org.picocontainer.Characteristics;
import static org.picocontainer.Characteristics.USE_NAMES;
import org.picocontainer.monitors.NullComponentMonitor;
import org.picocontainer.tck.AbstractComponentFactoryTest;
import org.picocontainer.tck.AbstractComponentAdapterTest.RecordingLifecycleStrategy;
import org.picocontainer.testmodel.NullLifecycle;
import org.picocontainer.testmodel.RecordingLifecycle;
import org.picocontainer.testmodel.RecordingLifecycle.One;

/**
 * @author Mauro Talevi
 */
public class ConstructorInjectionTestCase extends AbstractComponentFactoryTest {

	@Before
    public void setUp() throws Exception {
        picoContainer = new DefaultPicoContainer(createComponentFactory());
    }

    protected ComponentFactory createComponentFactory() {
        return new ConstructorInjection();
    }

    @Test public void testCustomLifecycleCanBeInjected() throws NoSuchMethodException {
        RecordingLifecycleStrategy strategy = new RecordingLifecycleStrategy(new StringBuffer());
        ConstructorInjection componentFactory =
            new ConstructorInjection();
        ConstructorInjector cica =  (ConstructorInjector)
        componentFactory.createComponentAdapter(new NullComponentMonitor(), strategy, new Properties(), NullLifecycle.class, NullLifecycle.class);
        One one = new RecordingLifecycle.One(new StringBuffer());
        cica.start(one);
        cica.stop(one);        
        cica.dispose(one);
        assertEquals("<start<stop<dispose", strategy.recording());
    }

    public static class ClassA {
        private int x;
        public ClassA(int x) {
            this.x = x;
        }
    }
    @Test public void testAutoConversionOfIntegerParam() {
        picoContainer.as(USE_NAMES).addComponent(ClassA.class);
        picoContainer.addComponent("x", "12");
        assertNotNull(picoContainer.getComponent(ClassA.class));
        assertEquals(12,picoContainer.getComponent(ClassA.class).x);
    }

    public static class ClassB {
        private float x;
        public ClassB(float x) {
            this.x = x;
        }
    }
    @Test public void testAutoConversionOfFloatParam() {
        picoContainer.as(USE_NAMES).addComponent(ClassB.class);
        picoContainer.addComponent("x", "1.2");
        assertNotNull(picoContainer.getComponent(ClassB.class));
        assertEquals(1.2,picoContainer.getComponent(ClassB.class).x, 0.0001);
    }


}