/*****************************************************************************
 * Copyright (C) PicoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the LICENSE.txt file.                                                     *
 *                                                                           *
 * Original code by                                                          *
 *****************************************************************************/
package org.picocontainer;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import static org.picocontainer.behaviors.Behaviors.caching;
import static org.picocontainer.behaviors.Behaviors.implementationHiding;
import static org.picocontainer.behaviors.Behaviors.synchronizing;
import static org.picocontainer.injectors.Injectors.SDI;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Properties;

import org.junit.Before;
import org.junit.Test;
import org.picocontainer.behaviors.Caching;
import org.picocontainer.behaviors.ImplementationHiding;
import org.picocontainer.behaviors.Locking;
import org.picocontainer.behaviors.PropertyApplying;
import org.picocontainer.behaviors.Synchronizing;
import org.picocontainer.containers.EmptyPicoContainer;
import org.picocontainer.injectors.AdaptingInjection;
import org.picocontainer.injectors.AnnotatedFieldInjection;
import org.picocontainer.injectors.AnnotatedMethodInjection;
import org.picocontainer.injectors.ConstructorInjection;
import org.picocontainer.injectors.SetterInjection;
import org.picocontainer.lifecycle.NullLifecycleStrategy;
import org.picocontainer.lifecycle.ReflectionLifecycleStrategy;
import org.picocontainer.lifecycle.StartableLifecycleStrategy;
import org.picocontainer.monitors.ConsoleComponentMonitor;
import org.picocontainer.monitors.NullComponentMonitor;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;

public class PicoBuilderTestCase {

    private XStream xs;

    @Before
    public void setUp() throws Exception {
        xs = new XStream();
        xs.alias("PICO", DefaultPicoContainer.class);
        xs.registerConverter(new Converter() {
            public boolean canConvert(Class aClass) {
                return aClass.getName().equals("org.picocontainer.DefaultPicoContainer$1") ||
                       aClass.getName().equals("org.picocontainer.Properties") ||
                       aClass == Boolean.class ||
                       aClass == HashSet.class ||
                       aClass == ArrayList.class;
            }

            public void marshal(Object o, HierarchicalStreamWriter hierarchicalStreamWriter, MarshallingContext marshallingContext) {
            }

            public Object unmarshal(HierarchicalStreamReader hierarchicalStreamReader, UnmarshallingContext unmarshallingContext) {
                return null;
            }
        });
        xs.setMode(XStream.XPATH_ABSOLUTE_REFERENCES);
    }

    @Test public void testBasic() {
        MutablePicoContainer actual = new PicoBuilder().build();
        MutablePicoContainer expected = new DefaultPicoContainer(new AdaptingInjection(),
                new NullLifecycleStrategy(), new EmptyPicoContainer());
        assertEquals(xs.toXML(expected), xs.toXML(actual));
    }

    @Test public void testWithStartableLifecycle() {

        NullComponentMonitor ncm = new NullComponentMonitor();

        MutablePicoContainer actual = new PicoBuilder().withLifecycle().build();
        MutablePicoContainer expected = new DefaultPicoContainer(new AdaptingInjection(),
                new StartableLifecycleStrategy(ncm), new EmptyPicoContainer(), ncm);
        assertEquals(xs.toXML(expected), xs.toXML(actual));
    }

    @Test public void testWithReflectionLifecycle() {
        NullComponentMonitor ncm = new NullComponentMonitor();

        MutablePicoContainer actual = new PicoBuilder().withReflectionLifecycle().build();
        MutablePicoContainer expected = new DefaultPicoContainer(new AdaptingInjection(),
                new ReflectionLifecycleStrategy(ncm), new EmptyPicoContainer(), ncm);
        assertEquals(xs.toXML(expected), xs.toXML(actual));
    }


    @Test public void testWithConsoleMonitor() {
        MutablePicoContainer actual = new PicoBuilder().withConsoleMonitor().build();
        MutablePicoContainer expected = new DefaultPicoContainer(new AdaptingInjection(),
                new NullLifecycleStrategy(), new EmptyPicoContainer(), new ConsoleComponentMonitor());
        assertEquals(xs.toXML(expected), xs.toXML(actual));
    }

    @Test public void testWithConsoleMonitorAndLifecycleUseTheSameUltimateMonitor() {
        MutablePicoContainer actual = new PicoBuilder().withLifecycle().withConsoleMonitor().build();
        ConsoleComponentMonitor cm = new ConsoleComponentMonitor();
        MutablePicoContainer expected = new DefaultPicoContainer(new AdaptingInjection(),
                new StartableLifecycleStrategy(cm), new EmptyPicoContainer(), cm);
        assertEquals(xs.toXML(expected), xs.toXML(actual));
    }


    @Test public void testWithCustomMonitorByClass() {
        MutablePicoContainer actual = new PicoBuilder().withMonitor(ConsoleComponentMonitor.class).build();
        ConsoleComponentMonitor cm = new ConsoleComponentMonitor();
        MutablePicoContainer expected = new DefaultPicoContainer(new AdaptingInjection(),
                new NullLifecycleStrategy(), new EmptyPicoContainer(), cm);
        assertEquals(xs.toXML(expected), xs.toXML(actual));
    }

    @SuppressWarnings({ "unchecked" })
    @Test public void testWithBogusCustomMonitorByClass() {
        // We do unchecked assignment so we test what its really doing, and smart IDE's don't complain
        try {
            Class aClass = HashMap.class;
            new PicoBuilder().withMonitor(aClass).build();
            fail("should have barfed");
        } catch (ClassCastException e) {
            // expected
        }
    }

    @Test public void testWithImplementationHiding() {
        MutablePicoContainer actual = new PicoBuilder().withHiddenImplementations().build();
        MutablePicoContainer expected = new DefaultPicoContainer(new ImplementationHiding().wrap(new AdaptingInjection()),
                new NullLifecycleStrategy(), new EmptyPicoContainer(), new NullComponentMonitor());
        assertEquals(xs.toXML(expected), xs.toXML(actual));
    }

    @Test public void testWithImplementationHidingInstance() {
        MutablePicoContainer actual = new PicoBuilder().withComponentFactory(new ImplementationHiding()).build();
        MutablePicoContainer expected = new DefaultPicoContainer(new ImplementationHiding().wrap(new AdaptingInjection()),
                new NullLifecycleStrategy(), new EmptyPicoContainer(), new NullComponentMonitor());
        assertEquals(xs.toXML(expected), xs.toXML(actual));
    }

    @Test public void testWithCafsListChainThingy() {
        MutablePicoContainer actual = new PicoBuilder(SDI()).withBehaviors(caching(), synchronizing(), implementationHiding()).build();
        MutablePicoContainer expected = new DefaultPicoContainer(new Caching().wrap(new Synchronizing().wrap(new ImplementationHiding().wrap(new SetterInjection()))),
                new NullLifecycleStrategy(), new EmptyPicoContainer(), new NullComponentMonitor());
        assertEquals(xs.toXML(expected), xs.toXML(actual));
    }


    public static class CustomParentcontainer extends EmptyPicoContainer {}

    @Test public void testWithCustomParentContainer() {
        MutablePicoContainer actual = new PicoBuilder(new CustomParentcontainer()).build();
        MutablePicoContainer expected = new DefaultPicoContainer(new AdaptingInjection(),
                new NullLifecycleStrategy(), new CustomParentcontainer(), new NullComponentMonitor());
        assertEquals(xs.toXML(expected), xs.toXML(actual));
    }

    @Test public void testWithBogusParentContainerBehavesAsIfNotSet() {
        MutablePicoContainer actual = new PicoBuilder((PicoContainer)null).build();
        MutablePicoContainer expected = new DefaultPicoContainer(new AdaptingInjection(),
                new NullLifecycleStrategy(), new EmptyPicoContainer(), new NullComponentMonitor());
        assertEquals(xs.toXML(expected), xs.toXML(actual));
    }


    @Test public void testWithSetterDI() {
        MutablePicoContainer actual = new PicoBuilder().withSetterInjection().build();
        MutablePicoContainer expected = new DefaultPicoContainer(new SetterInjection(),
                new NullLifecycleStrategy(), new EmptyPicoContainer(), new NullComponentMonitor());
        assertEquals(xs.toXML(expected), xs.toXML(actual));
    }

    @Test public void testWithAnnotatedMethodDI() {
            MutablePicoContainer actual = new PicoBuilder().withAnnotatedMethodInjection().build();
        MutablePicoContainer expected = new DefaultPicoContainer(new AnnotatedMethodInjection(),
                new NullLifecycleStrategy(), new EmptyPicoContainer(), new NullComponentMonitor());
        assertEquals(xs.toXML(expected), xs.toXML(actual));
    }

    @Test public void testWithAnnotatedFieldDI() {
            MutablePicoContainer actual = new PicoBuilder().withAnnotatedFieldInjection().build();
        MutablePicoContainer expected = new DefaultPicoContainer(new AnnotatedFieldInjection(),
                new NullLifecycleStrategy(), new EmptyPicoContainer(), new NullComponentMonitor());
        assertEquals(xs.toXML(expected), xs.toXML(actual));
    }

    @Test public void testWithCtorDI() {
        MutablePicoContainer actual = new PicoBuilder().withConstructorInjection().build();
        MutablePicoContainer expected = new DefaultPicoContainer(new ConstructorInjection(),
                new NullLifecycleStrategy(), new EmptyPicoContainer(), new NullComponentMonitor());
        assertEquals(xs.toXML(expected), xs.toXML(actual));
    }

    @Test public void testWithImplementationHidingAndSetterDI() {
        MutablePicoContainer actual = new PicoBuilder().withHiddenImplementations().withSetterInjection().build();
        MutablePicoContainer expected = new DefaultPicoContainer(new ImplementationHiding().wrap(new SetterInjection()),
                new NullLifecycleStrategy(), new EmptyPicoContainer(), new NullComponentMonitor());
        assertEquals(xs.toXML(expected), xs.toXML(actual));
    }

    @Test public void testWithCachingImplementationHidingAndSetterDI() {
        MutablePicoContainer actual = new PicoBuilder().withCaching().withHiddenImplementations().withSetterInjection().build();
        MutablePicoContainer expected = new DefaultPicoContainer(new Caching().wrap(new ImplementationHiding().wrap(new SetterInjection())),
                new NullLifecycleStrategy(), new EmptyPicoContainer(), new NullComponentMonitor());
        assertEquals(xs.toXML(expected), xs.toXML(actual));
    }

    @Test public void testWithSynchronizing() {
        MutablePicoContainer actual = new PicoBuilder().withSynchronizing().build();
        MutablePicoContainer expected = new DefaultPicoContainer(new Synchronizing().wrap(new AdaptingInjection()),
                new NullLifecycleStrategy(), new EmptyPicoContainer(), new NullComponentMonitor());
        assertEquals(xs.toXML(expected), xs.toXML(actual));
    }

    @Test public void testWithLocking() {
        MutablePicoContainer actual = new PicoBuilder().withLocking().build();
        MutablePicoContainer expected = new DefaultPicoContainer(new Locking().wrap(new AdaptingInjection()),
                new NullLifecycleStrategy(), new EmptyPicoContainer(), new NullComponentMonitor());
        assertEquals(xs.toXML(expected), xs.toXML(actual));
    }

    @Test public void testWithPropertyApplier() {
        MutablePicoContainer actual = new PicoBuilder().withPropertyApplier().build();
        MutablePicoContainer expected = new DefaultPicoContainer(new PropertyApplying().wrap(new AdaptingInjection()),
                new NullLifecycleStrategy(), new EmptyPicoContainer(), new NullComponentMonitor());
        assertEquals(xs.toXML(expected), xs.toXML(actual));
    }

    //TODO - fix up to refer to SomeContainerDependency
    @Test public void testWithCustomComponentFactory() {
        MutablePicoContainer actual = new PicoBuilder().withCustomContainerComponent(new SomeContainerDependency()).withComponentFactory(CustomComponentFactory.class).build();
        MutablePicoContainer expected = new DefaultPicoContainer(new CustomComponentFactory(new SomeContainerDependency()),
                new NullLifecycleStrategy(), new EmptyPicoContainer(), new NullComponentMonitor());
        assertEquals(xs.toXML(expected), xs.toXML(actual));
    }

    public static class SomeContainerDependency {
    }
    public static class CustomComponentFactory implements ComponentFactory {

        @SuppressWarnings({ "UnusedDeclaration" })
        public CustomComponentFactory(SomeContainerDependency someDependency) {
        }

        public ComponentAdapter createComponentAdapter(ComponentMonitor componentMonitor,
                                                       LifecycleStrategy lifecycleStrategy,
                                                       Properties componentProperties,
                                                       Object componentKey,
                                                       Class componentImplementation,
                                                       Parameter... parameters) throws PicoCompositionException {
            return null;
        }

        public void verify(PicoContainer container) {
        }

        public void accept(PicoVisitor visitor) {
            visitor.visitComponentFactory(this);
        }
    }


    @Test public void testWithCustomPicoContainer() {
        MutablePicoContainer actual = new PicoBuilder().implementedBy(TestPicoContainer.class).build();
        MutablePicoContainer expected = new TestPicoContainer(new AdaptingInjection(),
                new NullComponentMonitor(), new NullLifecycleStrategy(), new EmptyPicoContainer());
        assertEquals(xs.toXML(expected), xs.toXML(actual));
    }


    public static class TestPicoContainer extends DefaultPicoContainer {
        public TestPicoContainer(ComponentFactory componentFactory, ComponentMonitor monitor, LifecycleStrategy lifecycleStrategy, PicoContainer parent) {
            super(componentFactory, lifecycleStrategy, parent, monitor);
        }
    }


}
