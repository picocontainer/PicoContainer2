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



import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.picocontainer.tck.MockFactory.mockeryWithCountingNamingScheme;

import java.io.ByteArrayInputStream;
import java.io.StringReader;
import java.util.Vector;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionBindingListener;
import javax.servlet.http.HttpSessionEvent;

import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JMock;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.nanocontainer.integrationkit.ContainerBuilder;
import org.nanocontainer.integrationkit.DefaultContainerBuilder;
import org.nanocontainer.script.NanoContainerMarkupException;
import org.nanocontainer.script.ScriptedContainerBuilderFactory;
import org.nanocontainer.script.groovy.GroovyContainerBuilder;
import org.nanocontainer.script.xml.XMLContainerBuilder;
import org.picocontainer.DefaultPicoContainer;
import org.picocontainer.MutablePicoContainer;
import org.picocontainer.ObjectReference;
import org.picocontainer.PicoCompositionException;
import org.picocontainer.PicoContainer;
import org.picocontainer.references.SimpleReference;

/**
 * @author Aslak Helles&oslash;y
 * @author Mauro Talevi
 */
@RunWith(JMock.class)
public final class ServletContainerListenerTestCase implements KeyConstants {

	private Mockery mockery = mockeryWithCountingNamingScheme();
	
    private ServletContainerListener listener = new ServletContainerListener();

    private final String groovyScript =
        "pico = builder.container(parent:parent, scope:assemblyScope) {\n" +
        "   component(key:'string', instance:'A String')\n" +
        "}";
    
    private final String xmlScript =
            "<container>" +
            "<component-instance key='string'>" +
            "      <string>A String</string>" +
            "</component-instance>" +
            "</container>"; 
    
    @Test public void testApplicationScopeContainerIsCreatedWithInlinedScripts() {
        assertApplicationScopeContainerIsCreatedWithInlinedScript("nanocontainer.groovy", groovyScript, GroovyContainerBuilder.class);
        assertApplicationScopeContainerIsCreatedWithInlinedScript("nanocontainer.xml", xmlScript, XMLContainerBuilder.class);
    }

    private void assertApplicationScopeContainerIsCreatedWithInlinedScript(final String scriptName, final String script, 
            final Class<?> containerBuilder) {
    	final ServletContext servletContext = mockery.mock(ServletContext.class);
        final Vector<String> initParams = new Vector<String>();
        initParams.add(scriptName);
    	mockery.checking(new Expectations(){{
    		one(servletContext).getInitParameterNames();
    		will(returnValue(initParams.elements()));
    		one(servletContext).getInitParameter(with(equal(SYSTEM_PROPERTIES_CONTAINER)));
    		will(returnValue(null));
    		one(servletContext).getInitParameter(with(equal(PROPERTIES_CONTAINER)));
    		will(returnValue(null));
    		one(servletContext).getInitParameter(with(equal(scriptName)));
    		will(returnValue(script));
    		one(servletContext).setAttribute(with(equal(BUILDER)), with(any(containerBuilder)));
    		one(servletContext).setAttribute(with(equal(APPLICATION_CONTAINER)), with(any(PicoContainer.class)));  		
    	}});        
        listener.contextInitialized(new ServletContextEvent((ServletContext) servletContext));
    }
    
    @Test public void testApplicationScopeContainerIsCreatedWithInSeparateScripts() {
        assertApplicationScopeContainerIsCreatedWithSeparateScript("nanocontainer.groovy", groovyScript, GroovyContainerBuilder.class);
        assertApplicationScopeContainerIsCreatedWithSeparateScript("nanocontainer.xml", xmlScript, XMLContainerBuilder.class);
    }

    private void assertApplicationScopeContainerIsCreatedWithSeparateScript(final String scriptName, final String script, 
            final Class<?> containerBuilder) {
    	final ServletContext servletContext = mockery.mock(ServletContext.class);
        final Vector<String> initParams = new Vector<String>();
        initParams.add(scriptName);
    	mockery.checking(new Expectations(){{
    		one(servletContext).getInitParameterNames();
    		will(returnValue(initParams.elements()));
    		one(servletContext).getInitParameter(with(equal(scriptName)));
    		will(returnValue("/config/"+scriptName));
    		one(servletContext).getInitParameter(with(equal(SYSTEM_PROPERTIES_CONTAINER)));
    		will(returnValue(null));
    		one(servletContext).getInitParameter(with(equal(PROPERTIES_CONTAINER)));
    		will(returnValue(null));
    		one(servletContext).getResourceAsStream(with(equal("/config/"+scriptName)));
    		will(returnValue(new ByteArrayInputStream(script.getBytes())));
    		one(servletContext).setAttribute(with(equal(BUILDER)), with(any(containerBuilder)));
    		one(servletContext).setAttribute(with(equal(APPLICATION_CONTAINER)), with(any(PicoContainer.class)));
    	}});        
        
        listener.contextInitialized(new ServletContextEvent((ServletContext) servletContext));

    }

    @Test public void testApplicationScopeContainerIsNotBuildWhenNoInitParametersAreFound() {
    	final ServletContext servletContext = mockery.mock(ServletContext.class);
        final Vector initParams = new Vector();
        mockery.checking(new Expectations(){{
    		one(servletContext).getInitParameterNames();
    		will(returnValue(initParams.elements()));
    	}});        
        try {
            listener.contextInitialized(new ServletContextEvent((ServletContext) servletContext));
            fail("PicoCompositionException expected");
        } catch (PicoCompositionException e) {
            assertEquals("Couldn't create a builder from context parameters in web.xml", e.getMessage());
        }
    }

    @Test public void testApplicationScopeContainerIsNotBuildWhenInvalidParametersAreFound() {
    	final ServletContext servletContext = mockery.mock(ServletContext.class);
        final Vector<String> initParams = new Vector<String>();
        initParams.add("invalid-param");
        mockery.checking(new Expectations(){{
    		one(servletContext).getInitParameterNames();
    		will(returnValue(initParams.elements()));
    	}});
        try {
            listener.contextInitialized(new ServletContextEvent(
                    (ServletContext) servletContext));
            fail("PicoCompositionException expected");
        } catch (PicoCompositionException e) {
            assertEquals("Couldn't create a builder from context parameters in web.xml", e.getMessage());
        }
    }       

    @Test public void testApplicationScopeContainerIsNotBuildWhenClassNotFound() {
        final String script = 
            "<container>" +
            "<component-implementation class='com.inexistent.Foo'>" +
            "</component-implementation>" +
            "</container>";
        final ServletContext servletContext = mockery.mock(ServletContext.class);
        final Vector<String> initParams = new Vector<String>();
        initParams.add("nanocontainer.xml");
        mockery.checking(new Expectations(){{
    		one(servletContext).getInitParameterNames();
    		will(returnValue(initParams.elements()));
    		one(servletContext).getInitParameter(with(equal("nanocontainer.xml")));
    		will(returnValue(script));
    		one(servletContext).getInitParameter(with(equal(SYSTEM_PROPERTIES_CONTAINER)));
    		will(returnValue(null));
    		one(servletContext).getInitParameter(with(equal(PROPERTIES_CONTAINER)));
    		will(returnValue(null)); 		
    		one(servletContext).setAttribute(with(equal(BUILDER)), with(any(XMLContainerBuilder.class)));
    	}});      
        try {
            listener.contextInitialized(new ServletContextEvent(
                    (ServletContext) servletContext));
            fail("PicoCompositionException expected");
        } catch (NanoContainerMarkupException e) {
            assertTrue(e.getMessage().contains("Class not found"));
        }
    }       
    

    @Test public void testApplicationScopeContainerIsKilledWhenContextDestroyed() {
    	final ServletContext servletContext = mockery.mock(ServletContext.class);
    	final MutablePicoContainer container = mockery.mock(MutablePicoContainer.class);    	
        mockery.checking(new Expectations(){{
        	one(container).stop();
        	one(container).dispose();
        	one(container).getParent();
    		atLeast(1).of(servletContext).getAttribute(with(equal(APPLICATION_CONTAINER)));
    		will(returnValue(container));
    		one(servletContext).setAttribute(with(any(String.class)), with(any(Object.class)));
    	}});   
        listener.contextDestroyed(new ServletContextEvent(
                    (ServletContext) servletContext));
    }
        
    @Test public void testSessionScopeContainerIsCreatedWithApplicationScopeContainerAsParent(){
        assertSessionScopeContainerIsCreatedWithApplicationScopeContainerAsParent(groovyScript, GroovyContainerBuilder.class);
        assertSessionScopeContainerIsCreatedWithApplicationScopeContainerAsParent(xmlScript, XMLContainerBuilder.class);
    }
    
    private void assertSessionScopeContainerIsCreatedWithApplicationScopeContainerAsParent(
            final String script, final Class<?> containerBuilder) {
    	final ServletContext servletContext = mockery.mock(ServletContext.class);
        final MutablePicoContainer appScopeContainer = new DefaultPicoContainer();
        final HttpSession httpSession = mockery.mock(HttpSession.class);
        mockery.checking(new Expectations(){{
    		one(servletContext).getAttribute(with(equal(APPLICATION_CONTAINER)));
    		will(returnValue(appScopeContainer));
    		one(servletContext).getAttribute(with(equal(BUILDER)));
    		will(returnValue(createContainerBuilder(containerBuilder, script)));
    		one(httpSession).getServletContext();
    		will(returnValue(servletContext));
    		one(httpSession).setAttribute(with(equal(ServletContainerListener.KILLER_HELPER)), 
    				with(any(HttpSessionBindingListener.class)));
    		one(httpSession).setAttribute(with(equal(SESSION_CONTAINER)), 
    				with(any(PicoContainer.class)));
        }}); 

        listener.sessionCreated(new HttpSessionEvent((HttpSession) httpSession));
    }
    
    
    private ContainerBuilder createContainerBuilder(Class containerBuilder, String script) {
        MutablePicoContainer pico = new DefaultPicoContainer();
        pico.addComponent(new StringReader(script));
        pico.addComponent(getClass().getClassLoader());
        pico.addComponent(containerBuilder);
        return pico.getComponent(ContainerBuilder.class);
    }

    @Test public void testSessionDestroyedMethodIsIgnored() {
    	HttpSession httpSession = mockery.mock(HttpSession.class);
        listener.sessionDestroyed(new HttpSessionEvent((HttpSession)httpSession));
    }
    
    @Test public void testGroovyContainerBuilderCanBeScopedWithInlineScriptsUsingPicoSyntax() throws Exception{
      String picoScript =
          "componentFactory = new org.picocontainer.injectors.AdaptingInjection()\n"+
          "pico = new org.picocontainer.DefaultPicoContainer(componentFactory, parent)\n"+
          "   if ( assemblyScope instanceof javax.servlet.ServletContext ){ \n" +
          "      System.out.println('Application scope parent '+parent)\n "+
          "      pico.addComponent((Object)'testFoo', org.nanocontainer.nanowar.Foo)\n" +
          "   } else if ( assemblyScope instanceof javax.servlet.http.HttpSession ){ \n" +
          "      System.out.println('Session scope parent '+parent)\n "+
          "      System.out.println('foo:'+parent.getComponent((Object)'testFoo'))\n"+
          "      pico.addComponent((Object)'testFooHierarchy', org.nanocontainer.nanowar.FooHierarchy)\n"+
          "   }\n "+
          "";
      assertGroovyContainerBuilderCanBeScopedWithInlinedScript(picoScript);
    }

    //NANOWAR-23:  the node builder syntax is failing
    @Test public void testGroovyContainerBuilderCanBeScopedWithInlineScriptsUsingBuilderSyntax() throws Exception{
      String builderScript =
          "pico = builder.container(parent:parent, scope:assemblyScope) {\n" +
          "   if ( assemblyScope instanceof javax.servlet.ServletContext ){ \n" +
          "      component(key:org.nanocontainer.nanowar.Foo, class:org.nanocontainer.nanowar.Foo)\n " +
          "   } else if ( assemblyScope instanceof javax.servlet.http.HttpSession ){ \n" +
          "      component(key:'testFooHierarchy', class:org.nanocontainer.nanowar.FooHierarchy)\n"+
          "   }\n "+
          "}";
      assertGroovyContainerBuilderCanBeScopedWithInlinedScript(builderScript);
    }
    
    public void assertGroovyContainerBuilderCanBeScopedWithInlinedScript(final String script) {

        final Class<GroovyContainerBuilder> containerBuilder = GroovyContainerBuilder.class;
        final PicoContainer applicationContainer = buildApplicationContainer(script, containerBuilder);
        final ServletContext servletContext = mockery.mock(ServletContext.class);
        final HttpSession httpSession = mockery.mock(HttpSession.class);
        mockery.checking(new Expectations(){{
    		one(servletContext).getAttribute(with(equal(APPLICATION_CONTAINER)));
    		will(returnValue(applicationContainer));
    		one(servletContext).getAttribute(with(equal(BUILDER)));
    		will(returnValue(createContainerBuilder(containerBuilder, script)));
    		one(httpSession).getServletContext();
    		will(returnValue(servletContext));
    		one(httpSession).setAttribute(with(equal(ServletContainerListener.KILLER_HELPER)), 
    				with(any(HttpSessionBindingListener.class)));
    		one(httpSession).setAttribute(with(equal(SESSION_CONTAINER)), 
    				with(any(PicoContainer.class)));
        }}); 

        listener.sessionCreated(new HttpSessionEvent((HttpSession) httpSession));

    }    

    private PicoContainer buildApplicationContainer(String script, Class<GroovyContainerBuilder> containerBuilderClass) {
    	ServletContext servletContext = mockery.mock(ServletContext.class);
        ContainerBuilder containerBuilder = createContainerBuilder(script, containerBuilderClass);
        
        ObjectReference containerRef = new SimpleReference();
        containerBuilder.buildContainer(containerRef, new SimpleReference(), servletContext, false);
        return (PicoContainer) containerRef.get();
    }

    private ContainerBuilder createContainerBuilder(String script, Class<GroovyContainerBuilder> containerBuilderClass) {
        ScriptedContainerBuilderFactory scriptedContainerBuilderFactory = 
            new ScriptedContainerBuilderFactory(new StringReader(script), containerBuilderClass.getName(), 
                    Thread.currentThread().getContextClassLoader());
        return scriptedContainerBuilderFactory.getContainerBuilder();
    }

    @Test public void testScopedContainerComposerIsCreatedWithDefaultConfiguration() {
    	final ServletContext servletContext = mockery.mock(ServletContext.class);
        final Vector<String> initParams = new Vector<String>();
        initParams.add(ServletContainerListener.CONTAINER_COMPOSER);
        mockery.checking(new Expectations(){{
    		one(servletContext).getInitParameterNames();
    		will(returnValue(initParams.elements()));
    		one(servletContext).getInitParameter(with(equal(ServletContainerListener.CONTAINER_COMPOSER)));
    		will(returnValue(ScopedContainerComposer.class.getName()));
       		one(servletContext).getInitParameter(with(equal(ServletContainerListener.CONTAINER_COMPOSER_CONFIGURATION)));
    		will(returnValue(null));
    		one(servletContext).getInitParameter(with(equal(SYSTEM_PROPERTIES_CONTAINER)));
    		will(returnValue(null));
    		one(servletContext).getInitParameter(with(equal(PROPERTIES_CONTAINER)));
    		will(returnValue(null)); 		
    		one(servletContext).setAttribute(with(equal(BUILDER)), with(any(DefaultContainerBuilder.class)));
    		one(servletContext).setAttribute(with(equal(APPLICATION_CONTAINER)), with(any(PicoContainer.class)));
    	}});        

        listener.contextInitialized(new ServletContextEvent((ServletContext) servletContext));
    }

    @Test public void testScopedContainerComposerIsCreatedWithXMLConfiguration() {
        String xmlConfig =
            "<container>" +
            "<component-implementation class='org.nanocontainer.nanowar.ScopedContainerConfigurator'>" +
            "      <parameter><string>org.nanocontainer.script.xml.XMLContainerBuilder</string></parameter>" +
            "      <parameter><string>nanowar-application.xml</string></parameter> " +
            "      <parameter><string>nanowar-session.xml</string></parameter>        " +
            "      <parameter><string>nanowar-request.xml</string></parameter> " +
            "</component-implementation>" +
            "</container>";
        assertScopedContainerComposerIsCreatedWithConfiguration("composer-config.xml", xmlConfig);
    }
    
    //Requires GroovyContainerBuilder to implement ContainerPopulator
    public void TODO_testScopedContainerComposerIsCreatedWithGroovyConfiguration() {
        String groovyConfig =
            "pico = builder.container(parent:parent, scope:assemblyScope) {\n" +
            "   component(class:'org.nanocontainer.nanowar.ScopedContainerConfigurator', \n"+
            "             parameters:['org.nanocontainer.script.groovy.GroovyContainerBuilder', " +
            "                         'nanowar-application.groovy', 'nanowar-session.groovy', " +
            "                         'nanowar-request.groovy' ])\n" +
            "}";
        assertScopedContainerComposerIsCreatedWithConfiguration("composer-config.groovy", groovyConfig);        
    }
      
        
    private void assertScopedContainerComposerIsCreatedWithConfiguration(final String scriptName, final String script) {
    	final ServletContext servletContext = mockery.mock(ServletContext.class);
        final Vector<String> initParams = new Vector<String>();
        initParams.add(ServletContainerListener.CONTAINER_COMPOSER);
        mockery.checking(new Expectations(){{
    		one(servletContext).getInitParameterNames();
    		will(returnValue(initParams.elements()));
    		one(servletContext).getInitParameter(with(equal(ServletContainerListener.CONTAINER_COMPOSER)));
    		will(returnValue(ScopedContainerComposer.class.getName()));
       		one(servletContext).getInitParameter(with(equal(ServletContainerListener.CONTAINER_COMPOSER_CONFIGURATION)));
    		will(returnValue("nanowar/"+scriptName));
    		one(servletContext).getInitParameter(with(equal(SYSTEM_PROPERTIES_CONTAINER)));
    		will(returnValue(null));
    		one(servletContext).getInitParameter(with(equal(PROPERTIES_CONTAINER)));
    		will(returnValue(null)); 	
       		one(servletContext).getResourceAsStream(with(equal("nanowar/"+scriptName)));
    		will(returnValue(new ByteArrayInputStream(script.getBytes())));
    		one(servletContext).setAttribute(with(equal(BUILDER)), with(any(DefaultContainerBuilder.class)));
    		one(servletContext).setAttribute(with(equal(APPLICATION_CONTAINER)), with(any(PicoContainer.class)));
    	}});        

        listener.contextInitialized(new ServletContextEvent(servletContext));
    }

}

