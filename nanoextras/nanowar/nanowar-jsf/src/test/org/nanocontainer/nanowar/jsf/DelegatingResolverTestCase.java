/*****************************************************************************
 * Copyright (C) PicoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the LICENSE.txt file.                                                     *
 *                                                                           *
 * Original code by Centerline Computers, Inc.                               *
 *****************************************************************************/
package org.nanocontainer.nanowar.jsf;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.picocontainer.tck.MockFactory.mockeryWithCountingNamingScheme;

import java.util.HashMap;
import java.util.Map;

import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.el.VariableResolver;

import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JMock;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.nanocontainer.nanowar.KeyConstants;
import org.picocontainer.DefaultPicoContainer;
import org.picocontainer.MutablePicoContainer;

/**
 * Basic testing for the NanoWAR variable resolver. 
 * DelegatingResolverTestCase
 * @author Michael Rimov
 * @author Mauro Talevi
 */
@RunWith(JMock.class)
public class DelegatingResolverTestCase {

	private Mockery mockery = mockeryWithClassImposteriser();
	
    private MutablePicoContainer appContainer = null;

    private MutablePicoContainer sessionContainer = null;

    private MutablePicoContainer requestContainer = null;

    private VariableResolver variableResolver = mockery.mock(VariableResolver.class);

    private NanoWarDelegatingVariableResolver ourVariableResolver = null;

    private FacesContext facesContext = mockery.mock(FacesContext.class);

    private ExternalContext externalContext = mockery.mock(ExternalContext.class);

    private Map requestMap = null;

    private Map sessionMap = null;

    private Map appMap = null;

    @Before public void setUp() {
        appContainer = new DefaultPicoContainer();
        appContainer.addComponent("A", A.class);

        sessionContainer = appContainer.makeChildContainer();
        sessionContainer.addComponent("B", B.class);

        requestContainer = sessionContainer.makeChildContainer();
        requestContainer.addComponent("C", C.class);

        // Set up getExternalContext common call.
        mockery.checking(new Expectations(){{
            atLeast(1).of(facesContext).getExternalContext();
            will(returnValue(externalContext));        	
        }});

        // Set up return for hashmap.
        requestMap = new HashMap();
        requestMap.put(KeyConstants.REQUEST_CONTAINER, requestContainer);
        sessionMap = new HashMap();
        sessionMap.put(KeyConstants.SESSION_CONTAINER, sessionContainer);
        appMap = new HashMap();
        appMap.put(KeyConstants.APPLICATION_CONTAINER, appContainer);

        // Sanity Check for containers.
        assertNotNull(requestContainer.getComponent("A"));
        ourVariableResolver = new NanoWarDelegatingVariableResolver(variableResolver);

    }

    private Mockery mockeryWithClassImposteriser() {
		Mockery mockery = mockeryWithCountingNamingScheme();
		mockery.setImposteriser(ClassImposteriser.INSTANCE);
		return mockery;
	}

	@After public void tearDown() {
        appContainer = null;
        sessionContainer = null;
        requestContainer = null;
    }

    @Test public void testUnfoundObjectsPassOnToDelegator() {
  	  	mockery.checking(new Expectations(){{
          one(externalContext).getRequestMap();
          will(returnValue(requestMap));        	
          one(variableResolver).resolveVariable(with(any(FacesContext.class)), with(equal("D")));
          will(returnValue(new D()));        	
  	  	}});
        Object result = ourVariableResolver.resolveVariable(facesContext, "D");
        assertTrue(result instanceof D);
    }

    /**
     * Checks to make sure the request level container is returned.
     */
    @Test public void testRequestIsCheckedForRegisteredClasses() {
    	mockery.checking(new Expectations(){{
          atLeast(1).of(externalContext).getRequestMap();
          will(returnValue(requestMap));        	
          never(externalContext).getSessionMap();
          will(returnValue(sessionMap));        	
          never(externalContext).getApplicationMap();
          will(returnValue(appMap));        	
    	}});
        assertNotNull(ourVariableResolver.resolveVariable(facesContext,"C"));
        assertNotNull(ourVariableResolver.resolveVariable(facesContext,"B"));
        assertNotNull(ourVariableResolver.resolveVariable(facesContext,"A"));        
    }

    /**
     * Checks to make sure the session container only is returned.
     */
    @Test public void testSessionIsCheckedForRegisteredClasses() {
        requestMap.clear();
    	mockery.checking(new Expectations(){{
            atLeast(1).of(externalContext).getRequestMap();
            will(returnValue(requestMap));        	
            atLeast(1).of(externalContext).getSessionMap();
            will(returnValue(sessionMap));        	
            never(externalContext).getApplicationMap();
            will(returnValue(appMap));        	
            one(variableResolver).resolveVariable(with(any(FacesContext.class)), with(equal("C")));
            will(returnValue(null));       
      	}});
        assertNull(ourVariableResolver.resolveVariable(facesContext,"C"));
        assertNotNull(ourVariableResolver.resolveVariable(facesContext,"B"));
        assertNotNull(ourVariableResolver.resolveVariable(facesContext,"A"));

    }

    /**
     * Checks to make sure only the application container can be found if request and session containers
     * don't exist.
     *
     */
    @Test public void testApplicationContextIsCheckedForRegisteredClasses() {
        requestMap.clear();
        sessionMap.clear();
        mockery.checking(new Expectations(){{
            atLeast(1).of(externalContext).getRequestMap();
            will(returnValue(requestMap));        	
            atLeast(1).of(externalContext).getSessionMap();
            will(returnValue(sessionMap));        	
            atLeast(1).of(externalContext).getApplicationMap();
            will(returnValue(appMap));        	
            one(variableResolver).resolveVariable(with(any(FacesContext.class)), with(equal("C")));
            will(returnValue(null));       
            one(variableResolver).resolveVariable(with(any(FacesContext.class)), with(equal("B")));
            will(returnValue(null));       
      	}});
        assertNull(ourVariableResolver.resolveVariable(facesContext,"C"));
        assertNull(ourVariableResolver.resolveVariable(facesContext,"B"));
        assertNotNull(ourVariableResolver.resolveVariable(facesContext,"A"));

    }

    /**
     * Test Class.
     */
    public static class A {

    }

    /**
     * Test Class.
     */
    public static class B {

    }

    /**
     * Test Class.
     */
    public static class C {

    }

    /**
     * Test Class.
     */
    public static class D {

    }

}
