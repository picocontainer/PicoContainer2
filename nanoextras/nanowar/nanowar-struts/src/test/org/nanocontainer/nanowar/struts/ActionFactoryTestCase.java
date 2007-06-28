/*****************************************************************************
 * Copyright (C) NanoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the LICENSE.txt file.                                                     *
 *                                                                           *
 *****************************************************************************/
package org.nanocontainer.nanowar.struts;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionServlet;
import org.jmock.Mock;
import org.jmock.cglib.MockObjectTestCase;
import org.nanocontainer.nanowar.KeyConstants;
import org.picocontainer.MutablePicoContainer;
import org.picocontainer.PicoCompositionException;
import org.picocontainer.DefaultPicoContainer;
import org.picocontainer.injectors.AdaptiveInjectionFactory;
import org.picocontainer.behaviors.CachingBehaviorFactory;
import org.picocontainer.behaviors.AbstractBehaviorFactory;

/**
 * @author Stephen Molitor
 */
public final class ActionFactoryTestCase extends MockObjectTestCase {

    private final Mock requestMock = mock(HttpServletRequest.class);
    private final HttpServletRequest request = (HttpServletRequest) requestMock.proxy();

    private final Mock sessionMock = mock(HttpSession.class);
    private final HttpSession session = (HttpSession) sessionMock.proxy();

    private final Mock servletContextMock = mock(ServletContext.class);
    private final ServletContext servletContext = (ServletContext) servletContextMock.proxy();

    private final Mock servletMock = mock(ActionServlet.class);
    private final ActionServlet servlet = (ActionServlet) servletMock.proxy();

    private ActionMapping mapping1;
    private ActionMapping mapping2;

    private ActionFactory actionFactory;
    private TestService service;

    public void testActionContainerCreatedOnlyOncePerRequest() {
        MutablePicoContainer requestContainer = new DefaultPicoContainer();
        requestContainer.addComponent(TestService.class);
        MutablePicoContainer actionsContainer = new DefaultPicoContainer(requestContainer);

        requestMock.expects(once()).method("getAttribute").with(eq(KeyConstants.ACTIONS_CONTAINER)).will(
                returnValue(actionsContainer));
        requestMock.expects(once()).method("getAttribute").with(eq(KeyConstants.ACTIONS_CONTAINER)).will(
                returnValue(actionsContainer));
        requestMock.expects(once()).method("setAttribute").with(eq(KeyConstants.ACTIONS_CONTAINER),
                isA(MutablePicoContainer.class));
        requestMock.expects(once()).method("getAttribute").with(eq(KeyConstants.REQUEST_CONTAINER)).will(
                returnValue(requestContainer));
        requestMock.expects(once()).method("getAttribute").with(eq(KeyConstants.ACTIONS_CONTAINER)).will(
                returnValue(null));

        actionFactory.getAction(request, mapping1, servlet);
        actionFactory.getAction(request, mapping1, servlet);
        actionFactory.getAction(request, mapping1, servlet);
    }

    public void testGetActionWhenActionsContainerAlreadyExists() {
        MutablePicoContainer requestContainer = new DefaultPicoContainer(new CachingBehaviorFactory().forThis(new AdaptiveInjectionFactory()));
        requestContainer.addComponent(TestService.class, service);
        MutablePicoContainer actionsContainer = new DefaultPicoContainer(new CachingBehaviorFactory().forThis(new AdaptiveInjectionFactory()),requestContainer);

        requestMock.stubs().method("getAttribute").with(eq(KeyConstants.ACTIONS_CONTAINER)).will(
                returnValue(actionsContainer));

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

    public void testRequestContainerExists() {
        MutablePicoContainer requestContainer = new DefaultPicoContainer();
        requestContainer.addComponent(TestService.class, service);

        requestMock.expects(once()).method("getAttribute").with(eq(KeyConstants.ACTIONS_CONTAINER)).will(
                returnValue(null));
        requestMock.expects(once()).method("getAttribute").with(eq(KeyConstants.REQUEST_CONTAINER)).will(
                returnValue(requestContainer));
        requestMock.expects(once()).method("setAttribute").with(eq(KeyConstants.ACTIONS_CONTAINER),
                isA(MutablePicoContainer.class));

        TestAction action = (TestAction) actionFactory.getAction(request, mapping1, servlet);
        assertNotNull(action);
        assertSame(service, action.getService());
    }

    public void testSessionContainerExists() {
        MutablePicoContainer sessionContainer = new DefaultPicoContainer();
        sessionContainer.addComponent(TestService.class, service);

        requestMock.expects(once()).method("getAttribute").with(eq(KeyConstants.ACTIONS_CONTAINER)).will(
                returnValue(null));
        requestMock.expects(once()).method("getAttribute").with(eq(KeyConstants.REQUEST_CONTAINER)).will(
                returnValue(null));
        sessionMock.expects(once()).method("getAttribute").with(eq(KeyConstants.SESSION_CONTAINER)).will(
                returnValue(sessionContainer));
        requestMock.expects(once()).method("setAttribute").with(eq(KeyConstants.ACTIONS_CONTAINER),
                isA(MutablePicoContainer.class));

        TestAction action = (TestAction) actionFactory.getAction(request, mapping1, servlet);
        assertNotNull(action);
        assertSame(service, action.getService());
    }

    public void testApplicationContainerExists() {
        MutablePicoContainer appContainer = new DefaultPicoContainer();
        appContainer.addComponent(TestService.class, service);

        requestMock.expects(once()).method("getAttribute").with(eq(KeyConstants.ACTIONS_CONTAINER)).will(
                returnValue(null));
        requestMock.expects(once()).method("getAttribute").with(eq(KeyConstants.REQUEST_CONTAINER)).will(
                returnValue(null));
        sessionMock.expects(once()).method("getAttribute").with(eq(KeyConstants.SESSION_CONTAINER)).will(
                returnValue(null));
        servletContextMock.expects(once()).method("getAttribute").with(eq(KeyConstants.APPLICATION_CONTAINER)).will(
                returnValue(appContainer));
        requestMock.expects(once()).method("setAttribute").with(eq(KeyConstants.ACTIONS_CONTAINER),
                isA(MutablePicoContainer.class));

        TestAction action = (TestAction) actionFactory.getAction(request, mapping1, servlet);
        assertNotNull(action);
        assertSame(service, action.getService());
    }

    public void testNoContainerExists() {
        requestMock.expects(once()).method("getAttribute").with(eq(KeyConstants.ACTIONS_CONTAINER)).will(
                returnValue(null));
        requestMock.expects(once()).method("getAttribute").with(eq(KeyConstants.REQUEST_CONTAINER)).will(
                returnValue(null));
        sessionMock.expects(once()).method("getAttribute").with(eq(KeyConstants.SESSION_CONTAINER)).will(
                returnValue(null));
        servletContextMock.expects(once()).method("getAttribute").with(eq(KeyConstants.APPLICATION_CONTAINER)).will(
                returnValue(null));

        try {
            actionFactory.getAction(request, mapping1, servlet);
            fail("PicoCompositionException should have been raised");
        } catch (PicoCompositionException e) {
            // expected
        }
    }

    public void testBadActionType() {
        MutablePicoContainer actionsContainer = new DefaultPicoContainer();
        requestMock.stubs().method("getAttribute").with(eq(KeyConstants.ACTIONS_CONTAINER)).will(
                returnValue(actionsContainer));

        mapping1.setType("/i/made/a/typo");
        try {
            actionFactory.getAction(request, mapping1, servlet);
            fail("PicoCompositionException should have been raised");
        } catch (PicoCompositionException e) {
            // expected
        }
    }

    protected void setUp() {
        String actionType = StrutsTestAction.class.getName();

        mapping1 = new ActionMapping();
        mapping1.setPath("/myPath1");
        mapping1.setType(actionType);

        mapping2 = new ActionMapping();
        mapping2.setPath("/myPath2");
        mapping2.setType(actionType);

        requestMock.stubs().method("getSession").will(returnValue(session));
        sessionMock.stubs().method("getServletContext").will(returnValue(servletContext));

        actionFactory = new ActionFactory();
        service = new TestService();
    }

}