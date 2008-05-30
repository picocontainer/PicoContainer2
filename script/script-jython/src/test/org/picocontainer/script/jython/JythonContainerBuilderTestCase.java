/*****************************************************************************
 * Copyright (C) PicoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the LICENSE.txt file.                                                     *
 *                                                                           *
 * Original code by                                                          *
 *****************************************************************************/
package org.picocontainer.script.jython;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.fail;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;

import org.junit.Test;
import org.picocontainer.PicoCompositionException;
import org.picocontainer.PicoBuilder;
import org.picocontainer.DefaultPicoContainer;
import org.picocontainer.PicoContainer;
import org.picocontainer.injectors.AbstractInjector;
import org.picocontainer.script.AbstractScriptedContainerBuilderTestCase;
import org.picocontainer.script.LifecycleMode;
import org.picocontainer.script.jython.JythonContainerBuilder;
import org.picocontainer.script.testmodel.A;
import org.picocontainer.script.testmodel.WebServer;
import org.picocontainer.script.testmodel.WebServerImpl;

/**
 * @author Aslak Helles&oslash;y
 */
public class JythonContainerBuilderTestCase extends AbstractScriptedContainerBuilderTestCase {

    @Test public void testSimpleConfigurationIsPossible() {
        Reader script = new StringReader("from org.picocontainer.script.testmodel import *\n" +
                "from org.picocontainer.script import *\n" +                
                "pico = DefaultScriptedPicoContainer()\n" +
                "pico.addComponent(WebServerImpl)\n" +
                "pico.addComponent(DefaultWebServerConfig)\n");

        PicoContainer pico = buildContainer(new JythonContainerBuilder(script, getClass().getClassLoader()), null, "SOME_SCOPE");
        assertNotNull(pico.getComponent(WebServer.class));
    }

    @Test public void testDependenciesAreUnsatisfiableByChildContainers() throws IOException, ClassNotFoundException, PicoCompositionException {
        try {
            Reader script = new StringReader("" +
                    "from org.picocontainer.script.testmodel import *\n" +
                    "from org.picocontainer.script import *\n" +                
                    "pico = DefaultScriptedPicoContainer()\n" +
                    "pico.addComponent(WebServerImpl)\n" +
                    "childContainer = DefaultScriptedPicoContainer(pico)\n" +
                    "childContainer.addComponent(DefaultWebServerConfig)\n");
            PicoContainer pico = buildContainer(new JythonContainerBuilder(script, getClass().getClassLoader()), null, "SOME_SCOPE");
            pico.getComponent(WebServer.class);
            fail("should have thrown unsatisifiable dependencies");
        } catch (AbstractInjector.UnsatisfiableDependenciesException expected) {
        	assertNotNull(expected.getMessage());
        }
    }

    @Test public void testDependenciesAreSatisfiableByParentContainer() throws IOException, ClassNotFoundException, PicoCompositionException {
        Reader script = new StringReader("" +
                "from org.picocontainer.script.testmodel import *\n" +
                "from org.picocontainer.script import *\n" +                
                "from org.picocontainer import Parameter\n"+
                "pico = DefaultScriptedPicoContainer()\n" +
                "pico.addComponent(DefaultWebServerConfig)\n" +
                "child = pico.makeChildContainer()\n" +
                "child.addComponent(WebServerImpl)\n" +
                "pico.addComponent('wayOfPassingSomethingToTestEnv', child.getComponent(WebServerImpl), Parameter.DEFAULT)");
        PicoContainer pico = buildContainer(new JythonContainerBuilder(script, getClass().getClassLoader()), null, "SOME_SCOPE");
        WebServerImpl wsi = (WebServerImpl) pico.getComponent("wayOfPassingSomethingToTestEnv");
        assertNotNull(wsi);
    }

    @Test public void testContainerCanBeBuiltWithParent() {
        Reader script = new StringReader("" +
                "pico = ScriptedBuilder(parent).withLifecycle().build()\n");
        PicoContainer parent = new DefaultPicoContainer();
        PicoContainer pico = buildContainer(new JythonContainerBuilder(script, getClass().getClassLoader()), parent, "SOME_SCOPE");
        //pico.getParent() is now ImmutablePicoContainer
        assertNotSame(parent, pico.getParent());
    }
    
	@Test public void testAutoStartingContainerBuilderStarts() {
        A.reset();
        Reader script = new StringReader("" +
                "from org.picocontainer.script.testmodel import *\n" +
                "from org.picocontainer.script import *\n" +                
                "pico = parent.makeChildContainer() \n" +
                "pico.addComponent(A)\n" +
                "");
        PicoContainer parent = new PicoBuilder().withLifecycle().withCaching().build();
        PicoContainer pico = buildContainer(new JythonContainerBuilder(script, getClass().getClassLoader()), parent, "SOME_SCOPE");
        //PicoContainer.getParent() is now ImmutablePicoContainer
        assertNotSame(parent, pico.getParent());
        assertEquals("<A",A.componentRecorder);		
        A.reset();
	}
	
	@Test public void testNonAutoStartingContainerBuildDoesntAutostart() {
        A.reset();
        Reader script = new StringReader("" +
                "from org.picocontainer.script.testmodel import *\n" +
                "from org.picocontainer.script import *\n" +                
                "pico = parent.makeChildContainer() \n" +
                "pico.addComponent(A)\n" +
                "");
        PicoContainer parent = new PicoBuilder().withLifecycle().withCaching().build();
        JythonContainerBuilder containerBuilder = new JythonContainerBuilder(script, getClass().getClassLoader(), LifecycleMode.NO_LIFECYCLE);
        PicoContainer pico = buildContainer(containerBuilder, parent, "SOME_SCOPE");
        //PicoContainer.getParent() is now ImmutablePicoContainer
        assertNotSame(parent, pico.getParent());
        assertEquals("",A.componentRecorder);
        A.reset();
    }
	
    

}
