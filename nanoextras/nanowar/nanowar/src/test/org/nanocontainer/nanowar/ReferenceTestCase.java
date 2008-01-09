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
import static org.picocontainer.tck.MockFactory.mockeryWithCountingNamingScheme;

import java.io.UnsupportedEncodingException;

import javax.servlet.ServletContext;
import javax.servlet.ServletRequest;
import javax.servlet.http.HttpSession;

import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JMock;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.picocontainer.ObjectReference;

/**
 * @author Aslak Helles&oslash;y
 * @author Mauro Talevi
 */
@RunWith(JMock.class)
public class ReferenceTestCase {

	private Mockery mockery = mockeryWithCountingNamingScheme();
	
	private final String key = "foo";
    private final Object value = new Object();

    @Test public void testRequestScope() throws UnsupportedEncodingException {
    	final ServletRequest request = mockery.mock(ServletRequest.class);
    	mockery.checking(new Expectations(){{
    		one(request).setAttribute(with(equal(key)), with(equal(value)));
    		one(request).getAttribute(with(equal(key)));
    		will(returnValue(value));
    	}});
        RequestScopeReference ref = new RequestScopeReference(request, key);
        setGetAndVerify(ref);
    }

    @Test public void testApplicationScope() {
    	final ServletContext context = mockery.mock(ServletContext.class);
    	mockery.checking(new Expectations(){{
    		one(context).setAttribute(with(equal(key)), with(equal(value)));
    		one(context).getAttribute(with(equal(key)));
    		will(returnValue(value));
    	}});
        ApplicationScopeReference ref = new ApplicationScopeReference(context, key);
        setGetAndVerify(ref);
    }

    @Test public void testSessionScope() {
    	final HttpSession session = mockery.mock(HttpSession.class);
    	mockery.checking(new Expectations(){{
    		one(session).setAttribute(with(equal(key)), with(equal(value)));
    		one(session).getAttribute(with(equal(key)));
    		will(returnValue(value));
    	}});
        SessionScopeReference ref = new SessionScopeReference(session, key);
        setGetAndVerify(ref);
    }

    private void setGetAndVerify(ObjectReference ref) {
        ref.set(value);
        assertEquals(value, ref.get());
    }

  

}
