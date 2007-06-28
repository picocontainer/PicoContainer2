/*****************************************************************************
 * Copyright (C) NanoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the LICENSE.txt file.                                                     *
 *                                                                           *
 * Original code by                                                          *
 *****************************************************************************/
package org.nanocontainer.nanowar;

import java.io.StringReader;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.jmock.Mock;
import org.jmock.MockObjectTestCase;
import org.nanocontainer.script.ScriptedContainerBuilder;
import org.nanocontainer.script.ScriptedContainerBuilderFactory;
import org.nanocontainer.script.groovy.GroovyContainerBuilder;
import org.nanocontainer.script.xml.XMLContainerBuilder;
import org.picocontainer.MutablePicoContainer;
import org.picocontainer.PicoContainer;
import org.picocontainer.DefaultPicoContainer;
import org.picocontainer.ObjectReference;
import org.picocontainer.behaviors.CachingBehavior;

/**
 * @author Mauro Talevi
 * @author Konstantin Pribluda ( konstantin.pribluda[at]infodesire.com )
 */
public class ScopedContainerComposerTestCase extends MockObjectTestCase {

    public void testCompositionWithInvalidScope() {
        ScopedContainerComposer composer = new ScopedContainerComposer();
        MutablePicoContainer applicationContainer = new DefaultPicoContainer();
        composer.composeContainer(applicationContainer, "invalid-scope");
        assertNull(applicationContainer.getComponent("applicationScopedInstance"));
    }
    
    public void testComposedHierarchyWithDefaultConfiguration() {
        assertComposedHierarchy(new ScopedContainerComposer());
    }
    
    public void testComposedHierarchyWithCustomMXLConfiguration() {
        String xmlConfig = 
            "<container>" +
            "<component-implementation class='org.nanocontainer.nanowar.ScopedContainerConfigurator'>"+
            "      <parameter><string>org.nanocontainer.script.xml.XMLContainerBuilder</string></parameter>"+
            "      <parameter><string>nanowar-application.xml</string></parameter> "+
            "      <parameter><string>nanowar-session.xml</string></parameter>        "+
            "      <parameter><string>nanowar-request.xml</string></parameter> "+
            "</component-implementation>" +
            "</container>";
        assertComposedHierarchy(new ScopedContainerComposer(createConfigurationContainer(xmlConfig, XMLContainerBuilder.class)));
    }
    
    //requires GroovyContainerBuilder to implement ContainerPopulator
    public void TODO_testComposedHierarchyWithCustomGroovyConfiguration() {
        String groovyConfig =
            "pico = builder.container(parent:parent, scope:assemblyScope) {\n" +
            "   addComponent(class:'org.nanocontainer.nanowar.ScopedContainerConfigurator', \n"+
            "             parameters:['org.nanocontainer.script.groovy.GroovyContainerBuilder', " +
            "                         'nanowar-application.groovy', " +
            "                         'nanowar-session.groovy', " +
            "                         'nanowar-request.groovy' ])\n" +
            "}";
        assertComposedHierarchy(new ScopedContainerComposer(createConfigurationContainer(groovyConfig, GroovyContainerBuilder.class)));        
    }
    
    private void assertComposedHierarchy(ScopedContainerComposer composer) {
        MutablePicoContainer applicationContainer = new DefaultPicoContainer();
        Mock servletContextMock = mock(ServletContext.class);

        composer.composeContainer(applicationContainer, servletContextMock.proxy());
        assertNotNull(applicationContainer.getComponent("applicationScopedInstance"));
        assertNotNull(applicationContainer.getComponent("testFoo"));

        MutablePicoContainer sessionContainer = new DefaultPicoContainer(applicationContainer);
        Mock httpSessionMock = mock(HttpSession.class);
        composer.composeContainer(sessionContainer, httpSessionMock.proxy());
        assertNotNull(sessionContainer.getComponent("applicationScopedInstance"));
        assertNotNull(sessionContainer.getComponent("sessionScopedInstance"));

        MutablePicoContainer requestContainer = new DefaultPicoContainer(sessionContainer);
        Mock httpRequestMock = mock(HttpServletRequest.class);
        composer.composeContainer(requestContainer, httpRequestMock.proxy());
        assertNotNull(requestContainer.getComponent("applicationScopedInstance"));
        assertNotNull(requestContainer.getComponent("sessionScopedInstance"));
        assertNotNull(requestContainer.getComponent("requestScopedInstance"));
        assertNotNull(requestContainer.getComponent("testFooHierarchy"));
    }
    

    private PicoContainer createConfigurationContainer(String script, Class containerBuilder) {
        ScriptedContainerBuilderFactory scriptedContainerBuilderFactory = new ScriptedContainerBuilderFactory(
                new StringReader(script), containerBuilder.getName(), Thread.currentThread().getContextClassLoader());
        return buildContainer(scriptedContainerBuilderFactory.getContainerBuilder());        
    }
    
    private PicoContainer buildContainer(ScriptedContainerBuilder builder) {
        ObjectReference containerRef = new CachingBehavior.SimpleReference();
        builder.buildContainer(containerRef, new CachingBehavior.SimpleReference(), new CachingBehavior.SimpleReference(), false);
        return (PicoContainer) containerRef.get();
    }
        
}