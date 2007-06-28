/*****************************************************************************
 * Copyright (C) NanoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the LICENSE.txt file.                                                     *
 *                                                                           *
 * Original code by                                                          *
 *****************************************************************************/
package org.nanocontainer.script.jython;

import org.nanocontainer.integrationkit.PicoCompositionException;
import org.nanocontainer.script.AbstractScriptedContainerBuilderTestCase;
import org.nanocontainer.testmodel.WebServer;
import org.nanocontainer.testmodel.WebServerImpl;
import org.picocontainer.PicoContainer;
import org.picocontainer.injectors.AbstractInjector;
import org.picocontainer.DefaultPicoContainer;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;

/**
 * @author Aslak Helles&oslash;y
 * @version $Revision$
 */
public class JythonContainerBuilderTestCase extends AbstractScriptedContainerBuilderTestCase {

    public void testSimpleConfigurationIsPossible() {
        Reader script = new StringReader("from org.nanocontainer.testmodel import *\n" +
                "pico = DefaultNanoContainer()\n" +
                "pico.addAdapter(WebServerImpl)\n" +
                "pico.addAdapter(DefaultWebServerConfig)\n");

        PicoContainer pico = buildContainer(new JythonContainerBuilder(script, getClass().getClassLoader()), null, "SOME_SCOPE");
        assertNotNull(pico.getComponent(WebServer.class));
    }

    public void testDependenciesAreUnsatisfiableByChildContainers() throws IOException, ClassNotFoundException, PicoCompositionException {
        try {
            Reader script = new StringReader("" +
                    "from org.nanocontainer.testmodel import *\n" +
                    "pico = DefaultNanoContainer()\n" +
                    "pico.addAdapter(WebServerImpl)\n" +
                    "childContainer = DefaultNanoContainer(pico)\n" +
                    "childContainer.addAdapter(DefaultWebServerConfig)\n");
            PicoContainer pico = buildContainer(new JythonContainerBuilder(script, getClass().getClassLoader()), null, "SOME_SCOPE");
            pico.getComponent(WebServer.class);
            fail();
        } catch (AbstractInjector.UnsatisfiableDependenciesException expected) {
        }
    }

    public void testDependenciesAreSatisfiableByParentContainer() throws IOException, ClassNotFoundException, PicoCompositionException {
        Reader script = new StringReader("" +
                "from org.nanocontainer.testmodel import *\n" +
                "pico = DefaultNanoContainer()\n" +
                "pico.addAdapter(DefaultWebServerConfig)\n" +
                "child = pico.makeChildContainer()\n" +
                "child.addAdapter(WebServerImpl)\n" +
                "pico.registerComponentInstance('wayOfPassingSomethingToTestEnv', child.getComponentInstance(WebServerImpl))");
        PicoContainer pico = buildContainer(new JythonContainerBuilder(script, getClass().getClassLoader()), null, "SOME_SCOPE");
        WebServerImpl wsi = (WebServerImpl) pico.getComponent("wayOfPassingSomethingToTestEnv");
        assertNotNull(wsi);
    }

    public void testContainerCanBeBuiltWithParent() {
        Reader script = new StringReader("" +
                "pico = DefaultNanoContainer(parent)\n");
        PicoContainer parent = new DefaultPicoContainer();
        PicoContainer pico = buildContainer(new JythonContainerBuilder(script, getClass().getClassLoader()), parent, "SOME_SCOPE");
        //pico.getParent() is now ImmutablePicoContainer
        assertNotSame(parent, pico.getParent());
    }

}
