/*****************************************************************************
 * Copyright (C) NanoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the LICENSE.txt file.                                                     *
 *                                                                           *
 * Original code by                                                          *
 *****************************************************************************/

/**
 * @author Aslak Helles&oslash;y
 * @author Michael Rimov
 * @version $Revision$
 */
package org.nanocontainer.script.bsh;

import java.io.File;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.net.URL;
import java.net.URLClassLoader;
import java.net.MalformedURLException;
import java.util.Map;

import org.nanocontainer.script.AbstractScriptedContainerBuilderTestCase;
import org.nanocontainer.TestHelper;
import org.picocontainer.PicoContainer;
import org.picocontainer.DefaultPicoContainer;
import org.picocontainer.containers.ImmutablePicoContainer;

/**
 * @author Aslak Helles&oslash;y
 * @version $Revision$
 */
public class BeanShellContainerBuilderTestCase extends AbstractScriptedContainerBuilderTestCase {


    public void testContainerCanBeBuiltWithParent() {
        Reader script = new StringReader("" +
                "java.util.Map m = new java.util.HashMap();\n" +
                "m.put(\"foo\",\"bar\");" +
                "pico = new org.nanocontainer.DefaultNanoContainer(parent);\n" +
                "pico.addComponent((Object) \"hello\", m, new org.picocontainer.Parameter[0]);\n");
        PicoContainer parent = new DefaultPicoContainer();
        parent = new ImmutablePicoContainer(parent);
        BeanShellContainerBuilder beanShellContainerBuilder = new BeanShellContainerBuilder(script, getClass().getClassLoader());
        PicoContainer pico = buildContainer(beanShellContainerBuilder, parent, "SOME_SCOPE");
        //PicoContainer.getParent() is now ImmutablePicoContainer
        assertNotSame(parent, pico.getParent());
        Object o = pico.getComponent("hello");
        assertTrue(o instanceof Map);
        assertEquals("bar", ((Map) o).get("foo"));

    }

    /**
     * The following test requires the next beta of beanshell to work.
     * @todo Get this working again ! - PH
     * can run.
     * @throws IOException
     * @throws MalformedURLException
     */
    public void doNot_testWithParentClassPathPropagatesWithToBeanShellInterpreter() throws MalformedURLException {
        Reader script = new StringReader("" +
            "try {\n" +
            "    getClass(\"TestComp\");\n" +
            "} catch (ClassNotFoundException ex) {\n" +
            "     ClassLoader current = this.getClass().getClassLoader(); \n" +
            "     print(current.toString());\n" +
            "     print(current.getParent().toString());\n" +
            "     print(\"Failed ClassLoading: \");\n" +
            "     ex.printStackTrace();\n" +
            "}\n" +
            "Class clazz = getClass(\"TestComp\");\n" +
            "print(clazz); \n" +
            "ClassLoader cl = clazz.getClassLoader();" +
            "pico = new org.nanocontainer.DefaultNanoContainer(cl, parent);\n" +
            "pico.addAdapter( \"TestComp\" );\n");

        

        File testCompJar = TestHelper.getTestCompJarFile();
        System.err.println("--> " + testCompJar.getAbsolutePath());
        URLClassLoader classLoader = new URLClassLoader(new URL[] {testCompJar.toURL()}, this.getClass().getClassLoader());
        Class testComp = null;
        PicoContainer parent = new DefaultPicoContainer();

        try {
            testComp = classLoader.loadClass("TestComp");
        } catch (ClassNotFoundException ex) {
            ex.printStackTrace();
            fail("Unable to load test component from the jar using a url classloader");
        }

        PicoContainer pico = buildContainer(new BeanShellContainerBuilder(script, classLoader), parent, "SOME_SCOPE");
        assertNotNull(pico);
        Object testCompInstance = pico.getComponent(testComp);
        assertNotNull(testCompInstance);
        assertEquals(testCompInstance.getClass().getName(), testComp.getName());

    }

}
