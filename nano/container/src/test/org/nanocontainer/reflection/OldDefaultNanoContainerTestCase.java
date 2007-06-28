/*****************************************************************************
 * Copyright (C) NanoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the LICENSE.txt file.                                                     *
 *                                                                           *
 * Original code by Aslak Hellesoy and Paul Hammant                          *
 *****************************************************************************/

package org.nanocontainer.reflection;

import junit.framework.TestCase;
import org.nanocontainer.NanoContainer;
import org.nanocontainer.ClassName;
import org.nanocontainer.DefaultNanoContainer;
import org.nanocontainer.TestHelper;
import org.picocontainer.PicoClassNotFoundException;
import org.nanocontainer.testmodel.WebServerImpl;
import org.picocontainer.*;

import java.io.File;
import java.net.MalformedURLException;

public class OldDefaultNanoContainerTestCase extends TestCase {

    public void testBasic() throws PicoCompositionException {
        NanoContainer nanoContainer = new DefaultNanoContainer();
        nanoContainer.addComponent(new ClassName("org.nanocontainer.testmodel.DefaultWebServerConfig"));
        nanoContainer.addComponent("org.nanocontainer.testmodel.WebServer", new ClassName("org.nanocontainer.testmodel.WebServerImpl"));
    }

    public void testProvision() throws PicoException {
        NanoContainer nanoContainer = new DefaultNanoContainer();
        nanoContainer.addComponent(new ClassName("org.nanocontainer.testmodel.DefaultWebServerConfig"));
        nanoContainer.addComponent(new ClassName("org.nanocontainer.testmodel.WebServerImpl"));

        assertNotNull("WebServerImpl should exist", nanoContainer.getComponent(WebServerImpl.class));
        assertTrue("WebServerImpl should exist", nanoContainer.getComponent(WebServerImpl.class) != null);
    }

    public void testNoGenerationRegistration() throws PicoCompositionException {
        NanoContainer nanoContainer = new DefaultNanoContainer();
        try {
            nanoContainer.addComponent(new ClassName("Ping"));
            fail("should have failed");
        } catch (PicoClassNotFoundException e) {
            // expected
        }
    }

    public void testThatTestCompIsNotNaturallyInTheClassPathForTesting() {

        // the following tests try to load the jar containing TestComp - it
        // won't do to have the class already available in the classpath

        DefaultNanoContainer dfca = new DefaultNanoContainer();
        try {
            dfca.addComponent("foo", new ClassName("TestComp"));
            Object o = dfca.getComponent("foo");
            fail("Should have failed. Class was loaded from " + o.getClass().getProtectionDomain().getCodeSource().getLocation());
        } catch (PicoClassNotFoundException expected) {
        }

    }

    public void testChildContainerAdapterCanRelyOnParentContainerAdapter() throws MalformedURLException {

        File testCompJar = TestHelper.getTestCompJarFile();

        // Set up parent
        NanoContainer parentContainer = new DefaultNanoContainer();
        parentContainer.addClassLoaderURL(testCompJar.toURL());
        parentContainer.addComponent("parentTestComp", new ClassName("TestComp"));
        parentContainer.addComponent(new ClassName("java.lang.StringBuffer"));

        Object parentTestComp = parentContainer.getComponent("parentTestComp");
        assertEquals("TestComp", parentTestComp.getClass().getName());

        // Set up child
        NanoContainer childContainer = (NanoContainer) parentContainer.makeChildContainer();
        File testCompJar2 = new File(testCompJar.getParentFile(), "TestComp2.jar");
        //System.err.println("--> " + testCompJar2.getAbsolutePath());
        childContainer.addClassLoaderURL(testCompJar2.toURL());
        childContainer.addComponent("childTestComp", new ClassName("TestComp2"));

        Object childTestComp = childContainer.getComponent("childTestComp");

        assertEquals("TestComp2", childTestComp.getClass().getName());

        assertNotSame(parentTestComp, childTestComp);

        final ClassLoader parentCompClassLoader = parentTestComp.getClass().getClassLoader();
        final ClassLoader childCompClassLoader = childTestComp.getClass().getClassLoader();
        if(parentCompClassLoader != childCompClassLoader.getParent()) {
            printClassLoader(parentCompClassLoader);
            printClassLoader(childCompClassLoader);
            fail("parentTestComp classloader should be parent of childTestComp classloader");
        }
        //PicoContainer.getParent() is now ImmutablePicoContainer
        assertNotSame(parentContainer, childContainer.getParent());
    }

    private void printClassLoader(ClassLoader classLoader) {
        while(classLoader != null) {
            System.out.println(classLoader);
            classLoader = classLoader.getParent();
        }
        System.out.println("--");
    }

    public static class AnotherFooComp {

    }

    public void testClassLoaderJugglingIsPossible() throws MalformedURLException {
        NanoContainer parentContainer = new DefaultNanoContainer();


        File testCompJar = TestHelper.getTestCompJarFile();

        parentContainer.addComponent("foo", new ClassName("org.nanocontainer.testmodel.DefaultWebServerConfig"));

        Object fooWebServerConfig = parentContainer.getComponent("foo");
        assertEquals("org.nanocontainer.testmodel.DefaultWebServerConfig", fooWebServerConfig.getClass().getName());

        NanoContainer childContainer = new DefaultNanoContainer(parentContainer);
        childContainer.addClassLoaderURL(testCompJar.toURL());
        childContainer.addComponent("bar", new ClassName("TestComp"));

        Object barTestComp = childContainer.getComponent("bar");
        assertEquals("TestComp", barTestComp.getClass().getName());

        assertNotSame(fooWebServerConfig.getClass().getClassLoader(), barTestComp.getClass().getClassLoader());

        // This kludge is needed because IDEA, Eclipse and Maven have different numbers of
        // classloaders in their hierachies for junit invocation.
        ClassLoader fooCL = fooWebServerConfig.getClass().getClassLoader();
        ClassLoader barCL1 = barTestComp.getClass().getClassLoader().getParent();
        ClassLoader barCL2, barCL3;
        if (barCL1 != null && barCL1 != fooCL) {
            barCL2 = barCL1.getParent();
            if (barCL2 != null && barCL2 != fooCL) {
                barCL3 = barCL2.getParent();
                if (barCL3 != null && barCL3 != fooCL) {
                    fail("One of the parent classloaders of TestComp, should be that of DefaultWebServerConfig");
                }
            }
        }
    }

    public void TODO_testSecurityManagerCanPreventOperations() throws MalformedURLException {
        NanoContainer parentContainer = new DefaultNanoContainer();

        String testcompJarFileName = System.getProperty("testcomp.jar");
        // Paul's path to TestComp. PLEASE do not take out.
        //testcompJarFileName = "D:/OSS/PN/java/nanocontainer/src/test-comp/TestComp.jar";
        assertNotNull("The testcomp.jar system property should point to nano/reflection/src/test-comp/TestComp.jar", testcompJarFileName);
        File testCompJar = new File(testcompJarFileName);
        assertTrue(testCompJar.isFile());

        parentContainer.addComponent("foo", new ClassName("org.nanocontainer.testmodel.DefaultWebServerConfig"));

        Object fooWebServerConfig = parentContainer.getComponent("foo");
        assertEquals("org.nanocontainer.testmodel.DefaultWebServerConfig", fooWebServerConfig.getClass().getName());

        NanoContainer childContainer = new DefaultNanoContainer(parentContainer);
        childContainer.addClassLoaderURL(testCompJar.toURL());
        //TODO childContainer.setPermission(some permission list, that includes the preventing of general file access);
        // Or shoud this be done in the ctor for DRCA ?
        // or should it a parameter in the addClassLoaderURL(..) method
        childContainer.addComponent("bar", new ClassName("org.nanocontainer.testmodel.FileSystemUsing"));

        try {
            parentContainer.getComponent("bar");
            fail("Should have barfed");
        } catch (java.security.AccessControlException e) {
            // expected
        }
    }


}
