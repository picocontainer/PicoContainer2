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

import static org.picocontainer.behaviors.Behaviors.caching;
import static org.picocontainer.behaviors.Behaviors.implementationHiding;
import static org.picocontainer.behaviors.Behaviors.synchronizing;
import static org.picocontainer.injectors.Injectors.SDI;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Properties;

import junit.framework.TestCase;

import org.junit.Test;
import org.picocontainer.behaviors.ImplementationHiding;
import org.picocontainer.containers.EmptyPicoContainer;
import org.picocontainer.lifecycle.LifecycleState;
import org.picocontainer.monitors.ConsoleComponentMonitor;
import org.picocontainer.monitors.NullComponentMonitor;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;

public class PicoBuilderTestCase extends TestCase {

    private XStream xs;

    protected void setUp() throws Exception {
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
        MutablePicoContainer mpc = new PicoBuilder().build();
        String foo = simplifyRepresentation(mpc);
        assertEquals("PICO\n" +
                "  componentFactory=org.picocontainer.injectors.AdaptingInjection\n" +
                "  parent=org.picocontainer.containers.EmptyPicoContainer\n" +
                "  lifecycleStrategy=org.picocontainer.lifecycle.NullLifecycleStrategy\n" +
                "  componentMonitor=org.picocontainer.monitors.NullComponentMonitor\n" +
                "PICO",foo);
    }

    @Test public void testWithStartableLifecycle() {

        new NullComponentMonitor();

        MutablePicoContainer mpc = new PicoBuilder().withLifecycle().build();
        String foo = simplifyRepresentation(mpc);
        assertEquals("PICO\n" +
                "  componentFactory=org.picocontainer.injectors.AdaptingInjection\n" +
                "  parent=org.picocontainer.containers.EmptyPicoContainer\n" +
                "  lifecycleStrategy=org.picocontainer.lifecycle.StartableLifecycleStrategy\n" +
                "    componentMonitor=org.picocontainer.monitors.NullComponentMonitor\n" +
                "  componentMonitor=org.picocontainer.monitors.NullComponentMonitor reference=/PICO/lifecycleStrategy/componentMonitor\n" +
                "PICO",foo);
    }

    @Test public void testWithReflectionLifecycle() {
        MutablePicoContainer mpc = new PicoBuilder().withReflectionLifecycle().build();
        String foo = simplifyRepresentation(mpc);
        assertEquals("PICO\n" +
                "  componentFactory=org.picocontainer.injectors.AdaptingInjection\n" +
                "  parent=org.picocontainer.containers.EmptyPicoContainer\n" +
                "  lifecycleStrategy=org.picocontainer.lifecycle.ReflectionLifecycleStrategy\n" +
                "    methodNames\n" +
                "      stringstartstring\n" +
                "      stringstopstring\n" +
                "      stringdisposestring\n" +
                "    methodNames\n" +
                "    componentMonitor=org.picocontainer.monitors.NullComponentMonitor\n" +
                "  componentMonitor=org.picocontainer.monitors.NullComponentMonitor reference=/PICO/lifecycleStrategy/componentMonitor\n" +
                "PICO",foo);
    }


    @Test public void testWithConsoleMonitor() {
        MutablePicoContainer mpc = new PicoBuilder().withConsoleMonitor().build();
        String foo = simplifyRepresentation(mpc);
        assertEquals("PICO\n" +
                "  componentFactory=org.picocontainer.injectors.AdaptingInjection\n" +
                "  parent=org.picocontainer.containers.EmptyPicoContainer\n" +
                "  lifecycleStrategy=org.picocontainer.lifecycle.NullLifecycleStrategy\n" +
                "  componentMonitor=org.picocontainer.monitors.ConsoleComponentMonitor\n" +
                "    delegate=org.picocontainer.monitors.NullComponentMonitor\n" +
                "PICO",foo);
    }

    @Test public void testWithConsoleMonitorAndLifecycleUseTheSameUltimateMonitor() {
        MutablePicoContainer mpc = new PicoBuilder().withLifecycle().withConsoleMonitor().build();
        String foo = simplifyRepresentation(mpc);
        assertEquals("PICO\n" +
                     "  componentFactory=org.picocontainer.injectors.AdaptingInjection\n" +
                     "  parent=org.picocontainer.containers.EmptyPicoContainer\n" +
                     "  lifecycleStrategy=org.picocontainer.lifecycle.StartableLifecycleStrategy\n" +
                     "    componentMonitor=org.picocontainer.monitors.ConsoleComponentMonitor\n" +
                     "      delegate=org.picocontainer.monitors.NullComponentMonitor\n" +
                     "  componentMonitor=org.picocontainer.monitors.ConsoleComponentMonitor reference=/PICO/lifecycleStrategy/componentMonitor\n" +
                     "PICO",foo);
    }


    @Test public void testWithCustomMonitorByClass() {
        MutablePicoContainer mpc = new PicoBuilder().withMonitor(ConsoleComponentMonitor.class).build();
        String foo = simplifyRepresentation(mpc);
        assertEquals("PICO\n" +
                "  componentFactory=org.picocontainer.injectors.AdaptingInjection\n" +
                "  parent=org.picocontainer.containers.EmptyPicoContainer\n" +
                "  lifecycleStrategy=org.picocontainer.lifecycle.NullLifecycleStrategy\n" +
                "  componentMonitor=org.picocontainer.monitors.ConsoleComponentMonitor\n" +
                "    delegate=org.picocontainer.monitors.NullComponentMonitor\n" +
                "PICO",foo);
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
        MutablePicoContainer mpc = new PicoBuilder().withHiddenImplementations().build();
        String foo = simplifyRepresentation(mpc);
        assertEquals("PICO\n" +
                "  componentFactory=org.picocontainer.behaviors.ImplementationHiding\n" +
                "    delegate=org.picocontainer.injectors.AdaptingInjection\n" +
                "  parent=org.picocontainer.containers.EmptyPicoContainer\n" +
                "  lifecycleStrategy=org.picocontainer.lifecycle.NullLifecycleStrategy\n" +
                "  componentMonitor=org.picocontainer.monitors.NullComponentMonitor\n" +
                "PICO",foo);
    }

    @Test public void testWithImplementationHidingInstance() {
        MutablePicoContainer mpc = new PicoBuilder().withComponentFactory(new ImplementationHiding()).build();
        String foo = simplifyRepresentation(mpc);
        assertEquals("PICO\n" +
                "  componentFactory=org.picocontainer.behaviors.ImplementationHiding\n" +
                "    delegate=org.picocontainer.injectors.AdaptingInjection\n" +
                "  parent=org.picocontainer.containers.EmptyPicoContainer\n" +
                "  lifecycleStrategy=org.picocontainer.lifecycle.NullLifecycleStrategy\n" +
                "  componentMonitor=org.picocontainer.monitors.NullComponentMonitor\n" +
                "PICO",foo);
    }

    @Test public void testWithCafsListChainThingy() {
        MutablePicoContainer mpc = new PicoBuilder(SDI()).withBehaviors(caching(), synchronizing(), implementationHiding()).build();
        String foo = simplifyRepresentation(mpc);
        assertEquals("PICO\n" +
                "  componentFactory=org.picocontainer.behaviors.Caching\n" +
                "    delegate=org.picocontainer.behaviors.Synchronizing\n" +
                "      delegate=org.picocontainer.behaviors.ImplementationHiding\n" +
                "        delegate=org.picocontainer.injectors.SetterInjection\n" +
                "          set\n" +
                "  parent=org.picocontainer.containers.EmptyPicoContainer\n" +
                "  lifecycleStrategy=org.picocontainer.lifecycle.NullLifecycleStrategy\n" +
                "  componentMonitor=org.picocontainer.monitors.NullComponentMonitor\n" +
                "PICO",foo);
    }


    public static class CustomParentcontainer extends EmptyPicoContainer {}

    @Test public void testWithCustomParentContainer() {
        MutablePicoContainer mpc = new PicoBuilder(new CustomParentcontainer()).build();
        String foo = simplifyRepresentation(mpc);
        assertEquals("PICO\n" +
                "  componentFactory=org.picocontainer.injectors.AdaptingInjection\n" +
                "  parent=org.picocontainer.PicoBuilderTestCase_CustomParentcontainer\n" +
                "  lifecycleStrategy=org.picocontainer.lifecycle.NullLifecycleStrategy\n" +
                "  componentMonitor=org.picocontainer.monitors.NullComponentMonitor\n" +
                "PICO",foo);
    }

    @Test public void testWithBogusParentContainerBehavesAsIfNotSet() {
        MutablePicoContainer mpc = new PicoBuilder((PicoContainer)null).build();
        String foo = simplifyRepresentation(mpc);
        assertEquals("PICO\n" +
                     "  componentFactory=org.picocontainer.injectors.AdaptingInjection\n" +
                     "  parent=org.picocontainer.containers.EmptyPicoContainer\n" +
                     "  lifecycleStrategy=org.picocontainer.lifecycle.NullLifecycleStrategy\n" +
                     "  componentMonitor=org.picocontainer.monitors.NullComponentMonitor\n" +
                     "PICO", foo);
    }


    @Test public void testWithSetterDI() {
        MutablePicoContainer mpc = new PicoBuilder().withSetterInjection().build();
        String foo = simplifyRepresentation(mpc);
        assertEquals("PICO\n" +
                "  componentFactory=org.picocontainer.injectors.SetterInjection\n" +
                "    set\n" +
                "  parent=org.picocontainer.containers.EmptyPicoContainer\n" +
                "  lifecycleStrategy=org.picocontainer.lifecycle.NullLifecycleStrategy\n" +
                "  componentMonitor=org.picocontainer.monitors.NullComponentMonitor\n" +
                "PICO",foo);
    }

    @Test public void testWithAnnotatedMethodDI() {
            MutablePicoContainer mpc = new PicoBuilder().withAnnotatedMethodInjection().build();
        String foo = simplifyRepresentation(mpc);
        assertEquals("PICO\n" +
                "  componentFactory=org.picocontainer.injectors.AnnotatedMethodInjection\n" +
                "    org.picocontainer.annotations.Inject\n" +
                "  parent=org.picocontainer.containers.EmptyPicoContainer\n" +
                "  lifecycleStrategy=org.picocontainer.lifecycle.NullLifecycleStrategy\n" +
                "  componentMonitor=org.picocontainer.monitors.NullComponentMonitor\n" +
                "PICO",foo);
    }

    @Test public void testWithAnnotatedFieldDI() {
            MutablePicoContainer mpc = new PicoBuilder().withAnnotatedFieldInjection().build();
        String foo = simplifyRepresentation(mpc);
        assertEquals("PICO\n" +
                "  componentFactory=org.picocontainer.injectors.AnnotatedFieldInjection\n" +
                "    org.picocontainer.annotations.Inject\n" +
                "  parent=org.picocontainer.containers.EmptyPicoContainer\n" +
                "  lifecycleStrategy=org.picocontainer.lifecycle.NullLifecycleStrategy\n" +
                "  componentMonitor=org.picocontainer.monitors.NullComponentMonitor\n" +
                "PICO",foo);
    }

    @Test public void testWithCtorDI() {
        MutablePicoContainer mpc = new PicoBuilder().withConstructorInjection().build();
        String foo = simplifyRepresentation(mpc);
        assertEquals("PICO\n" +
                "  componentFactory=org.picocontainer.injectors.ConstructorInjection\n" +
                "  parent=org.picocontainer.containers.EmptyPicoContainer\n" +
                "  lifecycleStrategy=org.picocontainer.lifecycle.NullLifecycleStrategy\n" +
                "  componentMonitor=org.picocontainer.monitors.NullComponentMonitor\n" +
                "PICO",foo);
    }

    @Test public void testWithImplementationHidingAndSetterDI() {
        MutablePicoContainer mpc = new PicoBuilder().withHiddenImplementations().withSetterInjection().build();
        String foo = simplifyRepresentation(mpc);
        assertEquals("PICO\n" +
                "  componentFactory=org.picocontainer.behaviors.ImplementationHiding\n" +
                "    delegate=org.picocontainer.injectors.SetterInjection\n" +
                "      set\n" +
                "  parent=org.picocontainer.containers.EmptyPicoContainer\n" +
                "  lifecycleStrategy=org.picocontainer.lifecycle.NullLifecycleStrategy\n" +
                "  componentMonitor=org.picocontainer.monitors.NullComponentMonitor\n" +
                "PICO",foo);
    }

    @Test public void testWithCachingImplementationHidingAndSetterDI() {
        MutablePicoContainer mpc = new PicoBuilder().withCaching().withHiddenImplementations().withSetterInjection().build();
        String foo = simplifyRepresentation(mpc);
        assertEquals("PICO\n" +
                "  componentFactory=org.picocontainer.behaviors.Caching\n" +
                "    delegate=org.picocontainer.behaviors.ImplementationHiding\n" +
                "      delegate=org.picocontainer.injectors.SetterInjection\n" +
                "        set\n" +                
                "  parent=org.picocontainer.containers.EmptyPicoContainer\n" +
                "  lifecycleStrategy=org.picocontainer.lifecycle.NullLifecycleStrategy\n" +
                "  componentMonitor=org.picocontainer.monitors.NullComponentMonitor\n" +
                "PICO",foo);
    }

    @Test public void testWithSynchronizing() {
        MutablePicoContainer mpc = new PicoBuilder().withSynchronizing().build();
        String foo = simplifyRepresentation(mpc);
        assertEquals("PICO\n" +
                "  componentFactory=org.picocontainer.behaviors.Synchronizing\n" +
                "    delegate=org.picocontainer.injectors.AdaptingInjection\n" +
                "  parent=org.picocontainer.containers.EmptyPicoContainer\n" +
                "  lifecycleStrategy=org.picocontainer.lifecycle.NullLifecycleStrategy\n" +
                "  componentMonitor=org.picocontainer.monitors.NullComponentMonitor\n" +
                "PICO",foo);
    }

    @Test public void testWithLocking() {
        MutablePicoContainer mpc = new PicoBuilder().withLocking().build();
        String foo = simplifyRepresentation(mpc);
        assertEquals("PICO\n" +
                "  componentFactory=org.picocontainer.behaviors.Locking\n" +
                "    delegate=org.picocontainer.injectors.AdaptingInjection\n" +
                "  parent=org.picocontainer.containers.EmptyPicoContainer\n" +
                "  lifecycleStrategy=org.picocontainer.lifecycle.NullLifecycleStrategy\n" +
                "  componentMonitor=org.picocontainer.monitors.NullComponentMonitor\n" +
                "PICO",foo);
    }

    @Test public void testWithPropertyApplier() {
        MutablePicoContainer mpc = new PicoBuilder().withPropertyApplier().build();
        String foo = simplifyRepresentation(mpc);
        assertEquals("PICO\n" +
                     "  componentFactory=org.picocontainer.behaviors.PropertyApplying\n" +
                     "    delegate=org.picocontainer.injectors.AdaptingInjection\n" +
                     "  parent=org.picocontainer.containers.EmptyPicoContainer\n" +
                     "  lifecycleStrategy=org.picocontainer.lifecycle.NullLifecycleStrategy\n" +
                     "  componentMonitor=org.picocontainer.monitors.NullComponentMonitor\n" +
                     "PICO",foo);
    }

    //TODO - fix up to refer to SomeContainerDependency
    @Test public void testWithCustomComponentFactory() {
        MutablePicoContainer mpc = new PicoBuilder().withCustomContainerComponent(new SomeContainerDependency()).withComponentFactory(CustomComponentFactory.class).build();
        String foo = simplifyRepresentation(mpc);
        assertEquals("PICO\n" +
                     "  componentFactory=org.picocontainer.PicoBuilderTestCase_CustomComponentFactory\n" +
                     "  parent=org.picocontainer.containers.EmptyPicoContainer\n" +
                     "  lifecycleStrategy=org.picocontainer.lifecycle.NullLifecycleStrategy\n" +
                     "  componentMonitor=org.picocontainer.monitors.NullComponentMonitor\n" +
                     "PICO",foo);
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
    }


    @Test public void testWithCustomPicoContainer() {
        MutablePicoContainer mpc = new PicoBuilder().implementedBy(TestPicoContainer.class).build();
        String foo = simplifyRepresentation(mpc);
        assertEquals("org.picocontainer.PicoBuilderTestCase_-TestPicoContainer\n" +
                "  componentFactory=org.picocontainer.injectors.AdaptingInjection\n" +
                "  parent=org.picocontainer.containers.EmptyPicoContainer\n" +
                "  lifecycleStrategy=org.picocontainer.lifecycle.NullLifecycleStrategy\n" +
                "  componentMonitor=org.picocontainer.monitors.NullComponentMonitor\n" +
                "org.picocontainer.PicoBuilderTestCase_-TestPicoContainer",foo);
    }


    public static class TestPicoContainer extends DefaultPicoContainer {
        public TestPicoContainer(ComponentFactory componentFactory, ComponentMonitor monitor, LifecycleStrategy lifecycleStrategy, PicoContainer parent) {
            super(componentFactory, lifecycleStrategy, parent, monitor);
        }
    }


    private String simplifyRepresentation(MutablePicoContainer mpc) {
        String foo = xs.toXML(mpc);
        foo = foo.replace('$','_');
        foo = foo.replaceAll("/>","");
        foo = foo.replaceAll("</","");
        foo = foo.replaceAll("<","");
        foo = foo.replaceAll(">","");
        foo = foo.replaceAll("\n  startedComponentAdapters","");
        foo = foo.replaceAll("\n  componentKeyToAdapterCache","");
        foo = foo.replaceAll("\n  componentAdapters","");
        foo = foo.replaceAll("\n  orderedComponentAdapters","");
        foo = foo.replaceAll("\n  childrenStarted","");
        foo = foo.replaceAll("\n  handler","");
        foo = foo.replaceAll("\n  children","");
        foo = foo.replaceAll("injectionAnnotation","");
        foo = foo.replaceAll("setterMethodPrefix","");
        foo = foo.replaceAll("lifecycleState","");
        for (LifecycleState eachState : LifecycleState.values()) {
            foo = foo.replaceAll("\n  " + eachState.name(), "");        	
        }
        foo = foo.replaceAll("\n  lifecycleStrategy\n","\n");
        foo = foo.replaceAll("\n  componentMonitor\n","\n");
        foo = foo.replaceAll("\n    componentMonitor\n","\n");
        foo = foo.replaceAll("\n  delegate\n","\n");
        foo = foo.replaceAll("\n    useNames\n","\n");
        foo = foo.replaceAll("\n    delegate\n","\n");
        foo = foo.replaceAll("\n      delegate\n","\n");
        foo = foo.replaceAll("\n        delegate\n","\n");
        foo = foo.replaceAll("\n  componentCharacteristic class=\"org.picocontainer.DefaultPicoContainer$1\"","");
        foo = foo.replaceAll("\n  componentProperties","");
        foo = foo.replaceAll("\n    startedComponentAdapters","");
        foo = foo.replaceAll("\"class=","\"\nclass=");
        foo = foo.replaceAll("\n  componentFactory\n","\n");
        foo = foo.replaceAll("\n  lifecycleManager","");
        foo = foo.replaceAll("class=\"org.picocontainer.DefaultPicoContainer_1\"","");
        foo = foo.replaceAll("class=","=");
        foo = foo.replaceAll("\"","");
        foo = foo.replaceAll(" \n","\n");
        foo = foo.replaceAll(" =","=");
        foo = foo.replaceAll("\n\n","\n");

        return foo;
    }


}
