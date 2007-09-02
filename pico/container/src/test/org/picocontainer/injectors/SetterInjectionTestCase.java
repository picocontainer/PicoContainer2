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

import org.picocontainer.PicoCompositionException;
import org.picocontainer.ComponentFactory;
import org.picocontainer.injectors.SetterInjection;
import org.picocontainer.monitors.NullComponentMonitor;
import org.picocontainer.DefaultPicoContainer;
import org.picocontainer.injectors.SetterInjector;
import org.picocontainer.tck.AbstractComponentFactoryTestCase;
import org.picocontainer.tck.AbstractComponentAdapterTestCase.RecordingLifecycleStrategy;
import org.picocontainer.testmodel.NullLifecycle;
import org.picocontainer.testmodel.RecordingLifecycle;
import org.picocontainer.testmodel.RecordingLifecycle.One;

import java.util.Properties;

/**
 * @author J&ouml;rg Schaible
 */
public class SetterInjectionTestCase extends AbstractComponentFactoryTestCase {
    protected void setUp() throws Exception {
        picoContainer = new DefaultPicoContainer(createComponentFactory());
    }

    protected ComponentFactory createComponentFactory() {
        return new SetterInjection();
    }

    public static interface Bean {
    }

    public static class NamedBean implements Bean {
        private String name;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }

    public static class NamedBeanWithPossibleDefault extends NamedBean {
        private boolean byDefault;

        public NamedBeanWithPossibleDefault() {
        }

        public NamedBeanWithPossibleDefault(String name) {
            setName(name);
            byDefault = true;
        }

        public boolean getByDefault() {
            return byDefault;
        }
    }

    public static class NoBean extends NamedBean {
        public NoBean(String name) {
            setName(name);
        }
    }

    public void testContainerUsesStandardConstructor() {
        picoContainer.addComponent(Bean.class, NamedBeanWithPossibleDefault.class);
        picoContainer.addComponent("Tom");
        NamedBeanWithPossibleDefault bean = (NamedBeanWithPossibleDefault) picoContainer.getComponent(Bean.class);
        assertFalse(bean.getByDefault());
    }

    public void testContainerUsesOnlyStandardConstructor() {
        picoContainer.addComponent(Bean.class, NoBean.class);
        picoContainer.addComponent("Tom");
        try {
            picoContainer.getComponent(Bean.class);
            fail("Instantiation should have failed.");
        } catch (PicoCompositionException e) {
        }
    }

    public void testCustomLifecycleCanBeInjected() throws NoSuchMethodException {
        RecordingLifecycleStrategy strategy = new RecordingLifecycleStrategy(new StringBuffer());
        SetterInjection componentFactory = new SetterInjection();
        SetterInjector sica = (SetterInjector)componentFactory.createComponentAdapter(new NullComponentMonitor(), strategy, new Properties(), NullLifecycle.class, NullLifecycle.class);
        One one = new RecordingLifecycle.One(new StringBuffer());
        sica.start(one);
        sica.stop(one);        
        sica.dispose(one);
        assertEquals("<start<stop<dispose", strategy.recording());
    }

    public static class AnotherNamedBean implements Bean {
        private String name;

        public String getName() {
            return name;
        }

        public void initName(String name) {
            this.name = name;
        }
    }

    public void testAlternatePrefixWorks() {
        picoContainer = new DefaultPicoContainer(new SetterInjection("init"));
        picoContainer.addComponent(Bean.class, AnotherNamedBean.class);
        picoContainer.addComponent("Tom");
        AnotherNamedBean bean = picoContainer.getComponent(AnotherNamedBean.class);
        assertEquals("Tom", bean.getName());
    }


}