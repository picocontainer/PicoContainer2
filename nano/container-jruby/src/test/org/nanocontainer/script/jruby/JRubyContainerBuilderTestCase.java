package org.nanocontainer.script.jruby;

import org.picocontainer.ComponentAdapter;
import org.picocontainer.ComponentFactory;
import org.picocontainer.ComponentMonitor;
import org.picocontainer.DefaultPicoContainer;
import org.picocontainer.LifecycleStrategy;
import org.picocontainer.MutablePicoContainer;
import org.picocontainer.NameBinding;
import org.picocontainer.PicoBuilder;
import org.picocontainer.PicoContainer;
import org.picocontainer.adapters.InstanceAdapter;
import org.picocontainer.injectors.AbstractInjector;
import org.picocontainer.injectors.SetterInjection;
import org.picocontainer.injectors.SetterInjector;
import org.picocontainer.lifecycle.NullLifecycleStrategy;
import org.picocontainer.monitors.NullComponentMonitor;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.picocontainer.tck.MockFactory.mockeryWithCountingNamingScheme;

import java.io.File;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Properties;

import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JMock;
import org.jruby.exceptions.RaiseException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.nanocontainer.DefaultNanoContainer;
import org.nanocontainer.NanoContainer;
import org.nanocontainer.TestHelper;
import org.nanocontainer.integrationkit.LifecycleMode;
import org.picocontainer.PicoCompositionException;
import org.nanocontainer.script.AbstractScriptedContainerBuilderTestCase;
import org.nanocontainer.script.NanoContainerMarkupException;
import org.nanocontainer.testmodel.A;
import org.nanocontainer.testmodel.B;
import org.nanocontainer.testmodel.HasParams;
import org.nanocontainer.testmodel.ParentAssemblyScope;
import org.nanocontainer.testmodel.SomeAssemblyScope;
import org.nanocontainer.testmodel.X;
import org.picocontainer.ComponentAdapter;
import org.picocontainer.ComponentFactory;
import org.picocontainer.ComponentMonitor;
import org.picocontainer.DefaultPicoContainer;
import org.picocontainer.LifecycleStrategy;
import org.picocontainer.MutablePicoContainer;
import org.picocontainer.NameBinding;
import org.picocontainer.Parameter;
import org.picocontainer.PicoContainer;
import org.picocontainer.adapters.InstanceAdapter;
import org.picocontainer.injectors.AbstractInjector;
import org.picocontainer.injectors.SetterInjection;
import org.picocontainer.injectors.SetterInjector;
import org.picocontainer.lifecycle.NullLifecycleStrategy;
import org.picocontainer.monitors.NullComponentMonitor;

/**
 * @author Nick Sieger
 * @author Paul Hammant
 * @author Chris Bailey
 * @author Mauro Talevi
 */
@RunWith(JMock.class)
public class JRubyContainerBuilderTestCase extends AbstractScriptedContainerBuilderTestCase {
	private Mockery mockery = mockeryWithCountingNamingScheme();
	
	private static final String ASSEMBLY_SCOPE = "SOME_SCOPE";


    @Test public void testContainerCanBeBuiltWithParentGlobal() {
        Reader script = new StringReader(
                                         "StringBuffer = java.lang.StringBuffer\n" +
                                         "container(:parent => $parent) { \n" +
                                         "  component(StringBuffer)\n" +
                                         "}");
        PicoContainer parent = new DefaultPicoContainer();
        PicoContainer pico = buildContainer(script, parent, ASSEMBLY_SCOPE);
        //PicoContainer.getParent() is now ImmutablePicoContainer
        assertNotNull(pico.getParent());
        assertNotSame(parent, pico.getParent());
        assertEquals(StringBuffer.class, pico.getComponent(StringBuffer.class).getClass());
    }

    @Test public void testContainerCanBeBuiltWithComponentImplementation() {
        X.reset();
        Reader script = new StringReader(
                                         "A = org.nanocontainer.testmodel.A\n" +
                                         "container {\n" +
                                         "    component(A)\n" +
                                         "}");

        MutablePicoContainer pico = (MutablePicoContainer) buildContainer(script, null, ASSEMBLY_SCOPE);
        // LifecyleContainerBuilder starts the container
        pico.dispose();

        assertEquals("Should match the expression", "<AA>!A", X.componentRecorder);
    }

    @Test public void testContainerCanBeBuiltWithComponentInstance() {
        Reader script = new StringReader(
                                         "container { \n" +
                                         "  component(:key => 'string', :instance => 'foo')\n" +
                                         "}");

        PicoContainer pico = buildContainer(script, null, "SOME_SCOPE");

        assertEquals("foo", pico.getComponent("string"));
    }

    // TODO whoa - wrong addAdapter() being called.
    public void do_NOT_testBuildingWithPicoSyntax() {
        Reader script = new StringReader(
                                         "$parent.addAdapter('foo', Java::JavaClass.for_name('java.lang.String'))\n"
                                         +
                                         "DefaultPicoContainer = org.picocontainer.DefaultPicoContainer\n" +
                                         "pico = DefaultPicoContainer.new($parent)\n" +
                                         "pico.addAdapter(Java::JavaClass.for_name('org.nanocontainer.testmodel.A'))\n"
                                         +
                                         "pico");

        PicoContainer parent = new DefaultPicoContainer();
        PicoContainer pico = buildContainer(script, parent, "SOME_SCOPE");

        assertNotSame(parent, pico.getParent());
        System.err.println("-->1 " + pico.getComponents().get(0).getClass());
        assertNotNull(pico.getComponent(A.class));

        assertNotNull(pico.getComponent("foo"));
    }

    @Test public void testContainerBuiltWithMultipleComponentInstances() {
        Reader script = new StringReader(
                                         "container {\n" +
                                         "    component(:key => 'a', :instance => 'apple')\n" +
                                         "    component(:key => 'b', :instance => 'banana')\n" +
                                         "    component(:instance => 'noKeySpecified')\n" +
                                         "}");

        PicoContainer pico = buildContainer(script, null, ASSEMBLY_SCOPE);
        assertEquals("apple", pico.getComponent("a"));
        assertEquals("banana", pico.getComponent("b"));
        assertEquals("noKeySpecified", pico.getComponent(String.class));
    }

    @Test public void testShouldFailWhenNeitherClassNorInstanceIsSpecifiedForComponent() {
        Reader script = new StringReader(
                                         "container {\n" +
                                         "  component(:key => 'a')\n" +
                                         "}");

        try {
            buildContainer(script, null, ASSEMBLY_SCOPE);
            fail("NanoContainerMarkupException should have been raised");
        } catch(NanoContainerMarkupException e) {
            // expected
        }
    }

    @Test public void testAcceptsConstantParametersForComponent() {
        Reader script = new StringReader(
                                         "HasParams = org.nanocontainer.testmodel.HasParams\n" +
                                         "container {\n" +
                                         "    component(:key => 'byClass', :class => HasParams, :parameters => [ 'a', 'b', constant('c')])\n"
                                         +
                                         "}");

        PicoContainer pico = buildContainer(script, null, ASSEMBLY_SCOPE);
        HasParams byClass = (HasParams) pico.getComponent("byClass");
        assertEquals("abc", byClass.getParams());
    }

    @Test public void testAcceptsComponentClassNameAsString() {
        Reader script = new StringReader(
                                         "container {\n" +
                                         "    component(:key => 'byClassString', :class => 'org.nanocontainer.testmodel.HasParams', :parameters => [ 'c', 'a', 't' ])\n"
                                         +
                                         "}");

        PicoContainer pico = buildContainer(script, null, ASSEMBLY_SCOPE);
        HasParams byClassString = (HasParams) pico.getComponent("byClassString");
        assertEquals("cat", byClassString.getParams());
    }

    @Test public void testAcceptsComponentParametersForComponent() {
        Reader script = new StringReader(
                                         "A = org.nanocontainer.testmodel.A\n" +
                                         "B = org.nanocontainer.testmodel.B\n" +
                                         "container {\n" +
                                         "    component(:key => 'a1', :class => A)\n" +
                                         "    component(:key => 'a2', :class => A)\n" +
                                         "    component(:key => 'b1', :class => B, :parameters => [ key('a1') ])\n" +
                                         "    component(:key => 'b2', :class => B, :parameters => key('a2'))\n" +
                                         "}");

        PicoContainer pico = buildContainer(script, null, ASSEMBLY_SCOPE);
        A a1 = (A) pico.getComponent("a1");
        A a2 = (A) pico.getComponent("a2");
        B b1 = (B) pico.getComponent("b1");
        B b2 = (B) pico.getComponent("b2");

        assertNotNull(a1);
        assertNotNull(a2);
        assertNotNull(b1);
        assertNotNull(b2);

        assertSame(a1, b1.getA());
        assertSame(a2, b2.getA());
        assertNotSame(a1, a2);
        assertNotSame(b1, b2);
    }

    @Test public void testAcceptsComponentParameterWithClassNameKey() {
        Reader script = new StringReader(
                                         "A = org.nanocontainer.testmodel.A\n" +
                                         "B = org.nanocontainer.testmodel.B\n" +
                                         "container {\n" +
                                         "    component(:class => A)\n" +
                                         "    component(:key => B, :class => B, :parameters => key(A))\n" +
                                         "}");

        PicoContainer pico = buildContainer(script, null, ASSEMBLY_SCOPE);
        A a = pico.getComponent(A.class);
        B b = pico.getComponent(B.class);

        assertNotNull(a);
        assertNotNull(b);
        assertSame(a, b.getA());
    }

    @Test public void testInstantiateBasicComponentInDeeperTree() {
        X.reset();
        Reader script = new StringReader(
                                         "A = org.nanocontainer.testmodel.A\n" +
                                         "container {\n" +
                                         "  container {\n" +
                                         "    component(A)\n" +
                                         "  }\n" +
                                         "}");

        MutablePicoContainer pico = (MutablePicoContainer) buildContainer(script, null, ASSEMBLY_SCOPE);
        pico.dispose();
        assertEquals("Should match the expression", "<AA>!A", X.componentRecorder);
    }

    @Test public void testCustomComponentFactoryCanBeSpecified() {
        Reader script = new StringReader(
                                         "A = org.nanocontainer.testmodel.A\n" +
                                         "container(:component_adapter_factory => $assembly_scope) {\n" +
                                         "    component(A)\n" +
                                         "}");

        final A a = new A();
        
        final ComponentFactory componentFactory = mockery.mock(ComponentFactory.class);
        mockery.checking(new Expectations(){{
        	one(componentFactory).createComponentAdapter(with(any(ComponentMonitor.class)), with(any(LifecycleStrategy.class)), with(any(Properties.class)), with(same(A.class)), with(same(A.class)), with(aNull(Parameter[].class)));
            will(returnValue(new InstanceAdapter(A.class, a, new NullLifecycleStrategy(), new NullComponentMonitor())));
        }});
                                                                        
        PicoContainer pico = buildContainer(script, null, componentFactory);
        assertSame(a, pico.getComponent(A.class));
    }

    @Test public void testCustomComponentMonitorCanBeSpecified() {
        Reader script = new StringReader(
                                         "A = org.nanocontainer.testmodel.A\n" +
                                         "StringWriter = java.io.StringWriter\n" +
                                         "WriterComponentMonitor = org.picocontainer.monitors.WriterComponentMonitor\n" +
                                         "writer = StringWriter.new\n" +
                                         "monitor = WriterComponentMonitor.new(writer) \n" +
                                         "container(:component_monitor => monitor) {\n" +
                                         "    component(A)\n" +
                                         "    component(:key => StringWriter, :instance => writer)\n" +
                                         "}");

        //                            "container.monitor(WriterComponentMonitor.new).something_else() {\n" +

        PicoContainer pico = buildContainer(script, null, ASSEMBLY_SCOPE);
        StringWriter writer = pico.getComponent(StringWriter.class);
        assertTrue(writer.toString().length() > 0);
    }

    @Test public void testCustomComponentMonitorCanBeSpecifiedWhenCAFIsSpecified() {
        Reader script = new StringReader(
                                         "A = org.nanocontainer.testmodel.A\n" +
                                         "StringWriter = java.io.StringWriter\n" +
                                         "WriterComponentMonitor = org.picocontainer.monitors.WriterComponentMonitor\n" +
                                         "Caching = org.picocontainer.behaviors.Caching\n" +
                                         "ConstructorInjection = org.picocontainer.injectors.ConstructorInjection\n" +
                                         "writer = StringWriter.new\n" +
                                         "monitor = WriterComponentMonitor.new(writer) \n" +
                                         "container(:component_adapter_factory => Caching.new().wrap(ConstructorInjection.new), :component_monitor => monitor) {\n" +
                                         "    component(A)\n" +
                                         "    component(:key => StringWriter, :instance => writer)\n" +
                                         "}");

        PicoContainer pico = buildContainer(script, null, ASSEMBLY_SCOPE);
        StringWriter writer = pico.getComponent(StringWriter.class);
        assertTrue(writer.toString().length() > 0);
    }

    @Test public void testCustomComponentMonitorCanBeSpecifiedWhenParentIsSpecified() {
        DefaultNanoContainer parent = new DefaultNanoContainer();
        Reader script = new StringReader(
                                         "A = org.nanocontainer.testmodel.A\n" +
                                         "StringWriter = java.io.StringWriter\n" +
                                         "WriterComponentMonitor = org.picocontainer.monitors.WriterComponentMonitor\n" +
                                         "writer = StringWriter.new\n" +
                                         "monitor = WriterComponentMonitor.new(writer) \n" +
                                         "container(:parent => $parent, :component_monitor => monitor) {\n" +
                                         "    component(A)\n" +
                                         "    component(:key => StringWriter, :instance => writer)\n" +
                                         "}");

        PicoContainer pico = buildContainer(script, parent, ASSEMBLY_SCOPE);
        StringWriter writer = pico.getComponent(StringWriter.class);
        assertTrue(writer.toString().length() > 0);
    }

    @Test public void testCustomComponentMonitorCanBeSpecifiedWhenParentAndCAFAreSpecified() {
        DefaultNanoContainer parent = new DefaultNanoContainer();
        Reader script = new StringReader(
                                         "A = org.nanocontainer.testmodel.A\n" +
                                         "StringWriter = java.io.StringWriter\n" +
                                         "WriterComponentMonitor = org.picocontainer.monitors.WriterComponentMonitor\n" +
                                         "Caching = org.picocontainer.behaviors.Caching\n" +
                                         "ConstructorInjection = org.picocontainer.injectors.ConstructorInjection\n" +
                                         "writer = StringWriter.new\n" +
                                         "monitor = WriterComponentMonitor.new(writer) \n" +
                                         "container(:parent => $parent, :component_adapter_factory => Caching.new().wrap(ConstructorInjection.new), :component_monitor => monitor) {\n"
                                         +
                                         "    component(A)\n" +
                                         "    component(:key => StringWriter, :instance => writer)\n" +
                                         "}");

        PicoContainer pico = buildContainer(script, parent, ASSEMBLY_SCOPE);
        StringWriter writer = pico.getComponent(StringWriter.class);
        assertTrue(writer.toString().length() > 0);
    }

    @Test public void testInstantiateWithImpossibleComponentDependenciesConsideringTheHierarchy() {
        X.reset();
        Reader script = new StringReader(
                                         "A = org.nanocontainer.testmodel.A\n" +
                                         "B = org.nanocontainer.testmodel.B\n" +
                                         "C = org.nanocontainer.testmodel.C\n" +
                                         "container {\n" +
                                         "    component(B)\n" +
                                         "    container() {\n" +
                                         "        component(A)\n" +
                                         "    }\n" +
                                         "    component(C)\n" +
                                         "}");

        try {
            buildContainer(script, null, ASSEMBLY_SCOPE);
            fail("Should not have been able to instansiate component tree due to visibility/parent reasons.");
        } catch(AbstractInjector.UnsatisfiableDependenciesException expected) {
        }
    }

    @Test public void testInstantiateWithChildContainerAndStartStopAndDisposeOrderIsCorrect() {
        X.reset();
        Reader script = new StringReader(
                                         "A = org.nanocontainer.testmodel.A\n" +
                                         "B = org.nanocontainer.testmodel.B\n" +
                                         "C = org.nanocontainer.testmodel.C\n" +
                                         "container {\n" +
                                         "    component(A)\n" +
                                         "    container() {\n" +
                                         "         component(B)\n" +
                                         "    }\n" +
                                         "    component(C)\n" +
                                         "}\n");

        // A and C have no no dependancies. B Depends on A.
        MutablePicoContainer pico = (MutablePicoContainer) buildContainer(script, null, ASSEMBLY_SCOPE);
        pico.stop();
        pico.dispose();

        assertEquals("Should match the expression", "<A<C<BB>C>A>!B!C!A", X.componentRecorder);
    }

    @Test public void testBuildContainerWithParentAttribute() {
        DefaultNanoContainer parent = new DefaultNanoContainer();
        parent.addComponent("hello", "world");

        Reader script = new StringReader(
                                         "A = org.nanocontainer.testmodel.A\n" +
                                         "container(:parent => $parent) {\n" +
                                         "    component(A)\n" +
                                         "}\n");

        PicoContainer pico = buildContainer(script, parent, ASSEMBLY_SCOPE);
        // Should be able to get instance that was registered in the parent container
        assertEquals("world", pico.getComponent("hello"));
    }

    @Test public void testBuildContainerWithParentDependencyAndAssemblyScope() throws Exception {
        DefaultNanoContainer parent = new DefaultNanoContainer();
        parent.addComponent("a", A.class);

        String source =
                        "B = org.nanocontainer.testmodel.B\n" +
                        "SomeAssemblyScope = org.nanocontainer.testmodel.SomeAssemblyScope\n" +
                        "container(:parent => $parent) {\n" +
                        "  if $assembly_scope.kind_of?(SomeAssemblyScope)\n " +
                        "    component(B)\n" +
                        "  end\n " +
                        "}\n";

        Reader script = new StringReader(source);

        PicoContainer pico = buildContainer(script, parent, new SomeAssemblyScope());
        assertNotNull(pico.getComponent(B.class));

        script = new StringReader(source);
        pico = buildContainer(script, parent, ASSEMBLY_SCOPE);
        assertNull(pico.getComponent(B.class));
    }

    @Test public void testBuildContainerWithParentAndChildAssemblyScopes() throws IOException {
        String scriptValue =
                             "A = org.nanocontainer.testmodel.A\n" +
                             "B = org.nanocontainer.testmodel.B\n" +
                             "ParentAssemblyScope = org.nanocontainer.testmodel.ParentAssemblyScope\n" +
                             "SomeAssemblyScope = org.nanocontainer.testmodel.SomeAssemblyScope\n" +
                             "container(:parent => $parent) {\n" +
                             "  puts 'assembly_scope:'+$assembly_scope.inspect\n " +
                             "  case $assembly_scope\n" +
                             "  when ParentAssemblyScope\n " +
                             "    puts 'parent scope'\n " +
                             "    component(A)\n" +
                             "  when SomeAssemblyScope\n " +
                             "    puts 'child scope'\n " +
                             "    component(B)\n" +
                             "  else \n" +
                             "     raise 'Invalid Scope: ' +  $assembly_scope.inspect\n" +
                             "  end\n " +
                             "}\n";

        Reader script = new StringReader(scriptValue);
        NanoContainer parent = new DefaultNanoContainer(
            buildContainer(script, null, new ParentAssemblyScope()));
        assertNotNull(parent.getComponentAdapter(A.class, (NameBinding) null));

        script = new StringReader(scriptValue);
        PicoContainer pico = buildContainer(script, parent, new SomeAssemblyScope());
        assertNotNull(pico.getComponent(B.class));
    }
    
	@Test public void testAutoStartingContainerBuilderStarts() {
        A.reset();
        Reader script = new StringReader("" +
                "A = org.nanocontainer.testmodel.A\n" +
                "container(:parent => $parent) {\n" +
                "       component(A)\n" +
                "}\n");
        PicoContainer parent = new PicoBuilder().withLifecycle().withCaching().build();
        PicoContainer pico = buildContainer(new JRubyContainerBuilder(script, getClass().getClassLoader()), parent, "SOME_SCOPE");
        //PicoContainer.getParent() is now ImmutablePicoContainer
        assertNotSame(parent, pico.getParent());
        assertEquals("<A",A.componentRecorder);		
        A.reset();
	}
	
    @Test public void testNonAutoStartingContainerBuildDoesntAutostart() {
        A.reset();
        Reader script = new StringReader("" +
                "A = org.nanocontainer.testmodel.A\n" +
                "container(:parent => $parent) {\n" +
                "       component(A)\n" +
                "}\n");
        PicoContainer parent = new PicoBuilder().withLifecycle().withCaching().build();
        JRubyContainerBuilder containerBuilder = new JRubyContainerBuilder(script, getClass().getClassLoader(), LifecycleMode.NO_LIFECYCLE);
        PicoContainer pico = buildContainer(containerBuilder, parent, "SOME_SCOPE");
        //PicoContainer.getParent() is now ImmutablePicoContainer
        assertNotSame(parent, pico.getParent());
        assertEquals("",A.componentRecorder);
        A.reset();
    }
    

    public void FAILING_testBuildContainerWithParentAttributesPropagatesComponentFactory() {
        DefaultNanoContainer parent = new DefaultNanoContainer(new SetterInjection());
        Reader script = new StringReader("container(:parent => $parent)\n");

        MutablePicoContainer pico = (MutablePicoContainer) buildContainer(script, parent, ASSEMBLY_SCOPE);
        // Should be able to get instance that was registered in the parent container
        ComponentAdapter componentAdapter = pico.addComponent(String.class).getComponentAdapter(String.class, (NameBinding) null);
        assertTrue("ComponentAdapter should be originally defined by parent",
                   componentAdapter instanceof SetterInjector);
    }

    @Test public void testExceptionThrownWhenParentAttributeDefinedWithinChild() {
        DefaultNanoContainer parent = new DefaultNanoContainer(new SetterInjection());
        Reader script = new StringReader(
                                         "A = org.nanocontainer.testmodel.A\n" +
                                         "B = org.nanocontainer.testmodel.B\n" +
                                         "container() {\n" +
                                         "    component(A)\n" +
                                         "    container(:parent => $parent) {\n" +
                                         "         component(B)\n" +
                                         "    }\n" +
                                         "}\n");

        try {
            buildContainer(script, parent, ASSEMBLY_SCOPE);
            fail("NanoContainerMarkupException should have been thrown.");
        } catch(NanoContainerMarkupException ignore) {
            // expected
        }
    }

    //TODO
    @Test public void testSpuriousAttributes() {
        DefaultNanoContainer parent = new DefaultNanoContainer();

        Reader script = new StringReader(
                                         "container(:jim => 'Jam', :foo => 'bar')");
        try {
            buildContainer(script, parent, ASSEMBLY_SCOPE);
            //fail("Should throw exception upon spurious attributes?");
        } catch(NanoContainerMarkupException ex) {
            //ok?
        }
    }

    @Test public void testWithDynamicClassPathThatDoesNotExist() {
        DefaultNanoContainer parent = new DefaultNanoContainer();
        try {
            Reader script = new StringReader(
                                             "container {\n" +
                                             "  classPathElement(:path => 'this/path/does/not/exist.jar')\n" +
                                             "  component(:class => \"FooBar\")\n" +
                                             "}");

            buildContainer(script, parent, ASSEMBLY_SCOPE);
            fail("should have barfed with bad path exception");
        } catch(NanoContainerMarkupException e) {
            // excpected
        }

    }

    @Test public void testWithDynamicClassPath() {
        DefaultNanoContainer parent = new DefaultNanoContainer();
        Reader script = new StringReader(
            "TestHelper = org.nanocontainer.TestHelper\n"
            + "testCompJar = TestHelper.getTestCompJarFile()\n"
            + "compJarPath = testCompJar.getCanonicalPath()\n"
            + "container {\n"
            + "  classPathElement(:path => compJarPath)\n"
            + "  component(:class => \"TestComp\")\n"
            + "}" );

        MutablePicoContainer pico = (MutablePicoContainer) buildContainer(script, parent, ASSEMBLY_SCOPE);

        assertEquals(1, pico.getComponents().size());
        assertEquals("TestComp", pico.getComponents().get(0).getClass()
            .getName());
    }

    @Test public void testWithDynamicClassPathWithPermissions() {
        DefaultNanoContainer parent = new DefaultNanoContainer();
        Reader script = new StringReader(
            "TestHelper = org.nanocontainer.TestHelper\n" +
            "SocketPermission = java.net.SocketPermission\n"
            + "testCompJar = TestHelper.getTestCompJarFile()\n"
            + "compJarPath = testCompJar.getCanonicalPath()\n"
            + "container {\n"
            + "  classPathElement(:path => compJarPath) {\n"
            + "    grant(:perm => SocketPermission.new('google.com','connect'))\n"
            + "  }\n"
            + "  component(:class => \"TestComp\")\n"
            + "}" );

        MutablePicoContainer pico = (MutablePicoContainer) buildContainer(script, parent, ASSEMBLY_SCOPE);

        assertEquals(1, pico.getComponents().size());
        // can't actually test the permission under JUNIT control. We're just
        // testing the syntax here.
    }

    @Test public void testGrantPermissionInWrongPlace() {
        DefaultNanoContainer parent = new DefaultNanoContainer();
        try {
            Reader script = new StringReader(
                "TestHelper = org.nanocontainer.TestHelper\n" +
                "SocketPermission = java.net.SocketPermission\n" +
                "testCompJar = TestHelper.getTestCompJarFile()\n" +
                "container {\n" +
                "  grant(:perm => SocketPermission.new('google.com','connect'))\n" +
                "}");

            buildContainer(script, parent, ASSEMBLY_SCOPE);
            fail("should barf with RaiseException");
        } catch(PicoCompositionException e) {
            assertNotNull(e.getCause());
            assertTrue(e.getCause() instanceof RaiseException);
        }
    }


    @Test public void testWithParentClassPathPropagatesWithNoParentContainer() throws IOException {
        File testCompJar = TestHelper.getTestCompJarFile();

        URLClassLoader classLoader = new URLClassLoader(new URL[]{testCompJar.toURL()},
                                                        this.getClass().getClassLoader());
        Class testComp = null;

        try {
            testComp = classLoader.loadClass("TestComp");
        } catch(ClassNotFoundException ex) {
            fail("Unable to load test component from the jar using a url classloader");
        }
        Reader script = new StringReader(
            "container(:parent => $parent) {\n"
            + "  component(:class => \"TestComp\")\n"
            + "}");

        PicoContainer pico = buildContainer(new JRubyContainerBuilder(script, classLoader), null, null);
        assertNotNull(pico);
        Object testCompInstance = pico.getComponent(testComp.getName());
        assertSame(testCompInstance.getClass(), testComp);

    }

//    @Test public void testExceptionThrownWhenParentAttributeDefinedWithinChild() {
//        DefaultNanoContainer parent = new DefaultNanoContainer(new SetterInjectionComponentFactory() );
//        Reader script = new StringReader("" +
//                "package org.nanocontainer.testmodel\n" +
//                "nano = new GroovyNodeBuilder().container() {\n" +
//                "    addComponent(A)\n" +
//                "    container(parent:parent) {\n" +
//                "         addComponent(B)\n" +
//                "    }\n" +
//                "}\n");
//
//        try {
//            buildContainer(script, parent, ASSEMBLY_SCOPE);
//            fail("NanoContainerMarkupException should have been thrown.");
//        } catch (NanoContainerMarkupException ignore) {
//            // ignore
//        }
//    }
    

	/**
	 * Script will fail with not finding class TestComp if the classloader propagation is not working.
	 * @throws MalformedURLException
	 */
    @Test
	public void testWithParentClassPathPropagatesWithToInterpreter() throws MalformedURLException {
	    Reader script = new StringReader("" +
                "container(:parent => $parent) {\n" +
                "       component(:class => \"TestComp\")\n" +
                "}\n");
	
	    
	
	    File testCompJar = TestHelper.getTestCompJarFile();
	    assertTrue("Cannot find TestComp.jar. " + testCompJar.getAbsolutePath() + " Please set testcomp.jar system property before running.", testCompJar.exists());
	    //System.err.println("--> " + testCompJar.getAbsolutePath());
	    URLClassLoader classLoader = new URLClassLoader(new URL[] {testCompJar.toURI().toURL()}, this.getClass().getClassLoader());
	    Class testComp = null;
	    PicoContainer parent = new DefaultPicoContainer();
	
	    try {
	        testComp = classLoader.loadClass("TestComp");
	    } catch (ClassNotFoundException ex) {
	        ex.printStackTrace();
	        fail("Unable to load test component from the jar using a url classloader");
	    }
	
	    PicoContainer pico = buildContainer(new JRubyContainerBuilder(script, classLoader), parent, "SOME_SCOPE");
	    assertNotNull(pico);
	    Object testCompInstance = pico.getComponent("TestComp");
	    assertNotNull(testCompInstance);
	    assertEquals(testComp.getName(),testCompInstance.getClass().getName());
	
	}
    

    private PicoContainer buildContainer(Reader script, PicoContainer parent, Object scope) {
        return buildContainer(new JRubyContainerBuilder(script, getClass().getClassLoader()), parent, scope);
    }
}
