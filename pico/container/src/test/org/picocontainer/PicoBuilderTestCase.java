package org.picocontainer;

import static org.picocontainer.behaviors.Behaviors.caching;
import static org.picocontainer.behaviors.Behaviors.implHiding;
import static org.picocontainer.behaviors.Behaviors.threadSafe;
import org.picocontainer.behaviors.ImplementationHidingBehaviorFactory;
import org.picocontainer.containers.EmptyPicoContainer;
import static org.picocontainer.injectors.Injectors.SDI;
import org.picocontainer.monitors.ConsoleComponentMonitor;
import org.picocontainer.monitors.NullComponentMonitor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;
import junit.framework.TestCase;

public class PicoBuilderTestCase extends TestCase {

    private XStream xs;

    protected void setUp() throws Exception {
        xs = new XStream();
        xs.alias("PICO", DefaultPicoContainer.class);
        xs.registerConverter(new Converter() {
            public boolean canConvert(Class aClass) {
                return aClass.getName().equals("org.picocontainer.DefaultPicoContainer$OrderedComponentAdapterLifecycleManager") ||
                       aClass.getName().equals("org.picocontainer.DefaultPicoContainer$1") ||
                       aClass.getName().equals("org.picocontainer.ComponentCharacteristics") ||
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

    public void testBasic() {
        MutablePicoContainer mpc = new PicoBuilder().build();
        String foo = simplifyRepresentation(mpc);
        assertEquals("PICO\n" +
                "  componentFactory=org.picocontainer.injectors.AdaptiveInjectionFactory\n" +
                "  parent=org.picocontainer.containers.EmptyPicoContainer\n" +
                "  lifecycleStrategy=org.picocontainer.lifecycle.NullLifecycleStrategy\n" +
                "  componentMonitor=org.picocontainer.monitors.NullComponentMonitor\n" +
                "PICO",foo);
    }

    public void testWithStartableLifecycle() {

        NullComponentMonitor.getInstance();

        MutablePicoContainer mpc = new PicoBuilder().withLifecycle().build();
        String foo = simplifyRepresentation(mpc);
        assertEquals("PICO\n" +
                "  componentFactory=org.picocontainer.injectors.AdaptiveInjectionFactory\n" +
                "  parent=org.picocontainer.containers.EmptyPicoContainer\n" +
                "  lifecycleStrategy=org.picocontainer.lifecycle.StartableLifecycleStrategy\n" +
                "    componentMonitor=org.picocontainer.monitors.NullComponentMonitor\n" +
                "  componentMonitor=org.picocontainer.monitors.NullComponentMonitor reference=/PICO/lifecycleStrategy/componentMonitor\n" +
                "PICO",foo);
    }

    public void testWithReflectionLifecycle() {
        MutablePicoContainer mpc = new PicoBuilder().withReflectionLifecycle().build();
        String foo = simplifyRepresentation(mpc);
        assertEquals("PICO\n" +
                "  componentFactory=org.picocontainer.injectors.AdaptiveInjectionFactory\n" +
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


    public void testWithConsoleMonitor() {
        MutablePicoContainer mpc = new PicoBuilder().withConsoleMonitor().build();
        String foo = simplifyRepresentation(mpc);
        assertEquals("PICO\n" +
                "  componentFactory=org.picocontainer.injectors.AdaptiveInjectionFactory\n" +
                "  parent=org.picocontainer.containers.EmptyPicoContainer\n" +
                "  lifecycleStrategy=org.picocontainer.lifecycle.NullLifecycleStrategy\n" +
                "  componentMonitor=org.picocontainer.monitors.ConsoleComponentMonitor\n" +
                "    delegate=org.picocontainer.monitors.NullComponentMonitor\n" +
                "PICO",foo);
    }

    public void testWithConsoleMonitorAndLifecycleUseTheSameUltimateMonitor() {
        MutablePicoContainer mpc = new PicoBuilder().withLifecycle().withConsoleMonitor().build();
        String foo = simplifyRepresentation(mpc);
        assertEquals("PICO\n" +
                     "  componentFactory=org.picocontainer.injectors.AdaptiveInjectionFactory\n" +
                     "  parent=org.picocontainer.containers.EmptyPicoContainer\n" +
                     "  lifecycleStrategy=org.picocontainer.lifecycle.StartableLifecycleStrategy\n" +
                     "    componentMonitor=org.picocontainer.monitors.ConsoleComponentMonitor\n" +
                     "      delegate=org.picocontainer.monitors.NullComponentMonitor\n" +
                     "  componentMonitor=org.picocontainer.monitors.ConsoleComponentMonitor reference=/PICO/lifecycleStrategy/componentMonitor\n" +
                     "PICO",foo);
    }


    public void testWithCustomMonitorByClass() {
        MutablePicoContainer mpc = new PicoBuilder().withMonitor(ConsoleComponentMonitor.class).build();
        String foo = simplifyRepresentation(mpc);
        assertEquals("PICO\n" +
                "  componentFactory=org.picocontainer.injectors.AdaptiveInjectionFactory\n" +
                "  parent=org.picocontainer.containers.EmptyPicoContainer\n" +
                "  lifecycleStrategy=org.picocontainer.lifecycle.NullLifecycleStrategy\n" +
                "  componentMonitor=org.picocontainer.monitors.ConsoleComponentMonitor\n" +
                "    delegate=org.picocontainer.monitors.NullComponentMonitor\n" +
                "PICO",foo);
    }

    @SuppressWarnings({ "unchecked" })
    public void testWithBogusCustomMonitorByClass() {
        // We do unchecked assignment so we test what its really doing, and smart IDE's don't complain
        try {
            Class aClass = HashMap.class;
            new PicoBuilder().withMonitor(aClass).build();
            fail("should have barfed");
        } catch (ClassCastException e) {
            // expected
        }
    }

    public void testWithImplementationHiding() {
        MutablePicoContainer mpc = new PicoBuilder().withHiddenImplementations().build();
        String foo = simplifyRepresentation(mpc);
        assertEquals("PICO\n" +
                "  componentFactory=org.picocontainer.behaviors.ImplementationHidingBehaviorFactory\n" +
                "    delegate=org.picocontainer.injectors.AdaptiveInjectionFactory\n" +
                "  parent=org.picocontainer.containers.EmptyPicoContainer\n" +
                "  lifecycleStrategy=org.picocontainer.lifecycle.NullLifecycleStrategy\n" +
                "  componentMonitor=org.picocontainer.monitors.NullComponentMonitor\n" +
                "PICO",foo);
    }

    public void testWithImplementationHidingInstance() {
        MutablePicoContainer mpc = new PicoBuilder().withComponentFactory(new ImplementationHidingBehaviorFactory()).build();
        String foo = simplifyRepresentation(mpc);
        assertEquals("PICO\n" +
                "  componentFactory=org.picocontainer.behaviors.ImplementationHidingBehaviorFactory\n" +
                "    delegate=org.picocontainer.injectors.AdaptiveInjectionFactory\n" +
                "  parent=org.picocontainer.containers.EmptyPicoContainer\n" +
                "  lifecycleStrategy=org.picocontainer.lifecycle.NullLifecycleStrategy\n" +
                "  componentMonitor=org.picocontainer.monitors.NullComponentMonitor\n" +
                "PICO",foo);
    }

    public void testWithCafsListChainThingy() {
        MutablePicoContainer mpc = new PicoBuilder(SDI()).withBehaviors(caching(), threadSafe(), implHiding()).build();
        String foo = simplifyRepresentation(mpc);
        assertEquals("PICO\n" +
                "  componentFactory=org.picocontainer.behaviors.CachingBehaviorFactory\n" +
                "    delegate=org.picocontainer.behaviors.SynchronizedBehaviorFactory\n" +
                "      delegate=org.picocontainer.behaviors.ImplementationHidingBehaviorFactory\n" +
                "        delegate=org.picocontainer.injectors.SetterInjectionFactory\n" +
                "  parent=org.picocontainer.containers.EmptyPicoContainer\n" +
                "  lifecycleStrategy=org.picocontainer.lifecycle.NullLifecycleStrategy\n" +
                "  componentMonitor=org.picocontainer.monitors.NullComponentMonitor\n" +
                "PICO",foo);
    }


    public static class CustomParentcontainer extends EmptyPicoContainer {}

    public void testWithCustomParentContainer() {
        MutablePicoContainer mpc = new PicoBuilder(new CustomParentcontainer()).build();
        String foo = simplifyRepresentation(mpc);
        assertEquals("PICO\n" +
                "  componentFactory=org.picocontainer.injectors.AdaptiveInjectionFactory\n" +
                "  parent=org.picocontainer.PicoBuilderTestCase_CustomParentcontainer\n" +
                "  lifecycleStrategy=org.picocontainer.lifecycle.NullLifecycleStrategy\n" +
                "  componentMonitor=org.picocontainer.monitors.NullComponentMonitor\n" +
                "PICO",foo);
    }

    public void testWithBogusParentContainerBehavesAsIfNotSet() {
        MutablePicoContainer mpc = new PicoBuilder((PicoContainer)null).build();
        String foo = simplifyRepresentation(mpc);
        assertEquals("PICO\n" +
                     "  componentFactory=org.picocontainer.injectors.AdaptiveInjectionFactory\n" +
                     "  parent=org.picocontainer.containers.EmptyPicoContainer\n" +
                     "  lifecycleStrategy=org.picocontainer.lifecycle.NullLifecycleStrategy\n" +
                     "  componentMonitor=org.picocontainer.monitors.NullComponentMonitor\n" +
                     "PICO", foo);
    }


    public void testWithSetterDI() {
        MutablePicoContainer mpc = new PicoBuilder().withSetterInjection().build();
        String foo = simplifyRepresentation(mpc);
        assertEquals("PICO\n" +
                "  componentFactory=org.picocontainer.injectors.SetterInjectionFactory\n" +
                "  parent=org.picocontainer.containers.EmptyPicoContainer\n" +
                "  lifecycleStrategy=org.picocontainer.lifecycle.NullLifecycleStrategy\n" +
                "  componentMonitor=org.picocontainer.monitors.NullComponentMonitor\n" +
                "PICO",foo);
    }

    public void testWithAnnotationDI() {
        MutablePicoContainer mpc = new PicoBuilder().withAnnotationInjection().build();
        String foo = simplifyRepresentation(mpc);
        assertEquals("PICO\n" +
                "  componentFactory=org.picocontainer.injectors.MethodAnnotationInjectionFactory\n" +
                "  parent=org.picocontainer.containers.EmptyPicoContainer\n" +
                "  lifecycleStrategy=org.picocontainer.lifecycle.NullLifecycleStrategy\n" +
                "  componentMonitor=org.picocontainer.monitors.NullComponentMonitor\n" +
                "PICO",foo);
    }

    public void testWithCtorDI() {
        MutablePicoContainer mpc = new PicoBuilder().withConstructorInjection().build();
        String foo = simplifyRepresentation(mpc);
        assertEquals("PICO\n" +
                "  componentFactory=org.picocontainer.injectors.ConstructorInjectionFactory\n" +
                "  parent=org.picocontainer.containers.EmptyPicoContainer\n" +
                "  lifecycleStrategy=org.picocontainer.lifecycle.NullLifecycleStrategy\n" +
                "  componentMonitor=org.picocontainer.monitors.NullComponentMonitor\n" +
                "PICO",foo);
    }

    public void testWithImplementationHidingAndSetterDI() {
        MutablePicoContainer mpc = new PicoBuilder().withHiddenImplementations().withSetterInjection().build();
        String foo = simplifyRepresentation(mpc);
        assertEquals("PICO\n" +
                "  componentFactory=org.picocontainer.behaviors.ImplementationHidingBehaviorFactory\n" +
                "    delegate=org.picocontainer.injectors.SetterInjectionFactory\n" +
                "  parent=org.picocontainer.containers.EmptyPicoContainer\n" +
                "  lifecycleStrategy=org.picocontainer.lifecycle.NullLifecycleStrategy\n" +
                "  componentMonitor=org.picocontainer.monitors.NullComponentMonitor\n" +
                "PICO",foo);
    }

    public void testWithCachingImplementationHidingAndSetterDI() {
        MutablePicoContainer mpc = new PicoBuilder().withCaching().withHiddenImplementations().withSetterInjection().build();
        String foo = simplifyRepresentation(mpc);
        assertEquals("PICO\n" +
                "  componentFactory=org.picocontainer.behaviors.CachingBehaviorFactory\n" +
                "    delegate=org.picocontainer.behaviors.ImplementationHidingBehaviorFactory\n" +
                "      delegate=org.picocontainer.injectors.SetterInjectionFactory\n" +
                "  parent=org.picocontainer.containers.EmptyPicoContainer\n" +
                "  lifecycleStrategy=org.picocontainer.lifecycle.NullLifecycleStrategy\n" +
                "  componentMonitor=org.picocontainer.monitors.NullComponentMonitor\n" +
                "PICO",foo);
    }

    public void testWithThreadSafety() {
        MutablePicoContainer mpc = new PicoBuilder().withThreadSafety().build();
        String foo = simplifyRepresentation(mpc);
        assertEquals("PICO\n" +
                "  componentFactory=org.picocontainer.behaviors.SynchronizedBehaviorFactory\n" +
                "    delegate=org.picocontainer.injectors.AdaptiveInjectionFactory\n" +
                "  parent=org.picocontainer.containers.EmptyPicoContainer\n" +
                "  lifecycleStrategy=org.picocontainer.lifecycle.NullLifecycleStrategy\n" +
                "  componentMonitor=org.picocontainer.monitors.NullComponentMonitor\n" +
                "PICO",foo);
    }

    public void testWithPropertyApplier() {
        MutablePicoContainer mpc = new PicoBuilder().withPropertyApplier().build();
        String foo = simplifyRepresentation(mpc);
        assertEquals("PICO\n" +
                     "  componentFactory=org.picocontainer.behaviors.PropertyApplyingBehaviorFactory\n" +
                     "    delegate=org.picocontainer.injectors.AdaptiveInjectionFactory\n" +
                     "  parent=org.picocontainer.containers.EmptyPicoContainer\n" +
                     "  lifecycleStrategy=org.picocontainer.lifecycle.NullLifecycleStrategy\n" +
                     "  componentMonitor=org.picocontainer.monitors.NullComponentMonitor\n" +
                     "PICO",foo);
    }

    public void testWithCustomComponentFactory() {
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
                                                       ComponentCharacteristics componentCharacteristics,
                                                       Object componentKey,
                                                       Class componentImplementation,
                                                       Parameter... parameters) throws
                                                                                PicoCompositionException
        {
            return null;
        }
    }


    public void testWithCustomPicoContainer() {
        MutablePicoContainer mpc = new PicoBuilder().implementedBy(TestPicoContainer.class).build();
        String foo = simplifyRepresentation(mpc);
        assertEquals("org.picocontainer.PicoBuilderTestCase_-TestPicoContainer\n" +
                "  componentFactory=org.picocontainer.injectors.AdaptiveInjectionFactory\n" +
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
        foo = foo.replaceAll("\n  childrenStarted","");
        foo = foo.replaceAll("\n  componentAdapters","");
        foo = foo.replaceAll("\n  orderedComponentAdapters","");
        foo = foo.replaceAll("\n  started","");
        foo = foo.replaceAll("\n  disposed","");
        foo = foo.replaceAll("\n  handler","");
        foo = foo.replaceAll("\n  children","");
        foo = foo.replaceAll("\n  lifecycleStrategy\n","\n");
        foo = foo.replaceAll("\n  componentMonitor\n","\n");
        foo = foo.replaceAll("\n    componentMonitor\n","\n");
        foo = foo.replaceAll("\n  delegate\n","\n");
        foo = foo.replaceAll("\n    delegate\n","\n");
        foo = foo.replaceAll("\n      delegate\n","\n");
        foo = foo.replaceAll("\n  componentCharacteristic class=\"org.picocontainer.DefaultPicoContainer$1\"","");
        foo = foo.replaceAll("\n  componentCharacteristics","");
        foo = foo.replaceAll("\n  componentKeyToAdapterCache","");
        foo = foo.replaceAll("\n    startedComponentAdapters","");
        foo = foo.replaceAll("\"class=","\"\nclass=");
        foo = foo.replaceAll("\n  componentFactory\n","\n");
        foo = foo.replaceAll("\n  lifecycleManager","");
        foo = foo.replaceAll("class=\"org.picocontainer.DefaultPicoContainer_1\"","");
        foo = foo.replaceAll("class=\"org.picocontainer.DefaultPicoContainer_OrderedComponentAdapterLifecycleManager\"","");
        foo = foo.replaceAll("class=","=");
        foo = foo.replaceAll("\"","");
        foo = foo.replaceAll(" \n","\n");
        foo = foo.replaceAll(" =","=");
        foo = foo.replaceAll("\n\n","\n");

        return foo;
    }


}
