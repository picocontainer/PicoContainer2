/*******************************************************************************
 * Copyright (C) PicoContainer Organization. All rights reserved.
 * ---------------------------------------------------------------------------
 * The software in this package is published under the terms of the BSD style
 * license a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 ******************************************************************************/
package org.picocontainer.script;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.File;
import java.net.MalformedURLException;

import org.junit.Test;
import org.picocontainer.PicoClassNotFoundException;
import org.picocontainer.PicoCompositionException;
import org.picocontainer.PicoException;
import org.picocontainer.script.ClassName;
import org.picocontainer.script.DefaultScriptedPicoContainer;
import org.picocontainer.script.ScriptedPicoContainer;
import org.picocontainer.script.testmodel.WebServerImpl;

/**
 * @author Paul Hammant
 */
public class ClassNameDefaultScriptedPicoContainerTestCase {

    @Test
    public void testBasic() throws PicoCompositionException {
        ScriptedPicoContainer container = new DefaultScriptedPicoContainer();
        container.addComponent(new ClassName("org.picocontainer.script.testmodel.DefaultWebServerConfig"));
        container.addComponent("org.picocontainer.script.testmodel.WebServer", new ClassName(
                "org.picocontainer.script.testmodel.WebServerImpl"));
    }

    @Test
    public void testProvision() throws PicoException {
        ScriptedPicoContainer container = new DefaultScriptedPicoContainer();
        container.addComponent(new ClassName("org.picocontainer.script.testmodel.DefaultWebServerConfig"));
        container.addComponent(new ClassName("org.picocontainer.script.testmodel.WebServerImpl"));

        assertNotNull("WebServerImpl should exist", container.getComponent(WebServerImpl.class));
        assertTrue("WebServerImpl should exist", container.getComponent(WebServerImpl.class) != null);
    }

    @Test
    public void testNoGenerationRegistration() throws PicoCompositionException {
        ScriptedPicoContainer container = new DefaultScriptedPicoContainer();
        try {
            container.addComponent(new ClassName("Ping"));
            fail("should have failed");
        } catch (PicoClassNotFoundException e) {
            // expected
        }
    }

    @Test
    public void testThatTestCompIsNotNaturallyInTheClassPathForTesting() {
        // the following tests try to load the jar containing TestComp - it
        // won't do to have the class already available in the classpath
        DefaultScriptedPicoContainer dfca = new DefaultScriptedPicoContainer();
        try {
            dfca.addComponent("foo", new ClassName("TestComp"));
            Object o = dfca.getComponent("foo");
            fail("Should have failed. Class was loaded from "
                    + o.getClass().getProtectionDomain().getCodeSource().getLocation());
        } catch (PicoClassNotFoundException expected) {
        }

    }

    @Test
    public void testChildContainerAdapterCanRelyOnParentContainerAdapter() throws MalformedURLException {

        File testCompJar = TestHelper.getTestCompJarFile();

        // Set up parent
        ScriptedPicoContainer parentContainer = new DefaultScriptedPicoContainer();
        parentContainer.addClassLoaderURL(testCompJar.toURL());
        parentContainer.addComponent("parentTestComp", new ClassName("TestComp"));
        parentContainer.addComponent(new ClassName("java.lang.StringBuffer"));

        Object parentTestComp = parentContainer.getComponent("parentTestComp");
        assertEquals("TestComp", parentTestComp.getClass().getName());

        // Set up child
        ScriptedPicoContainer childContainer = (ScriptedPicoContainer) parentContainer.makeChildContainer();
        File testCompJar2 = new File(testCompJar.getParentFile(), "TestComp2.jar");
        // System.err.println("--> " + testCompJar2.getAbsolutePath());
        childContainer.addClassLoaderURL(testCompJar2.toURL());
        childContainer.addComponent("childTestComp", new ClassName("TestComp2"));

        Object childTestComp = childContainer.getComponent("childTestComp");

        assertEquals("TestComp2", childTestComp.getClass().getName());

        assertNotSame(parentTestComp, childTestComp);

        final ClassLoader parentCompClassLoader = parentTestComp.getClass().getClassLoader();
        final ClassLoader childCompClassLoader = childTestComp.getClass().getClassLoader();
        if (parentCompClassLoader != childCompClassLoader.getParent()) {
            printClassLoader(parentCompClassLoader);
            printClassLoader(childCompClassLoader);
            fail("parentTestComp classloader should be parent of childTestComp classloader");
        }
        // PicoContainer.getParent() is now ImmutablePicoContainer
        assertNotSame(parentContainer, childContainer.getParent());
    }

    private void printClassLoader(ClassLoader classLoader) {
        while (classLoader != null) {
            System.out.println(classLoader);
            classLoader = classLoader.getParent();
        }
        System.out.println("--");
    }

    public static class AnotherFooComp {

    }

    @Test
    public void testClassLoaderJugglingIsPossible() throws MalformedURLException {
        ScriptedPicoContainer parentContainer = new DefaultScriptedPicoContainer();

        File testCompJar = TestHelper.getTestCompJarFile();

        parentContainer.addComponent("foo", new ClassName("org.picocontainer.script.testmodel.DefaultWebServerConfig"));

        Object fooWebServerConfig = parentContainer.getComponent("foo");
        assertEquals("org.picocontainer.script.testmodel.DefaultWebServerConfig", fooWebServerConfig.getClass()
                .getName());

        ScriptedPicoContainer childContainer = new DefaultScriptedPicoContainer(parentContainer);
        childContainer.addClassLoaderURL(testCompJar.toURL());
        childContainer.addComponent("bar", new ClassName("TestComp"));

        Object barTestComp = childContainer.getComponent("bar");
        assertEquals("TestComp", barTestComp.getClass().getName());

        assertNotSame(fooWebServerConfig.getClass().getClassLoader(), barTestComp.getClass().getClassLoader());

        // This kludge is needed because IDEA, Eclipse and Maven have different
        // numbers of
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

    //TODO @Test
    public void testSecurityManagerCanPreventOperations() throws MalformedURLException {
        ScriptedPicoContainer parentContainer = new DefaultScriptedPicoContainer();

        String testcompJarFileName = System.getProperty("testcomp.jar");
        assertNotNull("The testcomp.jar system property does not exist", testcompJarFileName);
        File testCompJar = new File(testcompJarFileName);
        assertTrue(testCompJar.isFile());

        parentContainer.addComponent("foo", new ClassName("org.picocontainer.script.testmodel.DefaultWebServerConfig"));

        Object fooWebServerConfig = parentContainer.getComponent("foo");
        assertEquals("org.picocontainer.script.testmodel.DefaultWebServerConfig", fooWebServerConfig.getClass()
                .getName());

        ScriptedPicoContainer childContainer = new DefaultScriptedPicoContainer(parentContainer);
        childContainer.addClassLoaderURL(testCompJar.toURL());
        // TODO childContainer.setPermission(some permission list, that includes
        // the preventing of general file access);
        // Or shoud this be done in the ctor for DRCA ?
        // or should it a parameter in the addClassLoaderURL(..) method
        childContainer.addComponent("bar", new ClassName("org.picocontainer.script.testmodel.FileSystemUsing"));

        try {
            parentContainer.getComponent("bar");
            fail("Should have barfed");
        } catch (java.security.AccessControlException e) {
            // expected
        }
    }

}
