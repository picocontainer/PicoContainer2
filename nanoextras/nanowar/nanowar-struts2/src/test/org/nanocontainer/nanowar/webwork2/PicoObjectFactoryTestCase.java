/*****************************************************************************
 * Copyright (C) NanoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the LICENSE.txt file.                                                     *
 *                                                                           *
 *****************************************************************************/
package org.nanocontainer.nanowar.webwork2;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.fail;
import static org.picocontainer.tck.MockFactory.mockeryWithCountingNamingScheme;

import javax.servlet.http.HttpServletRequest;

import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JMock;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.nanocontainer.nanowar.KeyConstants;
import org.picocontainer.Characteristics;
import org.picocontainer.DefaultPicoContainer;
import org.picocontainer.MutablePicoContainer;
import org.picocontainer.ObjectReference;
import org.picocontainer.PicoCompositionException;
import org.picocontainer.injectors.AbstractInjector;
import org.picocontainer.references.ThreadLocalReference;

/**
 * @author Mauro Talevi
 * @author Konstantin Pribluda
 */
@RunWith(JMock.class)
public final class PicoObjectFactoryTestCase {

	private Mockery mockery = mockeryWithCountingNamingScheme();
	
    private PicoObjectFactory factory;
    private DefaultPicoContainer container;
    private final HttpServletRequest request = mockery.mock(HttpServletRequest.class);
    
    @Before public void setUp(){
        container = (DefaultPicoContainer)new DefaultPicoContainer().change(Characteristics.CACHE);
        ObjectReference reference = new ThreadLocalReference();
        reference.set(request);
        factory = new PicoObjectFactory(reference);
    }
    
	@Test public void testActionInstantiationWithValidClassName() throws Exception {
		
		mockery.checking(new Expectations(){{
	        atLeast(1).of(request).getAttribute(with(equal(KeyConstants.ACTIONS_CONTAINER)));
            will(returnValue(null));
            atLeast(1).of(request).getAttribute(with(equal(KeyConstants.REQUEST_CONTAINER)));
            will(returnValue(container));
            atLeast(1).of(request).setAttribute(with(equal(KeyConstants.ACTIONS_CONTAINER)),
                    with(any(MutablePicoContainer.class))); 
		}});
		
		container.addComponent("foo");
		TestAction action = (TestAction) factory
				.buildBean(TestAction.class.getName());
		assertNotNull(action);
		assertEquals("foo", action.getFoo());
	}
    
    @Test public void testActionInstantiationWhichFailsDueToFailedDependencies() throws Exception {
		mockery.checking(new Expectations(){{
	        atLeast(1).of(request).getAttribute(with(equal(KeyConstants.ACTIONS_CONTAINER)));
            will(returnValue(null));
            atLeast(1).of(request).getAttribute(with(equal(KeyConstants.REQUEST_CONTAINER)));
            will(returnValue(container));
            atLeast(1).of(request).setAttribute(with(equal(KeyConstants.ACTIONS_CONTAINER)),
                    with(any(MutablePicoContainer.class))); 
		}});

		TestAction action = null;
        try {
            action = (TestAction) factory
                            .buildBean(TestAction.class.getName());
            fail("should have barfed");
        } catch (AbstractInjector.UnsatisfiableDependenciesException e) {
            // expected
        }
        assertNull(action);
    }

    @Test public void testActionInstantiationWithInvalidClassName() throws Exception {
        try {
            factory.buildBean("invalidAction");
            fail("PicoCompositionException expected");
        } catch ( PicoCompositionException e) {
            // expected
        }
    }

    @Test public void testActionInstantiationWhichHasAlreadyBeenRegistered() throws Exception {
		mockery.checking(new Expectations(){{
	        atLeast(1).of(request).getAttribute(with(equal(KeyConstants.ACTIONS_CONTAINER)));
            will(returnValue(null));
            atLeast(1).of(request).getAttribute(with(equal(KeyConstants.REQUEST_CONTAINER)));
            will(returnValue(container));
            atLeast(1).of(request).setAttribute(with(equal(KeyConstants.ACTIONS_CONTAINER)),
                    with(any(MutablePicoContainer.class))); 
		}});

        container.addComponent("foo");
        container.addComponent(TestAction.class);
        TestAction action1 = container.getComponent(TestAction.class);
        TestAction action2 = (TestAction) factory
                .buildBean(TestAction.class.getName());
        assertSame(action1, action2);
    }

    /**
     * if component was not registered explicitely,  there shall be different instance for
     * next invocation.  not only actions are instantiated via factory,  but also important stuff like filters,
     * validators, interceptors etc - they shall not be shared. 
     * @throws Exception
     */
    @Test public void testActionInstantiationWhichHasAlreadyBeenRequested() throws Exception {
    	mockery.checking(new Expectations(){{
	        atLeast(1).of(request).getAttribute(with(equal(KeyConstants.ACTIONS_CONTAINER)));
            will(returnValue(container));
		}});

    	container.addComponent("foo");
        TestAction action1 = (TestAction) factory
                .buildBean(TestAction.class.getName());
        TestAction action2 = (TestAction) factory
                .buildBean(TestAction.class.getName());
        assertNotSame(action1, action2);
    }
    

}