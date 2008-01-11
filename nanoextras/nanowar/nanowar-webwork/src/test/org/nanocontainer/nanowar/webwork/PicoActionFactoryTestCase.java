/*****************************************************************************
 * Copyright (C) NanoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the LICENSE.txt file.                                                     *
 *                                                                           *
 *****************************************************************************/
package org.nanocontainer.nanowar.webwork;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.picocontainer.tck.MockFactory.mockeryWithCountingNamingScheme;

import javax.servlet.http.HttpServletRequest;

import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JMock;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.nanocontainer.nanowar.KeyConstants;
import org.picocontainer.DefaultPicoContainer;
import org.picocontainer.MutablePicoContainer;
import org.picocontainer.behaviors.Caching;

import webwork.action.ServletActionContext;

/**
 * @author Konstantin Pribluda
 * @author Mauro Talevi
 */
@RunWith(JMock.class)
public class PicoActionFactoryTestCase {

	private Mockery mockery = mockeryWithCountingNamingScheme();
	
    private PicoActionFactory factory;
    private DefaultPicoContainer container;
    
    @Before public void setUp(){
        factory = new PicoActionFactory();
        container = new DefaultPicoContainer(new Caching());
        (new ActionContextScopeReference(KeyConstants.REQUEST_CONTAINER)).set(container);
    }
    
	@Test public void testActionInstantiationWithValidClassName() throws Exception {
		container.addComponent("foo");
		TestAction action = (TestAction) factory
				.getActionImpl(TestAction.class.getName());
		assertNotNull(action);
		assertEquals("foo", action.getFoo());
	}
    
    @Test public void testActionInstantiationWhichFailsDueToFailedDependencies() throws Exception {
        TestAction action = (TestAction) factory
                .getActionImpl(TestAction.class.getName());
        assertNull(action);
    }

    @Test public void testActionInstantiationWithInvalidClassName() throws Exception {
        container.addComponent("foo");
        TestAction action = (TestAction) factory
                .getActionImpl("invalidAction");
        assertNull(action);
    }

    @Test public void testActionInstantiationWhichHasAlreadyBeenRegistered() throws Exception {
        container.addComponent("foo");
        container.addComponent(TestAction.class);
        TestAction action1 = container.getComponent(TestAction.class);
        TestAction action2 = (TestAction) factory
                .getActionImpl(TestAction.class.getName());
        assertSame(action1, action2);
    }

    @Test public void testActionInstantiationWhichHasAlreadyBeenRequested() throws Exception {
        container.addComponent("foo");
        TestAction action1 = (TestAction) factory
                .getActionImpl(TestAction.class.getName());
        TestAction action2 = (TestAction) factory
                .getActionImpl(TestAction.class.getName());
        assertSame(action1, action2);
    }
    
    @Test public void testActionContainerIsFoundInRequest() throws Exception {
    	
    	final HttpServletRequest request = mockery.mock(HttpServletRequest.class);
    	mockery.checking(new Expectations(){{
	        atLeast(1).of(request).getAttribute(with(equal(KeyConstants.ACTIONS_CONTAINER)));
            will(returnValue(null));
            atLeast(1).of(request).getAttribute(with(equal(KeyConstants.REQUEST_CONTAINER)));
            will(returnValue(container));
            atLeast(1).of(request).setAttribute(with(equal(KeyConstants.ACTIONS_CONTAINER)),
                    with(any(MutablePicoContainer.class))); 
		}});

        ServletActionContext.setRequest(request);
        container.addComponent("foo");
        TestAction action = (TestAction) factory
                .getActionImpl(TestAction.class.getName());
        assertNotNull(action);        
    }

}