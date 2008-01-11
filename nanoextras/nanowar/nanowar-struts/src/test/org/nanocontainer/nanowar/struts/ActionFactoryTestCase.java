/*****************************************************************************
 * Copyright (C) NanoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the LICENSE.txt file.                                                     *
 *                                                                           *
 *****************************************************************************/
package org.nanocontainer.nanowar.struts;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.fail;
import static org.picocontainer.tck.MockFactory.mockeryWithCountingNamingScheme;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionServlet;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JMock;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.nanocontainer.nanowar.KeyConstants;
import org.picocontainer.DefaultPicoContainer;
import org.picocontainer.MutablePicoContainer;
import org.picocontainer.PicoCompositionException;
import org.picocontainer.behaviors.Caching;

/**
 * @author Stephen Molitor
 * @author Mauro Talevi
 */
@RunWith(JMock.class)
public final class ActionFactoryTestCase {
	
	private Mockery mockery = mockeryWithClassImposteriser();
	
    private final HttpServletRequest request = mockery.mock(HttpServletRequest.class);

    private final HttpSession session = mockery.mock(HttpSession.class);

    private final ServletContext servletContext = mockery.mock(ServletContext.class);

    private final ActionServlet servlet = mockery.mock(ActionServlet.class);

    private ActionMapping mapping1;
    private ActionMapping mapping2;

    private ActionFactory actionFactory;
    private TestService service;

    @Before public void setUp() {
        String actionType = StrutsTestAction.class.getName();

        mapping1 = new ActionMapping();
        mapping1.setPath("/myPath1");
        mapping1.setType(actionType);

        mapping2 = new ActionMapping();
        mapping2.setPath("/myPath2");
        mapping2.setType(actionType);

        mockery.checking(new Expectations(){{
            atLeast(0).of(request).getSession();
            will(returnValue(session));
            atLeast(0).of(session).getServletContext();
            will(returnValue(servletContext));
        }});

        actionFactory = new ActionFactory();
        service = new TestService();
    }

    private Mockery mockeryWithClassImposteriser() {
		Mockery mockery = mockeryWithCountingNamingScheme();
		mockery.setImposteriser(ClassImposteriser.INSTANCE);
		return mockery;
	}

    @Test public void testActionContainerCreatedOnlyOncePerRequest() {
        final MutablePicoContainer requestContainer = new DefaultPicoContainer();
        requestContainer.addComponent(TestService.class);
        final MutablePicoContainer actionsContainer = new DefaultPicoContainer(requestContainer);
        
        mockery.checking(new Expectations(){{
            one(request).getAttribute(with(equal(KeyConstants.ACTIONS_CONTAINER)));
            will(returnValue(actionsContainer));
            one(request).getAttribute(with(equal(KeyConstants.ACTIONS_CONTAINER)));
            will(returnValue(actionsContainer));
            one(request).setAttribute(with(equal(KeyConstants.ACTIONS_CONTAINER)),
                    with(any(MutablePicoContainer.class)));
            one(request).getAttribute(with(equal(KeyConstants.REQUEST_CONTAINER)));
            will(returnValue(requestContainer));
            one(request).getAttribute(with(equal(KeyConstants.ACTIONS_CONTAINER)));
            will(returnValue(null));        	
        }});

        actionFactory.getAction(request, mapping1, servlet);
        actionFactory.getAction(request, mapping1, servlet);
        actionFactory.getAction(request, mapping1, servlet);
    }

    @Test public void testGetActionWhenActionsContainerAlreadyExists() {
        MutablePicoContainer requestContainer = new DefaultPicoContainer(new Caching());
        requestContainer.addComponent(TestService.class, service);
        final MutablePicoContainer actionsContainer = new DefaultPicoContainer(new Caching(),requestContainer);

        mockery.checking(new Expectations(){{
            atLeast(1).of(request).getAttribute(with(equal(KeyConstants.ACTIONS_CONTAINER)));
            will(returnValue(actionsContainer));
        }});

        StrutsTestAction action1 = (StrutsTestAction) actionFactory.getAction(request, mapping1, servlet);
        StrutsTestAction action2 = (StrutsTestAction) actionFactory.getAction(request, mapping2, servlet);
        TestAction action3 = (TestAction) actionFactory.getAction(request, mapping1, servlet);
        TestAction action4 = (TestAction) actionFactory.getAction(request, mapping2, servlet);

        assertNotNull(action1);
        assertNotNull(action2);
        assertNotSame(action1, action2);
        assertSame(action1, action3);
        assertSame(action2, action4);

        assertSame(action1, actionsContainer.getComponent("/myPath1"));
        assertSame(action2, actionsContainer.getComponent("/myPath2"));

        assertSame(service, action1.getService());
        assertSame(service, action2.getService());

        assertNotNull(action1.getServlet());
        assertNotNull(action2.getServlet());
        assertSame(servlet, action1.getServlet());
        assertSame(servlet, action2.getServlet());
    }

    @Test public void testRequestContainerExists() {
        final MutablePicoContainer requestContainer = new DefaultPicoContainer();
        requestContainer.addComponent(TestService.class, service);

        mockery.checking(new Expectations(){{
            one(request).getAttribute(with(equal(KeyConstants.ACTIONS_CONTAINER)));
            will(returnValue(null));
            one(request).getAttribute(with(equal(KeyConstants.REQUEST_CONTAINER)));
            will(returnValue(requestContainer));
            one(request).setAttribute(with(equal(KeyConstants.ACTIONS_CONTAINER)),
                    with(any(MutablePicoContainer.class)));        	
        }});
        
        TestAction action = (TestAction) actionFactory.getAction(request, mapping1, servlet);
        assertNotNull(action);
        assertSame(service, action.getService());
    }

    @Test public void testSessionContainerExists() {
        final MutablePicoContainer sessionContainer = new DefaultPicoContainer();
        sessionContainer.addComponent(TestService.class, service);

        mockery.checking(new Expectations(){{
            one(request).getAttribute(with(equal(KeyConstants.ACTIONS_CONTAINER)));
            will(returnValue(null));
            one(request).getAttribute(with(equal(KeyConstants.REQUEST_CONTAINER)));
            will(returnValue(null));
            one(session).getAttribute(with(equal(KeyConstants.SESSION_CONTAINER)));
            will(returnValue(sessionContainer));
            one(request).setAttribute(with(equal(KeyConstants.ACTIONS_CONTAINER)),
                    with(any(MutablePicoContainer.class)));        	
        }});

        TestAction action = (TestAction) actionFactory.getAction(request, mapping1, servlet);
        assertNotNull(action);
        assertSame(service, action.getService());
    }

    @Test public void testApplicationContainerExists() {
        final MutablePicoContainer appContainer = new DefaultPicoContainer();
        appContainer.addComponent(TestService.class, service);

        mockery.checking(new Expectations(){{
            one(request).getAttribute(with(equal(KeyConstants.ACTIONS_CONTAINER)));
            will(returnValue(null));
            one(request).getAttribute(with(equal(KeyConstants.REQUEST_CONTAINER)));
            will(returnValue(null));
            one(session).getAttribute(with(equal(KeyConstants.SESSION_CONTAINER)));
            will(returnValue(null));
            one(servletContext).getAttribute(with(equal(KeyConstants.APPLICATION_CONTAINER)));
            will(returnValue(appContainer));
            one(request).setAttribute(with(equal(KeyConstants.ACTIONS_CONTAINER)),
                    with(any(MutablePicoContainer.class)));        	
        }});

        
        TestAction action = (TestAction) actionFactory.getAction(request, mapping1, servlet);
        assertNotNull(action);
        assertSame(service, action.getService());
    }

    @Test public void testNoContainerExists() {

        mockery.checking(new Expectations(){{
            one(request).getAttribute(with(equal(KeyConstants.ACTIONS_CONTAINER)));
            will(returnValue(null));
            one(request).getAttribute(with(equal(KeyConstants.REQUEST_CONTAINER)));
            will(returnValue(null));
            one(session).getAttribute(with(equal(KeyConstants.SESSION_CONTAINER)));
            will(returnValue(null));
            one(servletContext).getAttribute(with(equal(KeyConstants.APPLICATION_CONTAINER)));
            will(returnValue(null));
        }});
        
        try {
            actionFactory.getAction(request, mapping1, servlet);
            fail("PicoCompositionException should have been raised");
        } catch (PicoCompositionException e) {
            // expected
        }
    }

    @Test public void testBadActionType() {
        final MutablePicoContainer actionsContainer = new DefaultPicoContainer();

        mockery.checking(new Expectations(){{
            one(request).getAttribute(with(equal(KeyConstants.ACTIONS_CONTAINER)));
            will(returnValue(actionsContainer));
        }});
        
        mapping1.setType("/i/made/a/typo");
        try {
            actionFactory.getAction(request, mapping1, servlet);
            fail("PicoCompositionException should have been raised");
        } catch (PicoCompositionException e) {
            // expected
        }
    }

}