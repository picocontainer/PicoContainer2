/*****************************************************************************
 * Copyright (C) NanoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the LICENSE.txt file.                                                     *
 *****************************************************************************/
package org.nanocontainer.nanowar;


import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import static org.picocontainer.tck.MockFactory.mockeryWithCountingNamingScheme;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;

import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JMock;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * 
 * @author Mauro Talevi
 */
@RunWith(JMock.class)
public class ServletContainerLauncherTestCase {

	private Mockery mockery = mockeryWithCountingNamingScheme();
	
	@Test public void testStartContainerWithNullBuilderFails(){
        ServletContext context = mockServletContext(KeyConstants.BUILDER, null);
        HttpServletRequest request = mockHttpServletRequest(null, null);
        ServletRequestContainerLauncher launcher = new ServletRequestContainerLauncher(context, request);
        try {
            launcher.startContainer();
            fail("ServletException expected");
        } catch ( ServletException e) {
            assertEquals(ServletContainerListener.class.getName()+" not deployed", e.getMessage());
        }                
    }

    @Test public void testKillContainerWithNullContainerIsIgnored(){
        ServletContext context = mockServletContext(KeyConstants.BUILDER, null);
        HttpServletRequest request = mockHttpServletRequest(KeyConstants.REQUEST_CONTAINER, null);
        ServletRequestContainerLauncher launcher = new ServletRequestContainerLauncher(context, request);
        launcher.killContainer();
    }
    
    private ServletContext mockServletContext(final String key, final Object attribute) {
    	final ServletContext context = mockery.mock(ServletContext.class);
    	mockery.checking(new Expectations(){{
    		one(context).getAttribute(with(equal(key)));
    		will(returnValue(attribute));
    	}});
        return context;
    }

    private HttpServletRequest mockHttpServletRequest(final String key, final Object attribute) {
    	final HttpServletRequest request = mockery.mock(HttpServletRequest.class);
        if ( key != null ){
        	mockery.checking(new Expectations(){{
        		one(request).getAttribute(with(equal(key)));
        		will(returnValue(attribute));
        	}});
        }
        return request;
    }

}
