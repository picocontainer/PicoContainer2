package org.nanocontainer;

import org.picocontainer.ComponentFactory;
import org.picocontainer.ComponentMonitor;
import org.picocontainer.DefaultPicoContainer;
import org.picocontainer.LifecycleStrategy;
import org.picocontainer.MutablePicoContainer;
import org.picocontainer.PicoContainer;
import static org.picocontainer.behaviors.Behaviors.caching;
import static org.picocontainer.behaviors.Behaviors.implementationHiding;
import org.picocontainer.behaviors.ImplementationHiding;
import org.picocontainer.containers.EmptyPicoContainer;
import static org.picocontainer.injectors.Injectors.SDI;
import org.picocontainer.monitors.ConsoleComponentMonitor;

import java.util.HashMap;
import java.io.IOException;

import junit.framework.TestCase;

public class NanoBuilderTestCase extends TestCase {

    PrettyXmlRepresentation pxr;

    protected void setUp() throws Exception {
        pxr = new PrettyXmlRepresentation();
    }

    public void testBasic() throws IOException {
        NanoContainer nc = new NanoBuilder().build();
        String foo = pxr.simplifyRepresentation(nc);
        assertEquals("org.nanocontainer.DefaultNanoContainer\n" +
                "  delegate=org.picocontainer.DefaultPicoContainer\n" +
                "    componentFactory=org.picocontainer.injectors.AdaptingInjection\n" +
                "    parent=org.picocontainer.containers.EmptyPicoContainer\n" +
                "    lifecycleStrategy=org.picocontainer.lifecycle.NullLifecycleStrategy\n" +
                "    componentMonitor=org.picocontainer.monitors.NullComponentMonitor\n" +
                "",foo);
    }

    public void testWithStartableLifecycle() throws IOException {
        NanoContainer nc = new NanoBuilder().withLifecycle().build();
        String foo = pxr.simplifyRepresentation(nc);
        assertEquals("org.nanocontainer.DefaultNanoContainer\n" +
                "  delegate=org.picocontainer.DefaultPicoContainer\n" +
                "    componentFactory=org.picocontainer.injectors.AdaptingInjection\n" +
                "    parent=org.picocontainer.containers.EmptyPicoContainer\n" +
                "    lifecycleStrategy=org.picocontainer.lifecycle.StartableLifecycleStrategy\n" +
                "      componentMonitor=org.picocontainer.monitors.NullComponentMonitor\n" +
                "    componentMonitor=org.picocontainer.monitors.NullComponentMonitor reference=/org.nanocontainer.DefaultNanoContainer/delegate/lifecycleStrategy/componentMonitor\n",
                foo);
    }

    public void testWithReflectionLifecycle() throws IOException {
        NanoContainer nc = new NanoBuilder().withReflectionLifecycle().build();
        String foo = pxr.simplifyRepresentation(nc);
        assertEquals("org.nanocontainer.DefaultNanoContainer\n" +
                "  delegate=org.picocontainer.DefaultPicoContainer\n" +
                "    componentFactory=org.picocontainer.injectors.AdaptingInjection\n" +
                "    parent=org.picocontainer.containers.EmptyPicoContainer\n" +
                "    lifecycleStrategy=org.picocontainer.lifecycle.ReflectionLifecycleStrategy\n" +
                "      methodNames\n" +
                "        string:start\n" +
                "        string:stop\n" +
                "        string:dispose\n" +
                "      componentMonitor=org.picocontainer.monitors.NullComponentMonitor\n" +
                "    componentMonitor=org.picocontainer.monitors.NullComponentMonitor reference=/org.nanocontainer.DefaultNanoContainer/delegate/lifecycleStrategy/componentMonitor\n",
                foo);
    }

    public void testWithConsoleMonitor() throws IOException {
        NanoContainer nc = new NanoBuilder().withConsoleMonitor().build();
        String foo = pxr.simplifyRepresentation(nc);
        assertEquals("org.nanocontainer.DefaultNanoContainer\n" +
                "  delegate=org.picocontainer.DefaultPicoContainer\n" +
                "    componentFactory=org.picocontainer.injectors.AdaptingInjection\n" +
                "    parent=org.picocontainer.containers.EmptyPicoContainer\n" +
                "    lifecycleStrategy=org.picocontainer.lifecycle.NullLifecycleStrategy\n" +
                "    componentMonitor=org.picocontainer.monitors.ConsoleComponentMonitor\n" +
                "      delegate=org.picocontainer.monitors.NullComponentMonitor\n" +
                "",foo);
    }

    public void testWithCustomMonitorByClass() throws IOException {
        NanoContainer nc = new NanoBuilder().withMonitor(ConsoleComponentMonitor.class).build();
        String foo = pxr.simplifyRepresentation(nc);
        assertEquals("org.nanocontainer.DefaultNanoContainer\n" +
                "  delegate=org.picocontainer.DefaultPicoContainer\n" +
                "    componentFactory=org.picocontainer.injectors.AdaptingInjection\n" +
                "    parent=org.picocontainer.containers.EmptyPicoContainer\n" +
                "    lifecycleStrategy=org.picocontainer.lifecycle.NullLifecycleStrategy\n" +
                "    componentMonitor=org.picocontainer.monitors.ConsoleComponentMonitor\n" +
                "      delegate=org.picocontainer.monitors.NullComponentMonitor\n" +
                "",foo);
    }

    @SuppressWarnings({ "unchecked" })
    public void testWithBogusCustomMonitorByClass() {
        try {
            Class aClass = HashMap.class;
            new NanoBuilder().withMonitor(aClass).build();
            fail("should have barfed");
        } catch (ClassCastException e) {
            // expected
        }
    }

    public void testWithImplementationHiding() throws IOException {
        NanoContainer nc = new NanoBuilder().withHiddenImplementations().build();
        String foo = pxr.simplifyRepresentation(nc);
        assertEquals("org.nanocontainer.DefaultNanoContainer\n" +
                "  delegate=org.picocontainer.DefaultPicoContainer\n" +
                "    componentFactory=org.picocontainer.behaviors.ImplementationHiding\n" +
                "      delegate=org.picocontainer.injectors.AdaptingInjection\n" +
                "    parent=org.picocontainer.containers.EmptyPicoContainer\n" +
                "    lifecycleStrategy=org.picocontainer.lifecycle.NullLifecycleStrategy\n" +
                "    componentMonitor=org.picocontainer.monitors.NullComponentMonitor\n",foo);
    }


    public void testWithImplementationHidingInstance() throws IOException {
        NanoContainer nc = new NanoBuilder().withComponentFactory(new ImplementationHiding()).build();
        String foo = pxr.simplifyRepresentation(nc);
        assertEquals("org.nanocontainer.DefaultNanoContainer\n" +
                "  delegate=org.picocontainer.DefaultPicoContainer\n" +
                "    componentFactory=org.picocontainer.behaviors.ImplementationHiding\n" +
                "      delegate=org.picocontainer.injectors.AdaptingInjection\n" +
                "    parent=org.picocontainer.containers.EmptyPicoContainer\n" +
                "    lifecycleStrategy=org.picocontainer.lifecycle.NullLifecycleStrategy\n" +
                "    componentMonitor=org.picocontainer.monitors.NullComponentMonitor\n",
                foo);
    }

    public void testWithComponentFactoriesListChainThingy() throws IOException{
        NanoContainer nc = new NanoBuilder(SDI()).withComponentAdapterFactories(caching(), implementationHiding()).build();
        String foo = pxr.simplifyRepresentation(nc);
        assertEquals("org.nanocontainer.DefaultNanoContainer\n" +
                "  delegate=org.picocontainer.DefaultPicoContainer\n" +
                "    componentFactory=org.picocontainer.behaviors.Caching\n" +
                "      delegate=org.picocontainer.behaviors.ImplementationHiding\n" +
                "        delegate=org.picocontainer.injectors.SetterInjection\n" +
                "          set\n" +
                "    parent=org.picocontainer.containers.EmptyPicoContainer\n" +
                "    lifecycleStrategy=org.picocontainer.lifecycle.NullLifecycleStrategy\n" +
                "    componentMonitor=org.picocontainer.monitors.NullComponentMonitor\n",
                foo);
    }

    public static class CustomParentcontainer extends EmptyPicoContainer {
    }

    public void testWithCustomParentContainer() throws IOException {
        NanoContainer nc = new NanoBuilder(new CustomParentcontainer()).build();
        String foo = pxr.simplifyRepresentation(nc);
        assertEquals("org.nanocontainer.DefaultNanoContainer\n" +
                "  delegate=org.picocontainer.DefaultPicoContainer\n" +
                "    componentFactory=org.picocontainer.injectors.AdaptingInjection\n" +
                "    parent=org.nanocontainer.NanoBuilderTestCase$CustomParentcontainer\n" +
                "    lifecycleStrategy=org.picocontainer.lifecycle.NullLifecycleStrategy\n" +
                "    componentMonitor=org.picocontainer.monitors.NullComponentMonitor\n", foo);
    }

    public void testWithBogusParentContainerBehavesAsIfNotSet() throws IOException {
        NanoContainer nc = new NanoBuilder((PicoContainer)null).build();
        String foo = pxr.simplifyRepresentation(nc);
        assertEquals("org.nanocontainer.DefaultNanoContainer\n" +
                     "  delegate=org.picocontainer.DefaultPicoContainer\n" +
                     "    componentFactory=org.picocontainer.injectors.AdaptingInjection\n" +
                     "    parent=org.picocontainer.containers.EmptyPicoContainer\n" +
                     "    lifecycleStrategy=org.picocontainer.lifecycle.NullLifecycleStrategy\n" +
                     "    componentMonitor=org.picocontainer.monitors.NullComponentMonitor\n" +
                     "", foo);
    }


    public void testWithSetterDI() throws IOException {
        NanoContainer nc = new NanoBuilder().withSetterInjection().build();
        String foo = pxr.simplifyRepresentation(nc);
        assertEquals("org.nanocontainer.DefaultNanoContainer\n" +
                "  delegate=org.picocontainer.DefaultPicoContainer\n" +
                "    componentFactory=org.picocontainer.injectors.SetterInjection\n" +
                "      set\n" +
                "    parent=org.picocontainer.containers.EmptyPicoContainer\n" +
                "    lifecycleStrategy=org.picocontainer.lifecycle.NullLifecycleStrategy\n" +
                "    componentMonitor=org.picocontainer.monitors.NullComponentMonitor\n",
                foo);
    }

    public void testWithAnnotationDI() throws IOException {
        NanoContainer nc = new NanoBuilder().withAnnotationInjection().build();
        String foo = pxr.simplifyRepresentation(nc);
        assertEquals("org.nanocontainer.DefaultNanoContainer\n" +
                "  delegate=org.picocontainer.DefaultPicoContainer\n" +
                "    componentFactory=org.picocontainer.injectors.AnnotatedMethodInjection\n" +
                "      org.picocontainer.annotations.Inject\n" +
                "    parent=org.picocontainer.containers.EmptyPicoContainer\n" +
                "    lifecycleStrategy=org.picocontainer.lifecycle.NullLifecycleStrategy\n" +
                "    componentMonitor=org.picocontainer.monitors.NullComponentMonitor\n",
                foo);
    }

    public void testWithCtorDI() throws IOException {
        NanoContainer nc = new NanoBuilder().withConstructorInjection().build();
        String foo = pxr.simplifyRepresentation(nc);
        assertEquals("org.nanocontainer.DefaultNanoContainer\n" +
                "  delegate=org.picocontainer.DefaultPicoContainer\n" +
                "    componentFactory=org.picocontainer.injectors.ConstructorInjection\n" +
                "    parent=org.picocontainer.containers.EmptyPicoContainer\n" +
                "    lifecycleStrategy=org.picocontainer.lifecycle.NullLifecycleStrategy\n" +
                "    componentMonitor=org.picocontainer.monitors.NullComponentMonitor\n",foo);
    }

    public void testWithImplementationHidingAndSetterDI() throws IOException {
        NanoContainer nc = new NanoBuilder().withHiddenImplementations().withSetterInjection().build();
        String foo = pxr.simplifyRepresentation(nc);
        assertEquals("org.nanocontainer.DefaultNanoContainer\n" +
                "  delegate=org.picocontainer.DefaultPicoContainer\n" +
                "    componentFactory=org.picocontainer.behaviors.ImplementationHiding\n" +
                "      delegate=org.picocontainer.injectors.SetterInjection\n" +
                "        set\n" +
                "    parent=org.picocontainer.containers.EmptyPicoContainer\n" +
                "    lifecycleStrategy=org.picocontainer.lifecycle.NullLifecycleStrategy\n" +
                "    componentMonitor=org.picocontainer.monitors.NullComponentMonitor\n",
                foo);
    }

    public void testWithCachingImplementationHidingAndSetterDI() throws IOException {
        NanoContainer nc = new NanoBuilder().withCaching().withHiddenImplementations().withSetterInjection().build();
        String foo = pxr.simplifyRepresentation(nc);
        assertEquals("org.nanocontainer.DefaultNanoContainer\n" +
                "  delegate=org.picocontainer.DefaultPicoContainer\n" +
                "    componentFactory=org.picocontainer.behaviors.Caching\n" +
                "      delegate=org.picocontainer.behaviors.ImplementationHiding\n" +
                "        delegate=org.picocontainer.injectors.SetterInjection\n" +
                "          set\n" +                
                "    parent=org.picocontainer.containers.EmptyPicoContainer\n" +
                "    lifecycleStrategy=org.picocontainer.lifecycle.NullLifecycleStrategy\n" +
                "    componentMonitor=org.picocontainer.monitors.NullComponentMonitor\n",
                foo);
    }

    public void testWithThreadSafety() throws IOException {
        NanoContainer nc = new NanoBuilder().withThreadSafety().build();
        String foo = pxr.simplifyRepresentation(nc);
        assertEquals("org.nanocontainer.DefaultNanoContainer\n" +
                "  delegate=org.picocontainer.DefaultPicoContainer\n" +
                "    componentFactory=org.picocontainer.behaviors.Synchronizing\n" +
                "      delegate=org.picocontainer.injectors.AdaptingInjection\n" +
                "    parent=org.picocontainer.containers.EmptyPicoContainer\n" +
                "    lifecycleStrategy=org.picocontainer.lifecycle.NullLifecycleStrategy\n" +
                "    componentMonitor=org.picocontainer.monitors.NullComponentMonitor\n",
                foo);
    }

    public void testWithCustomNanoContainer() throws IOException {
        NanoContainer nc = new NanoBuilder().implementedBy(TestNanoContainer.class).build();
        String foo = pxr.simplifyRepresentation(nc);
        assertEquals("org.nanocontainer.NanoBuilderTestCase_-TestNanoContainer\n" +
                "  delegate=org.picocontainer.DefaultPicoContainer\n" +
                "    componentFactory=org.picocontainer.injectors.AdaptingInjection\n" +
                "    parent=org.picocontainer.containers.EmptyPicoContainer\n" +
                "    lifecycleStrategy=org.picocontainer.lifecycle.NullLifecycleStrategy\n" +
                "    componentMonitor=org.picocontainer.monitors.NullComponentMonitor\n",
                foo);
    }


    public static class TestNanoContainer extends DefaultNanoContainer {
        public TestNanoContainer(ClassLoader classLoader, MutablePicoContainer delegate) {
            super(classLoader, delegate);
        }
    }

    public void testWithCustomNanoAndPicoContainer() throws IOException {
        NanoContainer nc = new NanoBuilder().implementedBy(TestNanoContainer.class).picoImplementedBy(TestPicoContainer.class).build();
        String foo = pxr.simplifyRepresentation(nc);
        assertEquals("org.nanocontainer.NanoBuilderTestCase_-TestNanoContainer\n" +
                "  delegate=org.nanocontainer.NanoBuilderTestCase$TestPicoContainer\n" +
                "    componentFactory=org.picocontainer.injectors.AdaptingInjection\n" +
                "    parent=org.picocontainer.containers.EmptyPicoContainer\n" +
                "    lifecycleStrategy=org.picocontainer.lifecycle.NullLifecycleStrategy\n" +
                "    componentMonitor=org.picocontainer.monitors.NullComponentMonitor\n",
                foo);
    }

    public static class TestPicoContainer extends DefaultPicoContainer {
        public TestPicoContainer(ComponentFactory componentFactory, ComponentMonitor monitor, LifecycleStrategy lifecycleStrategy, PicoContainer parent) {
            super(componentFactory, lifecycleStrategy, parent, monitor);
        }
    }




}
