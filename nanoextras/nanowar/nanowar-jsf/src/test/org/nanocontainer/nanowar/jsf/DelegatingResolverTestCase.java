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


import java.util.HashMap;
import java.util.Map;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.el.VariableResolver;
import org.jmock.cglib.Mock;
import org.jmock.cglib.MockObjectTestCase;
import org.nanocontainer.nanowar.KeyConstants;
import org.picocontainer.MutablePicoContainer;
import org.picocontainer.DefaultPicoContainer;

/**
 * Basic testing for the NanoWAR variable resolver. 
 * DelegatingResolverTestCase
 * @author Michael Rimov
 */
public class DelegatingResolverTestCase extends MockObjectTestCase {

    private MutablePicoContainer appContainer = null;

    private MutablePicoContainer sessionContainer = null;

    private MutablePicoContainer requestContainer = null;

    private Mock variableResolverMock = null;

    private VariableResolver delegate;

    private NanoWarDelegatingVariableResolver ourVariableResolver = null;

    private FacesContext facesContext = null;

    private Mock facesContextMock = null;

    private Mock externalContextMock = null;

    private ExternalContext externalContext = null;

    private Map requestMap = null;

    private Map sessionMap = null;

    private Map appMap = null;

    protected void setUp() {
        appContainer = new DefaultPicoContainer();
        appContainer.addComponent("A", A.class);

        sessionContainer = appContainer.makeChildContainer();
        sessionContainer.addComponent("B", B.class);

        requestContainer = sessionContainer.makeChildContainer();
        requestContainer.addComponent("C", C.class);

        facesContextMock = new Mock(FacesContext.class);
        facesContext = (FacesContext) facesContextMock.proxy();

        externalContextMock = new Mock(ExternalContext.class);
        externalContext = (ExternalContext) externalContextMock.proxy();
        // Set up getExternalContext common call.
        facesContextMock.expects(atLeastOnce()).method("getExternalContext").will(returnValue(externalContext));

        // Set up return for hashmap.
        requestMap = new HashMap();
        requestMap.put(KeyConstants.REQUEST_CONTAINER, requestContainer);
        sessionMap = new HashMap();
        sessionMap.put(KeyConstants.SESSION_CONTAINER, sessionContainer);
        appMap = new HashMap();
        appMap.put(KeyConstants.APPLICATION_CONTAINER, appContainer);

        variableResolverMock = new Mock(VariableResolver.class);

        // Sanity Check for containers.
        assertNotNull(requestContainer.getComponent("A"));
        delegate = (VariableResolver) variableResolverMock.proxy();
        ourVariableResolver = new NanoWarDelegatingVariableResolver(delegate);

    }

    protected void tearDown() {
        delegate = null;
        appContainer = null;
        sessionContainer = null;
        requestContainer = null;
        variableResolverMock = null;
        delegate = null;
        ourVariableResolver = null;
        facesContext = null;
        externalContextMock = null;
        externalContext = null;
    }

    public void testUnfoundObjectsPassOnToDelegator() {
        externalContextMock.expects(once()).method("getRequestMap").will(returnValue(requestMap));
        variableResolverMock.expects(once()).method("resolveVariable").with(ANYTHING, eq("D")).will(
            returnValue(new D()));
        Object result = ourVariableResolver.resolveVariable(facesContext, "D");
        assertTrue(result instanceof D);
    }

    /**
     * Checks to make sure the request level container is returned.
     */
    public void testRequestIsCheckedForRegisteredClasses() {
        externalContextMock.expects(atLeastOnce()).method("getRequestMap").will(returnValue(requestMap));
        externalContextMock.expects(never()).method("getSessionMap").will(returnValue(sessionMap));
        externalContextMock.expects(never()).method("getApplicationMap").will(returnValue(appMap));
        assertNotNull(ourVariableResolver.resolveVariable(facesContext,"C"));
        assertNotNull(ourVariableResolver.resolveVariable(facesContext,"B"));
        assertNotNull(ourVariableResolver.resolveVariable(facesContext,"A"));
        
    }

    /**
     * Checks to make sure the session container only is returned.
     */
    public void testSessionIsCheckedForRegisteredClasses() {
        requestMap.clear();
        externalContextMock.expects(atLeastOnce()).method("getRequestMap").will(returnValue(requestMap));
        externalContextMock.expects(atLeastOnce()).method("getSessionMap").will(returnValue(sessionMap));
        externalContextMock.expects(never()).method("getApplicationMap").will(returnValue(appMap));
        variableResolverMock.expects(once()).method("resolveVariable").with(ANYTHING, eq("C")).will(returnValue(null));
        assertNull(ourVariableResolver.resolveVariable(facesContext,"C"));
        assertNotNull(ourVariableResolver.resolveVariable(facesContext,"B"));
        assertNotNull(ourVariableResolver.resolveVariable(facesContext,"A"));

    }

    /**
     * Checks to make sure only the application container can be found if request and session containers
     * don't exist.
     *
     */
    public void testApplicationContextIsCheckedForRegisteredClasses() {
        requestMap.clear();
        sessionMap.clear();
        externalContextMock.expects(atLeastOnce()).method("getRequestMap").will(returnValue(requestMap));
        externalContextMock.expects(atLeastOnce()).method("getSessionMap").will(returnValue(sessionMap));
        externalContextMock.expects(atLeastOnce()).method("getApplicationMap").will(returnValue(appMap));
        variableResolverMock.expects(once()).method("resolveVariable").with(ANYTHING, eq("C")).will(returnValue(null));
        variableResolverMock.expects(once()).method("resolveVariable").with(ANYTHING, eq("B")).will(returnValue(null));
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
