/*****************************************************************************
 * Copyright (C) NanoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the LICENSE.txt file.                                                     *
 *                                                                           *
 *****************************************************************************/
package org.nanocontainer.nanowar;

import static org.junit.Assert.assertSame;
import static org.junit.Assert.fail;
import static org.picocontainer.tck.MockFactory.mockeryWithCountingNamingScheme;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JMock;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.picocontainer.DefaultPicoContainer;
import org.picocontainer.MutablePicoContainer;
import org.picocontainer.PicoCompositionException;


/**
 * @author Stephen Molitor
 * @author Mauro Talevi
 */
@RunWith(JMock.class)
public final class ServletContainerFinderTestCase {

	private Mockery mockery = mockeryWithCountingNamingScheme();
	
    private final HttpServletRequest request = mockery.mock(HttpServletRequest.class);
    private final HttpSession session = mockery.mock(HttpSession.class);
    private final ServletContext servletContext = mockery.mock(ServletContext.class);

    private ServletContainerFinder finder = new ServletContainerFinder();

    @Test public void testRequestContainerExists() {
        final MutablePicoContainer container = new DefaultPicoContainer();
    	mockery.checking(new Expectations(){{
    		one(request).getAttribute(with(equal(KeyConstants.REQUEST_CONTAINER)));
    		will(returnValue(container));
    	}});
        assertSame(container, finder.findContainer(request));
    }

    @Test public void testSessionContainerExists() {
        final MutablePicoContainer container = new DefaultPicoContainer();
    	mockery.checking(new Expectations(){{
    		one(request).getAttribute(with(equal(KeyConstants.REQUEST_CONTAINER)));
    		will(returnValue(null));
    		one(session).getAttribute(with(equal(KeyConstants.SESSION_CONTAINER)));
    		will(returnValue(container));    		
    		one(request).getSession();
    		will(returnValue(session));
    	}});
        assertSame(container, finder.findContainer(request));
    }

    @Test public void testApplicationContainerExists() {
        final MutablePicoContainer container = new DefaultPicoContainer();
    	mockery.checking(new Expectations(){{
    		one(request).getAttribute(with(equal(KeyConstants.REQUEST_CONTAINER)));
    		will(returnValue(null));
    		one(session).getAttribute(with(equal(KeyConstants.SESSION_CONTAINER)));
    		will(returnValue(null));    		
    		one(servletContext).getAttribute(with(equal(KeyConstants.APPLICATION_CONTAINER)));
    		will(returnValue(container));    		
    		atLeast(1).of(request).getSession();
    		will(returnValue(session));
    		one(session).getServletContext();
    		will(returnValue(servletContext));    	
    	}});
        assertSame(container, finder.findContainer(request));
    }

    @Test public void testNoContainerExists() {
    	mockery.checking(new Expectations(){{
    		one(request).getAttribute(with(equal(KeyConstants.REQUEST_CONTAINER)));
    		will(returnValue(null));
    		one(session).getAttribute(with(equal(KeyConstants.SESSION_CONTAINER)));
    		will(returnValue(null));    		
    		one(servletContext).getAttribute(with(equal(KeyConstants.APPLICATION_CONTAINER)));
    		will(returnValue(null));    		
    		atLeast(1).of(request).getSession();
    		will(returnValue(session));
    		one(session).getServletContext();
    		will(returnValue(servletContext));    	
    	}});
        
        try {
            finder.findContainer(request);
            fail("PicoCompositionException should have been raised");
        } catch (PicoCompositionException e) {
            // expected
        }
    }

}