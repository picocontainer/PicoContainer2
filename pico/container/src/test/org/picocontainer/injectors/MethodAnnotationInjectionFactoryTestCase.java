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
import org.picocontainer.DefaultPicoContainer;
import org.picocontainer.injectors.MethodAnnotationInjectionFactory;
import org.picocontainer.injectors.MethodAnnotationInjector;
import org.picocontainer.monitors.NullComponentMonitor;
import org.picocontainer.tck.AbstractComponentAdapterFactoryTestCase;
import org.picocontainer.tck.AbstractComponentAdapterTestCase.RecordingLifecycleStrategy;
import org.picocontainer.testmodel.NullLifecycle;
import org.picocontainer.testmodel.RecordingLifecycle.One;

/**
 * @author J&ouml;rg Schaible</a>
 * @version $Revision$
 */
public class MethodAnnotationInjectionFactoryTestCase extends AbstractComponentAdapterFactoryTestCase {
    protected void setUp() throws Exception {
        picoContainer = new DefaultPicoContainer(createComponentFactory());
    }

    protected ComponentFactory createComponentFactory() {
        return new MethodAnnotationInjectionFactory();
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
        MethodAnnotationInjectionFactory componentFactory = new MethodAnnotationInjectionFactory();
        MethodAnnotationInjector aica = (MethodAnnotationInjector)componentFactory.createComponentAdapter(new NullComponentMonitor(), strategy, null, NullLifecycle.class, NullLifecycle.class);
        One one = new One(new StringBuffer());
        aica.start(one);
        aica.stop(one);
        aica.dispose(one);
        assertEquals("<start<stop<dispose", strategy.recording());
    }
}