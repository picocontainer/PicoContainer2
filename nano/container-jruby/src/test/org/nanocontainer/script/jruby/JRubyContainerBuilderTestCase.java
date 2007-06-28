package org.nanocontainer.script.jruby;

import org.picocontainer.ComponentAdapter;
import org.picocontainer.ComponentCharacteristics;
import org.picocontainer.ComponentFactory;
import org.picocontainer.ComponentMonitor;
import org.picocontainer.LifecycleStrategy;
import org.picocontainer.MutablePicoContainer;
import org.picocontainer.PicoContainer;
import org.picocontainer.DefaultPicoContainer;
import org.picocontainer.monitors.NullComponentMonitor;
import org.picocontainer.lifecycle.NullLifecycleStrategy;
import org.picocontainer.adapters.InstanceAdapter;
import org.picocontainer.injectors.SetterInjector;
import org.picocontainer.injectors.SetterInjectionFactory;
import org.picocontainer.injectors.AbstractInjector;

import java.io.File;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;
import java.net.URL;
import java.net.URLClassLoader;

import org.jmock.Mock;
import org.jmock.core.Constraint;
import org.jruby.exceptions.RaiseException;
import org.nanocontainer.DefaultNanoContainer;
import org.nanocontainer.NanoContainer;
import org.nanocontainer.TestHelper;
import org.nanocontainer.integrationkit.PicoCompositionException;
import org.nanocontainer.script.AbstractScriptedContainerBuilderTestCase;
import org.nanocontainer.script.NanoContainerMarkupException;
import org.nanocontainer.testmodel.A;
import org.nanocontainer.testmodel.B;
import org.nanocontainer.testmodel.HasParams;
import org.nanocontainer.testmodel.ParentAssemblyScope;
import org.nanocontainer.testmodel.SomeAssemblyScope;
import org.nanocontainer.testmodel.X;

/**
 * @author Nick Sieger
 * @author Paul Hammant
 * @author Chris Bailey
 * @author Mauro Talevi
 */
public class JRubyContainerBuilderTestCase extends AbstractScriptedContainerBuilderTestCase {
    private static final String ASSEMBLY_SCOPE = "SOME_SCOPE";


    public void testContainerCanBeBuiltWithParentGlobal() {
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

    public void testContainerCanBeBuiltWithComponentImplementation() {
        X.reset();
        Reader script = new StringReader(
                                         "A = org.nanocontainer.testmodel.A\n" +
                                         "container {\n" +
                                         "    component(A)\n" +
                                         "}");

        MutablePicoContainer pico = (MutablePicoContainer) buildContainer(script, null, ASSEMBLY_SCOPE);
        // LifecyleContainerBuilder starts the container
        pico.dispose();

        assertEquals("Should match the expression", "<A!A", X.componentRecorder);
    }

    public void testContainerCanBeBuiltWithComponentInstance() {
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

    public void testContainerBuiltWithMultipleComponentInstances() {
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

    public void testShouldFailWhenNeitherClassNorInstanceIsSpecifiedForComponent() {
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

    public void testAcceptsConstantParametersForComponent() {
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

    public void testAcceptsComponentClassNameAsString() {
        Reader script = new StringReader(
                                         "container {\n" +
                                         "    component(:key => 'byClassString', :class => 'org.nanocontainer.testmodel.HasParams', :parameters => [ 'c', 'a', 't' ])\n"
                                         +
                                         "}");

        PicoContainer pico = buildContainer(script, null, ASSEMBLY_SCOPE);
        HasParams byClassString = (HasParams) pico.getComponent("byClassString");
        assertEquals("cat", byClassString.getParams());
    }

    public void testAcceptsComponentParametersForComponent() {
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

    public void testAcceptsComponentParameterWithClassNameKey() {
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

    public void testInstantiateBasicComponentInDeeperTree() {
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
        assertEquals("Should match the expression", "<A!A", X.componentRecorder);
    }

    public void testCustomComponentAdapterFactoryCanBeSpecified() {
        Reader script = new StringReader(
                                         "A = org.nanocontainer.testmodel.A\n" +
                                         "container(:component_adapter_factory => $assembly_scope) {\n" +
                                         "    component(A)\n" +
                                         "}");

        A a = new A();
        Mock componentFactoryMock = mock(ComponentFactory.class);
        Constraint[] cons = {isA(ComponentMonitor.class), isA(LifecycleStrategy.class), isA(ComponentCharacteristics.class), same(A.class), same(A.class), eq(null)};
        componentFactoryMock.expects(once()).method("createComponentAdapter").with(cons)
            .will(returnValue(new InstanceAdapter(A.class, a, NullLifecycleStrategy.getInstance(),
                                                                        NullComponentMonitor.getInstance())));
        PicoContainer pico = buildContainer(script, null, componentFactoryMock.proxy());
        assertSame(a, pico.getComponent(A.class));
    }

    public void testCustomComponentMonitorCanBeSpecified() {
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

        PicoContainer pico = buildContainer(script, null, ASSEMBLY_SCOPE);
        StringWriter writer = pico.getComponent(StringWriter.class);
        assertTrue(writer.toString().length() > 0);
    }

    public void testCustomComponentMonitorCanBeSpecifiedWhenCAFIsSpecified() {
        Reader script = new StringReader(
                                         "A = org.nanocontainer.testmodel.A\n" +
                                         "StringWriter = java.io.StringWriter\n" +
                                         "WriterComponentMonitor = org.picocontainer.monitors.WriterComponentMonitor\n" +
                                         "CachingBehaviorFactory = org.picocontainer.behaviors.CachingBehaviorFactory\n" +
                                         "ConstructorInjectionFactory = org.picocontainer.injectors.ConstructorInjectionFactory\n" +
                                         "writer = StringWriter.new\n" +
                                         "monitor = WriterComponentMonitor.new(writer) \n" +
                                         "container(:component_adapter_factory => CachingBehaviorFactory.new().forThis(ConstructorInjectionFactory.new), :component_monitor => monitor) {\n" +
                                         "    component(A)\n" +
                                         "    component(:key => StringWriter, :instance => writer)\n" +
                                         "}");

        PicoContainer pico = buildContainer(script, null, ASSEMBLY_SCOPE);
        StringWriter writer = pico.getComponent(StringWriter.class);
        assertTrue(writer.toString().length() > 0);
    }

    public void testCustomComponentMonitorCanBeSpecifiedWhenParentIsSpecified() {
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

    public void testCustomComponentMonitorCanBeSpecifiedWhenParentAndCAFAreSpecified() {
        DefaultNanoContainer parent = new DefaultNanoContainer();
        Reader script = new StringReader(
                                         "A = org.nanocontainer.testmodel.A\n" +
                                         "StringWriter = java.io.StringWriter\n" +
                                         "WriterComponentMonitor = org.picocontainer.monitors.WriterComponentMonitor\n" +
                                         "CachingBehaviorFactory = org.picocontainer.behaviors.CachingBehaviorFactory\n" +
                                         "ConstructorInjectionFactory = org.picocontainer.injectors.ConstructorInjectionFactory\n" +
                                         "writer = StringWriter.new\n" +
                                         "monitor = WriterComponentMonitor.new(writer) \n" +
                                         "container(:parent => $parent, :component_adapter_factory => CachingBehaviorFactory.new().forThis(ConstructorInjectionFactory.new), :component_monitor => monitor) {\n"
                                         +
                                         "    component(A)\n" +
                                         "    component(:key => StringWriter, :instance => writer)\n" +
                                         "}");

        PicoContainer pico = buildContainer(script, parent, ASSEMBLY_SCOPE);
        StringWriter writer = pico.getComponent(StringWriter.class);
        assertTrue(writer.toString().length() > 0);
    }

    public void testInstantiateWithImpossibleComponentDependenciesConsideringTheHierarchy() {
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

    public void testInstantiateWithChildContainerAndStartStopAndDisposeOrderIsCorrect() {
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

    public void testBuildContainerWithParentAttribute() {
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

    public void testBuildContainerWithParentDependencyAndAssemblyScope() throws Exception {
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

    public void testBuildContainerWithParentAndChildAssemblyScopes() throws IOException {
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
        assertNotNull(parent.getComponentAdapter(A.class));

        script = new StringReader(scriptValue);
        PicoContainer pico = buildContainer(script, parent, new SomeAssemblyScope());
        assertNotNull(pico.getComponent(B.class));
    }

    public void FAILING_testBuildContainerWithParentAttributesPropagatesComponentAdapterFactory() {
        DefaultNanoContainer parent = new DefaultNanoContainer(new SetterInjectionFactory());
        Reader script = new StringReader("container(:parent => $parent)\n");

        MutablePicoContainer pico = (MutablePicoContainer) buildContainer(script, parent, ASSEMBLY_SCOPE);
        // Should be able to get instance that was registered in the parent container
        ComponentAdapter componentAdapter = pico.addComponent(String.class).getComponentAdapter(String.class);
        assertTrue("ComponentAdapter should be originally defined by parent",
                   componentAdapter instanceof SetterInjector);
    }

    public void testExceptionThrownWhenParentAttributeDefinedWithinChild() {
        DefaultNanoContainer parent = new DefaultNanoContainer(new SetterInjectionFactory());
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
    public void testSpuriousAttributes() {
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

    public void testWithDynamicClassPathThatDoesNotExist() {
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

    public void testWithDynamicClassPath() {
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

    public void testWithDynamicClassPathWithPermissions() {
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

    public void testGrantPermissionInWrongPlace() {
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


    public void testWithParentClassPathPropagatesWithNoParentContainer() throws IOException {
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

//    public void testExceptionThrownWhenParentAttributeDefinedWithinChild() {
//        DefaultNanoContainer parent = new DefaultNanoContainer(new SetterInjectionComponentAdapterFactory() );
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

    private PicoContainer buildContainer(Reader script, PicoContainer parent, Object scope) {
        return buildContainer(new JRubyContainerBuilder(script, getClass().getClassLoader()), parent, scope);
    }
}
