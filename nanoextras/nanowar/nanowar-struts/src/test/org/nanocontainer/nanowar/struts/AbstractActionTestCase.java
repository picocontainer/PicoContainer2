/*****************************************************************************
 * Copyright (C) NanoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the LICENSE.txt file.                                                     *
 *                                                                           *
 *****************************************************************************/
package org.nanocontainer.nanowar.struts;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionMapping;
import org.jmock.Mock;
import org.jmock.MockObjectTestCase;
import org.nanocontainer.nanowar.KeyConstants;
import org.picocontainer.MutablePicoContainer;
import org.picocontainer.DefaultPicoContainer;

/**
 * @author Stephen Molitor
 */
public abstract class AbstractActionTestCase extends MockObjectTestCase {
    protected HttpServletRequest request;
    protected HttpServletResponse response;
    protected ActionMapping mapping;
    protected TestService service;

    private Mock requestMock;
    private Mock responseMock;
    private MutablePicoContainer container;

    protected void setUp() {
        requestMock = mock(HttpServletRequest.class);
        request = (HttpServletRequest) requestMock.proxy();

        responseMock = mock(HttpServletResponse.class);
        response = (HttpServletResponse) responseMock.proxy();

        String actionType = StrutsTestAction.class.getName();
        mapping = new ActionMapping();
        mapping.setPath("/myPath1");
        mapping.setType(actionType);

        service = new TestService();
        container = new DefaultPicoContainer();
        container.addComponent(TestService.class, service);

        requestMock.stubs().method("getAttribute").with(eq(KeyConstants.ACTIONS_CONTAINER)).will(returnValue(container));
    }

}