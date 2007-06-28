package org.nanocontainer.script.groovy;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;

import org.jmock.Mock;
import org.jmock.core.Constraint;
import org.nanocontainer.TestHelper;
import org.nanocontainer.DefaultNanoContainer;
import org.nanocontainer.NanoContainer;
import org.nanocontainer.integrationkit.PicoCompositionException;
import org.nanocontainer.script.AbstractScriptedContainerBuilderTestCase;
import org.nanocontainer.script.NanoContainerMarkupException;
import org.nanocontainer.testmodel.A;
import org.nanocontainer.testmodel.B;
import org.nanocontainer.testmodel.HasParams;
import org.nanocontainer.testmodel.ParentAssemblyScope;
import org.nanocontainer.testmodel.SomeAssemblyScope;
import org.nanocontainer.testmodel.WebServerConfig;
import org.nanocontainer.testmodel.X;
import org.picocontainer.ComponentAdapter;
import org.picocontainer.MutablePicoContainer;
import org.picocontainer.PicoContainer;
import org.picocontainer.ComponentCharacteristics;
import org.picocontainer.ComponentMonitor;
import org.picocontainer.LifecycleStrategy;
import org.picocontainer.ComponentFactory;
import org.picocontainer.behaviors.CachingBehaviorFactory;
import org.picocontainer.injectors.AbstractInjector;
import org.picocontainer.monitors.NullComponentMonitor;
import org.picocontainer.lifecycle.NullLifecycleStrategy;
import org.picocontainer.adapters.InstanceAdapter;
import org.picocontainer.injectors.SetterInjector;
import org.picocontainer.injectors.SetterInjectionFactory;
import org.picocontainer.injectors.AdaptiveInjectionFactory;

import java.io.File;
import java.net.URLClassLoader;
import java.net.URL;

/**
 *
 * @author Paul Hammant
 * @author Mauro Talevi
 * @version $Revision: 1775 $
 */
public class GroovyNodeBuilderTestCase extends AbstractScriptedContainerBuilderTestCase {
    private static final String ASSEMBLY_SCOPE = "SOME_SCOPE";

    public void testInstantiateBasicScriptable() throws PicoCompositionException {
        Reader script = new StringReader("" +
                "import org.nanocontainer.testmodel.*\n" +
                "X.reset()\n" +
                "nano = builder.container {\n" +
                "    component(A)\n" +
                "}");

        MutablePicoContainer pico = (MutablePicoContainer) buildContainer(script, null, ASSEMBLY_SCOPE);
        // LifecyleContainerBuilder starts the container

        pico.dispose();

        assertEquals("Should match the expression", "<A!A", X.componentRecorder);
    }

    public void testComponentInstances() throws PicoCompositionException {
        Reader script = new StringReader("" +
                "import org.nanocontainer.testmodel.*\n" +
                "nano = builder.container {\n" +
                "    component(key:'a', instance:'apple')\n" +
                "    component(key:'b', instance:'banana')\n" +
                "    component(instance:'noKeySpecified')\n" +
                "}");

        PicoContainer pico = buildContainer(script, null, ASSEMBLY_SCOPE);
        assertEquals("apple", pico.getComponent("a"));
        assertEquals("banana", pico.getComponent("b"));
        assertEquals("noKeySpecified", pico.getComponent(String.class));
    }

    public void testShouldFailWhenNeitherClassNorInstanceIsSpecifiedForComponent() {
        Reader script = new StringReader("" +
                "nano = builder.container {\n" +
                "    component(key:'a')\n" +
                "}");

        try {
            buildContainer(script, null, ASSEMBLY_SCOPE);
            fail("NanoContainerMarkupException should have been raised");
        } catch (NanoContainerMarkupException e) {
            // expected
        }
    }

    public void testShouldAcceptConstantParametersForComponent() throws PicoCompositionException {
        Reader script = new StringReader("" +
                "import org.picocontainer.parameters.ConstantParameter\n" +
                "import org.nanocontainer.testmodel.*\n" +
                "" +
                "nano = builder.container {\n" +
                "    component(key:'byClass', class:HasParams, parameters:[ 'a', 'b', new ConstantParameter('c') ])\n" +
                "    component(key:'byClassString', class:'org.nanocontainer.testmodel.HasParams', parameters:[ 'c', 'a', 't' ])\n" +
                "}");

        PicoContainer pico = buildContainer(script, null, ASSEMBLY_SCOPE);
        HasParams byClass = (HasParams) pico.getComponent("byClass");
        assertEquals("abc", byClass.getParams());

        HasParams byClassString = (HasParams) pico.getComponent("byClassString");
        assertEquals("cat", byClassString.getParams());
    }

    public void testShouldAcceptComponentParametersForComponent() throws PicoCompositionException {
        Reader script = new StringReader("" +
                "import org.picocontainer.parameters.ComponentParameter\n" +
                "import org.nanocontainer.testmodel.*\n" +
                "nano = builder.container {\n" +
                "    component(key:'a1', class:A)\n" +
                "    component(key:'a2', class:A)\n" +
                "    component(key:'b1', class:B, parameters:[ new ComponentParameter('a1') ])\n" +
                "    component(key:'b2', class:B, parameters:[ new ComponentParameter('a2') ])\n" +
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

        assertSame(a1, b1.a);
        assertSame(a2, b2.a);
        assertNotSame(a1, a2);
        assertNotSame(b1, b2);
    }

    public void testShouldAcceptComponentParameter() throws PicoCompositionException {
        Reader script = new StringReader("" +
                "import org.picocontainer.parameters.ComponentParameter\n" +
                "import org.nanocontainer.testmodel.*\n" +
                "" +
                "nano = builder.container {\n" +
                "    component(class:A)\n" +
                "    component(key:B, class:B, parameters:[ new ComponentParameter(A) ])\n" +
                "}");

        PicoContainer pico = buildContainer(script, null, ASSEMBLY_SCOPE);
        A a = pico.getComponent(A.class);
        B b = pico.getComponent(B.class);

        assertNotNull(a);
        assertNotNull(b);
        assertSame(a, b.a);
    }

    public void testShouldAcceptComponentParameterWithClassNameKey() throws PicoCompositionException {
        Reader script = new StringReader("" +
                "import org.nanocontainer.testmodel.*\n" +
                "nano = builder.container {\n" +
                "    component(classNameKey:'org.nanocontainer.testmodel.A', class:A)\n" +
                "    component(key:B, class:B)\n" +
                "}");

        PicoContainer pico = buildContainer(script, null, ASSEMBLY_SCOPE);
        A a = pico.getComponent(A.class);
        B b = pico.getComponent(B.class);

        assertNotNull(a);
        assertNotNull(b);
        assertSame(a, b.a);
    }

    public void testShouldAcceptComponentParameterWithClassNameKeyAndParameter() throws PicoCompositionException {
        Reader script = new StringReader("" +
                "import org.picocontainer.parameters.ComponentParameter\n" +
                "import org.nanocontainer.testmodel.*\n" +
                "nano = builder.container {\n" +
                "    component(classNameKey:'org.nanocontainer.testmodel.A', class:A)\n" +
                "    component(key:B, class:B, parameters:[ new ComponentParameter(A) ])\n" +
                "}");

        PicoContainer pico = buildContainer(script, null, ASSEMBLY_SCOPE);
        A a = pico.getComponent(A.class);
        B b = pico.getComponent(B.class);

        assertNotNull(a);
        assertNotNull(b);
        assertSame(a, b.a);
    }

    public void testComponentParametersScript() {
        Reader script = new StringReader("" +
                "package org.nanocontainer.script.groovy\n" +
                "import org.picocontainer.parameters.ComponentParameter\n" +
                "import org.nanocontainer.testmodel.*\n" +
                "nano = builder.container {\n" +
                "    component(key:'a', class:A)\n" +
                "    component(key:'b', class:B, parameters:[ new ComponentParameter('a') ])\n" +
                "}");

        PicoContainer pico = buildContainer(script, null, ASSEMBLY_SCOPE);
        A a = (A) pico.getComponent("a");
        B b = (B) pico.getComponent("b");
        assertSame(a, b.a);
    }

    public void testShouldBeAbleToHandOffToNestedBuilder() {
        Reader script = new StringReader("" +
                "package org.nanocontainer.script.groovy\n" +
                "import org.nanocontainer.testmodel.*\n" +
                "builder.registerBuilder(name:'foo', class:'org.nanocontainer.script.groovy.TestingChildBuilder')\n" +
                "nano = builder.container {\n" +
                "    component(key:'a', class:A)\n" +
                "    foo {\n" +
                "      component(key:'b', class:B)\n" +
                "    }\n" +
                "}");

        PicoContainer pico = buildContainer(script, null, ASSEMBLY_SCOPE);
        Object a = pico.getComponent("a");
        Object b = pico.getComponent("b");

        assertNotNull(a);
        assertNotNull(b);
    }

    public void testShouldBeAbleToHandOffToNestedBuilderTheInlinedWay() {
        Reader script = new StringReader("" +
                "package org.nanocontainer.script.groovy\n" +
                "import org.nanocontainer.testmodel.*\n" +
                "nano = builder.container {\n" +
                "    component(key:'a', class:A)\n" +
                "    newBuilder(class:'org.nanocontainer.script.groovy.TestingChildBuilder') {\n" +
                "      component(key:'b', class:B)\n" +
                "    }\n" +
                "}");

        PicoContainer pico = buildContainer(script, null, ASSEMBLY_SCOPE);
        Object a = pico.getComponent("a");
        Object b = pico.getComponent("b");

        assertNotNull(a);
        assertNotNull(b);
    }


    public void testInstantiateBasicComponentInDeeperTree() {
        X.reset();
        Reader script = new StringReader("" +
                "package org.nanocontainer.script.groovy\n" +
                "import org.nanocontainer.testmodel.*\n" +
                "nano = builder.container {\n" +
                "    container() {\n" +
                "        component(A)\n" +
                "    }\n" +
                "}");

        MutablePicoContainer pico = (MutablePicoContainer) buildContainer(script, null, ASSEMBLY_SCOPE);
        pico.dispose();
        assertEquals("Should match the expression", "<A!A", X.componentRecorder);
    }

    public void testCustomComponentAdapterFactoryCanBeSpecified() {
        Reader script = new StringReader("" +
                "package org.nanocontainer.script.groovy\n" +
                "import org.nanocontainer.testmodel.*\n" +
                "nano = builder.container(componentFactory:assemblyScope) {\n" +
                "    component(A)\n" +
                "}");

        A a = new A();
        Mock componentFactoryMock = mock(ComponentFactory.class);
        componentFactoryMock.expects(once()).method("createComponentAdapter").with(new Constraint[] { isA(ComponentMonitor.class), isA(LifecycleStrategy.class), isA(
            ComponentCharacteristics.class), same(A.class), same(A.class), eq(null)}).will(returnValue(new InstanceAdapter(A.class, a, NullLifecycleStrategy.getInstance(),
                                                                        NullComponentMonitor.getInstance())));
        ComponentFactory componentFactory = (ComponentFactory) componentFactoryMock.proxy();
        PicoContainer pico = buildContainer(script, null, componentFactory);
        assertSame(a, pico.getComponent(A.class));
    }

    public void testCustomComponentMonitorCanBeSpecified() {
        Reader script = new StringReader("" +
                "package org.nanocontainer.script.groovy\n" +
                "import java.io.StringWriter\n" +
                "import org.picocontainer.monitors.WriterComponentMonitor\n" +
                "import org.nanocontainer.testmodel.*\n" +
                "writer = new StringWriter()\n" +
                "monitor = new WriterComponentMonitor(writer) \n"+
                "nano = builder.container(componentMonitor: monitor) {\n" +
                "    component(A)\n" +
                "    component(key:StringWriter, instance:writer)\n" +
                "}");

        PicoContainer pico = buildContainer(script, null, ASSEMBLY_SCOPE);
        pico.getComponent(WebServerConfig.class);
        StringWriter writer = pico.getComponent(StringWriter.class);
        String s = writer.toString();
        System.err.println("--> " + s);
        assertTrue(s.length() > 0);
    }

    public void testCustomComponentMonitorCanBeSpecifiedWhenCAFIsSpecified() {
        Reader script = new StringReader("" +
                "package org.nanocontainer.script.groovy\n" +
                "import java.io.StringWriter\n" +
                "import org.picocontainer.behaviors.CachingBehaviorFactory\n" +
                "import org.picocontainer.injectors.ConstructorInjectionFactory\n" +
                "import org.picocontainer.monitors.WriterComponentMonitor\n" +
                "import org.nanocontainer.testmodel.*\n" +
                "writer = new StringWriter()\n" +
                "monitor = new WriterComponentMonitor(writer) \n"+
                "nano = builder.container(componentFactory: new CachingBehaviorFactory().forThis(new ConstructorInjectionFactory()), componentMonitor: monitor) {\n" +
                "    component(A)\n" +
                "    component(key:StringWriter, instance:writer)\n" +
                "}");

        PicoContainer pico = buildContainer(script, null, ASSEMBLY_SCOPE);
        pico.getComponent(WebServerConfig.class);
        StringWriter writer = pico.getComponent(StringWriter.class);
        assertTrue(writer.toString().length() > 0);
    }

    public void testCustomComponentMonitorCanBeSpecifiedWhenParentIsSpecified() {
        DefaultNanoContainer parent = new DefaultNanoContainer();
        Reader script = new StringReader("" +
                "package org.nanocontainer.script.groovy\n" +
                "import java.io.StringWriter\n" +
                "import org.picocontainer.monitors.WriterComponentMonitor\n" +
                "import org.nanocontainer.testmodel.*\n" +
                "writer = new StringWriter()\n" +
                "monitor = new WriterComponentMonitor(writer) \n"+
                "nano = builder.container(parent:parent, componentMonitor: monitor) {\n" +
                "    component(A)\n" +
                "    component(key:StringWriter, instance:writer)\n" +
                "}");

        PicoContainer pico = buildContainer(script, parent, ASSEMBLY_SCOPE);
        pico.getComponent(WebServerConfig.class);
        StringWriter writer = pico.getComponent(StringWriter.class);
        assertTrue(writer.toString().length() > 0);
    }

    public void testCustomComponentMonitorCanBeSpecifiedWhenParentAndCAFAreSpecified() {
        DefaultNanoContainer parent = new DefaultNanoContainer();
        Reader script = new StringReader("" +
                "package org.nanocontainer.script.groovy\n" +
                "import java.io.StringWriter\n" +
                "import org.picocontainer.behaviors.CachingBehaviorFactory\n" +
                "import org.picocontainer.injectors.ConstructorInjectionFactory\n" +
                "import org.picocontainer.monitors.WriterComponentMonitor\n" +
                "import org.nanocontainer.testmodel.*\n" +
                "writer = new StringWriter()\n" +
                "monitor = new WriterComponentMonitor(writer) \n"+
                "nano = builder.container(parent:parent, componentFactory: new CachingBehaviorFactory().forThis(new ConstructorInjectionFactory()), componentMonitor: monitor) {\n" +
                "    component(A)\n" +
                "    component(key:StringWriter, instance:writer)\n" +
                "}");

        PicoContainer pico = buildContainer(script, parent, ASSEMBLY_SCOPE);
        pico.getComponent(WebServerConfig.class);
        StringWriter writer = pico.getComponent(StringWriter.class);
        assertTrue(writer.toString().length() > 0);
    }

    public void testInstantiateWithImpossibleComponentDependenciesConsideringTheHierarchy() {
        X.reset();
        Reader script = new StringReader("" +
                "package org.nanocontainer.script.groovy\n" +
                "import org.nanocontainer.testmodel.*\n" +
                "nano = builder.container {\n" +
                "    component(B)\n" +
                "    container() {\n" +
                "        component(A)\n" +
                "    }\n" +
                "    component(C)\n" +
                "}");

        try {
            buildContainer(script, null, ASSEMBLY_SCOPE);
            fail("Should not have been able to instansiate component tree due to visibility/parent reasons.");
        } catch (AbstractInjector.UnsatisfiableDependenciesException expected) {
        }
    }

    public void testInstantiateWithChildContainerAndStartStopAndDisposeOrderIsCorrect() {
        X.reset();
        Reader script = new StringReader("" +
                "package org.nanocontainer.script.groovy\n" +
                "import org.nanocontainer.testmodel.*\n" +
                "nano = builder.container {\n" +
                "    component(A)\n" +
                "    container() {\n" +
                "         component(B)\n" +
                "    }\n" +
                "    component(C)\n" +
                "}\n");

        // A and C have no no dependancies. B Depends on A.
        MutablePicoContainer pico = (MutablePicoContainer) buildContainer(script, null, ASSEMBLY_SCOPE);
        //pico.start();
        pico.stop();
        pico.dispose();

        assertEquals("Should match the expression", "<A<C<BB>C>A>!B!C!A", X.componentRecorder);
    }

    public void testBuildContainerWithParentAttribute() {
        DefaultNanoContainer parent = new DefaultNanoContainer();
        parent.addComponent("hello", "world");

        Reader script = new StringReader("" +
                "package org.nanocontainer.script.groovy\n" +
                "import org.nanocontainer.testmodel.*\n" +
                "nano = builder.container(parent:parent) {\n" +
                "    component(A)\n" +
                "}\n");

        PicoContainer pico = buildContainer(script, parent, ASSEMBLY_SCOPE);
        // Should be able to get instance that was registered in the parent container
        assertEquals("world", pico.getComponent("hello"));
    }


    public void testBuildContainerWithParentDependencyAndAssemblyScope() {
        DefaultNanoContainer parent = new DefaultNanoContainer();
        parent.addComponent("a", A.class);

        Reader script = new StringReader("" +
                "package org.nanocontainer.script.groovy\n" +
                "import org.nanocontainer.testmodel.*\n" +
                "nano = builder.container(parent:parent) {\n" +
                "  if ( assemblyScope instanceof SomeAssemblyScope ){\n "+
                "    component(B)\n" +
                "  }\n "+
                "}\n");

        PicoContainer pico = buildContainer(script, parent, new SomeAssemblyScope());
        assertNotNull(pico.getComponent(B.class));
    }

    public void testBuildContainerWithParentAndChildAssemblyScopes() throws IOException {
        String scriptValue = ("" +
                "package org.nanocontainer.script.groovy\n" +
                "import org.nanocontainer.testmodel.*\n" +
                "nano = builder.container(parent:parent) {\n" +
                "  System.out.println('assemblyScope:'+assemblyScope)\n " +
                "  if ( assemblyScope instanceof ParentAssemblyScope ){\n "+
                "    System.out.println('parent scope')\n " +
                "    component(A)\n" +
                "  } else if ( assemblyScope instanceof SomeAssemblyScope ){\n "+
                "    System.out.println('child scope')\n " +
                "    component(B)\n" +
                "  } else { \n" +
                "     throw new IllegalArgumentException('Invalid Scope: ' +  assemblyScope.getClass().getName())\n" +
                "      System.out.println('Invalid scope')\n " +
                "  } \n "+
                "}\n");

        Reader script = new StringReader(scriptValue);
        NanoContainer parent = new DefaultNanoContainer(
            buildContainer(script, null, new ParentAssemblyScope()));

        assertNotNull(parent.getComponentAdapter(A.class));

        script = new StringReader(scriptValue);
        PicoContainer pico = buildContainer(script, parent,  new SomeAssemblyScope());
        assertNotNull(pico.getComponent(B.class));
    }



    public void testBuildContainerWhenExpectedParentDependencyIsNotFound() {
        DefaultNanoContainer parent = new DefaultNanoContainer(new CachingBehaviorFactory().forThis(new AdaptiveInjectionFactory()));

        Reader script = new StringReader("" +
                "package org.nanocontainer.script.groovy\n" +
                "import org.nanocontainer.testmodel.*\n" +
                "nano = builder.container(parent:parent) {\n" +
                "  if ( assemblyScope instanceof SomeAssemblyScope ){\n "+
                "    component(B)\n" +
                "  }\n "+
                "}\n");

        try {
            buildContainer(script, parent, new SomeAssemblyScope());
            fail("UnsatisfiableDependenciesException expected");
        } catch (AbstractInjector.UnsatisfiableDependenciesException e) {
            // expected
        }
    }

    public void testBuildContainerWithParentAttributesPropagatesComponentAdapterFactory() {
        DefaultNanoContainer parent = new DefaultNanoContainer(new SetterInjectionFactory() );
        Reader script = new StringReader("" +
                "nano = builder.container(parent:parent) {\n" +
                "}\n");

        MutablePicoContainer pico = (MutablePicoContainer)buildContainer(script, parent, ASSEMBLY_SCOPE);
        // Should be able to get instance that was registered in the parent container
        ComponentAdapter componentAdapter = pico.addComponent(String.class).getComponentAdapter(String.class);
        assertTrue("ComponentAdapter should be originally defined by parent" , componentAdapter instanceof SetterInjector);
    }



    public void testExceptionThrownWhenParentAttributeDefinedWithinChild() {
        DefaultNanoContainer parent = new DefaultNanoContainer(new SetterInjectionFactory() );
        Reader script = new StringReader("" +
                "package org.nanocontainer.script.groovy\n" +
                "import org.nanocontainer.testmodel.*\n" +
                "nano = builder.container() {\n" +
                "    component(A)\n" +
                "    container(parent:parent) {\n" +
                "         component(B)\n" +
                "    }\n" +
                "}\n");

        try {
            buildContainer(script, parent, ASSEMBLY_SCOPE);
            fail("NanoContainerMarkupException should have been thrown.");
        } catch (NanoContainerMarkupException ignore) {
            // expected
        }
    }

    // TODO
    public void testSpuriousAttributes() {
        DefaultNanoContainer parent = new DefaultNanoContainer();

        Reader script = new StringReader("" +
                "package org.nanocontainer.script.groovy\n" +
                "import org.nanocontainer.testmodel.*\n" +
                "nano = builder.container(jim:'Jam', foo:'bar') {\n" +

                "}\n");
            try {
                buildContainer(script, parent, ASSEMBLY_SCOPE);
                //fail("Should throw exception upon spurious attributes?");
            } catch (NanoContainerMarkupException ex) {
                //ok?
            }
    }




    public void testWithDynamicClassPathThatDoesNotExist() {
        DefaultNanoContainer parent = new DefaultNanoContainer();
        try {
            Reader script = new StringReader("" +
                    "        child = null\n" +
                    "        pico = builder.container {\n" +
                    "            classPathElement(path:'this/path/does/not/exist.jar')\n" +
                    "            component(class:\"FooBar\") " +
                    "        }");

            buildContainer(script, parent, ASSEMBLY_SCOPE);
            fail("should have barfed with bad path exception");
        } catch (NanoContainerMarkupException e) {
            // excpected
        }

    }


    public void testWithDynamicClassPath() {
        DefaultNanoContainer parent = new DefaultNanoContainer(new CachingBehaviorFactory().forThis(new AdaptiveInjectionFactory()));
        Reader script = new StringReader(
                "        builder = new org.nanocontainer.script.groovy.GroovyNodeBuilder()\n"
              + "        File testCompJar = org.nanocontainer.TestHelper.getTestCompJarFile()\n"
              + "        compJarPath = testCompJar.getCanonicalPath()\n"
              + "        child = null\n"
              + "        pico = builder.container {\n"
              + "            classPathElement(path:compJarPath)\n"
              + "            component(class:\"TestComp\")\n"
              + "        }");

        MutablePicoContainer pico = (MutablePicoContainer) buildContainer(script, parent, ASSEMBLY_SCOPE);

        assertTrue(pico.getComponents().size() == 1);
        assertEquals("TestComp", pico.getComponents().get(0).getClass()
                .getName());
    }




    public void testWithDynamicClassPathWithPermissions() {
        DefaultNanoContainer parent = new DefaultNanoContainer(new CachingBehaviorFactory().forThis(new AdaptiveInjectionFactory()));
        Reader script = new StringReader(
                ""
                + "        builder = new org.nanocontainer.script.groovy.GroovyNodeBuilder()\n"
                        + "        File testCompJar = org.nanocontainer.TestHelper.getTestCompJarFile()\n"
                        + "        compJarPath = testCompJar.getCanonicalPath()\n"
                        + "        child = null\n"
                        + "        pico = builder.container {\n"
                        + "            classPathElement(path:compJarPath) {\n"
                        + "                grant(new java.net.SocketPermission('google.com','connect'))\n"
                        + "            }\n"
                        + "            component(class:\"TestComp\")\n"
                        + "        }" + "");

        MutablePicoContainer pico = (MutablePicoContainer)buildContainer(script, parent, ASSEMBLY_SCOPE);

        assertTrue(pico.getComponents().size() == 1);
        // can't actually test the permission under JUNIT control. We're just
        // testing the syntax here.
    }


    public void testGrantPermissionInWrongPlace() {
        DefaultNanoContainer parent = new DefaultNanoContainer(new CachingBehaviorFactory().forThis(new AdaptiveInjectionFactory()));
        try {
            Reader script = new StringReader("" +
                    "        builder = new org.nanocontainer.script.groovy.GroovyNodeBuilder()\n" +
                    "        File testCompJar = org.nanocontainer.TestHelper.getTestCompJarFile()\n" +
                    "        compJarPath = testCompJar.getCanonicalPath()\n" +
                    "        child = null\n" +
                    "        pico = builder.container {\n" +
                    "            grant(new java.net.SocketPermission('google.com','connect'))\n" +
                    "        }" +
                    "");

            buildContainer(script, parent, ASSEMBLY_SCOPE);
            fail("should barf with [Don't know how to create a 'grant' child] exception");
        } catch (NanoContainerMarkupException e) {
            assertTrue(e.getMessage().indexOf("Don't know how to create a 'grant' child") > -1);
        }

    }

    /**
     * Santity check to make sure testcomp doesn't exist in the testing classpath
     * otherwise all the tests that depend on the custom classpaths are suspect.
     */
    public void testTestCompIsNotAvailableViaSystemClassPath() {
        try {
            getClass().getClassLoader().loadClass("TestComp");
            fail("Invalid configuration TestComp exists in system classpath. ");
        } catch (ClassNotFoundException ex) {
            //ok.
        }

    }


    public void testWithParentClassPathPropagatesWithNoParentContainer()throws IOException {
        File testCompJar = TestHelper.getTestCompJarFile();

        URLClassLoader classLoader = new URLClassLoader(new URL[] {testCompJar.toURL()}, this.getClass().getClassLoader());
        Class testComp = null;

        try {
            testComp = classLoader.loadClass("TestComp");
        } catch (ClassNotFoundException ex) {
            fail("Unable to load test component from the jar using a url classloader");
        }
        Reader script = new StringReader(""
                + "pico = builder.container(parent:parent) {\n"
                + "         component(class:\"TestComp\")\n"
                + "}");

        PicoContainer pico = buildContainer(new GroovyContainerBuilder(script, classLoader), null, null);
        assertNotNull(pico);
        Object testCompInstance = pico.getComponent(testComp.getName());
        assertSame(testCompInstance.getClass(), testComp);

    }

    public void testValidationTurnedOnThrowsExceptionForUnknownAttributes() {
        DefaultNanoContainer parent = new DefaultNanoContainer();
        Reader script = new StringReader(
            "import org.nanocontainer.script.NullNodeBuilderDecorationDelegate\n" +
            "import org.nanocontainer.script.groovy.GroovyNodeBuilder\n" +
            "import org.nanocontainer.testmodel.*\n" +
            "builder = new GroovyNodeBuilder(new NullNodeBuilderDecorationDelegate(), GroovyNodeBuilder.PERFORM_ATTRIBUTE_VALIDATION)\n" +
            "nano = builder.container {\n" +
            "    component(key:'a', instance:'apple', badAttribute:'foo')\n" +
            "}");

        try {
            buildContainer(script, parent, ASSEMBLY_SCOPE);
            fail("GroovyNodeBuilder with validation should have thrown NanoContainerMarkupException");
        } catch(GroovyCompilationException ex) {
            //Weed out the groovy compilation exceptions
            throw ex;
        } catch (NanoContainerMarkupException ex) {
            //a-ok
        }
    }

    public void testValidationTurnedOffDoesntThrowExceptionForUnknownAttributes() {
        DefaultNanoContainer parent = new DefaultNanoContainer();
        Reader script = new StringReader(
            "import org.nanocontainer.script.NullNodeBuilderDecorationDelegate\n" +
            "import org.nanocontainer.script.groovy.GroovyNodeBuilder\n" +
            "import org.nanocontainer.testmodel.*\n" +
            "builder = new GroovyNodeBuilder(new NullNodeBuilderDecorationDelegate(), GroovyNodeBuilder.SKIP_ATTRIBUTE_VALIDATION)\n" +
            "nano = builder.container {\n" +
            "    component(key:'a', instance:'apple', badAttribute:'foo')\n" +
            "}");

        try {
            buildContainer(script, parent, ASSEMBLY_SCOPE);
            //a-ok
        } catch(GroovyCompilationException ex) {
            //Weed out the groovy compilation exceptions
            throw ex;
        } catch (NanoContainerMarkupException ex) {
            fail("GroovyNodeBuilder with validation turned off should never have thrown NanoContainerMarkupException: " + ex.getMessage());
        }

    }


    public void testComponentAdapterIsPotentiallyScriptable() throws PicoCompositionException {
        Reader script = new StringReader("" +
                "import org.nanocontainer.testmodel.*\n" +
                "X.reset()\n" +
                "nano = builder.container {\n" +
                "    ca = component(java.lang.Object).getComponentAdapter(java.lang.Object) \n" +
                "    component(instance:ca.getClass().getName())\n" +
                "}");

        PicoContainer pico = buildContainer(script, null, ASSEMBLY_SCOPE);
        // LifecyleContainerBuilder starts the container
        Object one = pico.getComponents().get(1);
        assertEquals("org.picocontainer.behaviors.CachingBehavior", one.toString());
    }


    private PicoContainer buildContainer(Reader script, PicoContainer parent, Object scope) {
        return buildContainer(new GroovyContainerBuilder(script, getClass().getClassLoader()), parent, scope);
    }










}
