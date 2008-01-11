/*****************************************************************************
 * Copyright (C) NanoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the LICENSE.txt file.                                                     *
 *                                                                           *
 *****************************************************************************/
package org.nanocontainer.nanowar.struts;

import static org.picocontainer.tck.MockFactory.mockeryWithCountingNamingScheme;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionMapping;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JMock;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.nanocontainer.nanowar.KeyConstants;
import org.picocontainer.DefaultPicoContainer;
import org.picocontainer.MutablePicoContainer;

/**
 * @author Stephen Molitor
 * @author Mauro Talevi
 */
@RunWith(JMock.class)
public abstract class AbstractActionTestCase {

	Mockery mockery = mockeryWithCountingNamingScheme();
	
	protected HttpServletRequest request = mockery. mock(HttpServletRequest.class);
    protected HttpServletResponse response = mockery. mock(HttpServletResponse.class);
    protected ActionMapping mapping;
    protected TestService service;

    private MutablePicoContainer container;

    @Before public void setUp() {
        String actionType = StrutsTestAction.class.getName();
        mapping = new ActionMapping();
        mapping.setPath("/myPath1");
        mapping.setType(actionType);

        service = new TestService();
        container = new DefaultPicoContainer();
        container.addComponent(TestService.class, service);
        
        mockery.checking(new Expectations(){{
            atLeast(1).of(request).getAttribute(with(equal(KeyConstants.ACTIONS_CONTAINER)));
            will(returnValue(container));        	
        }});
    }

}