/*****************************************************************************
 * Copyright (C) NanoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the LICENSE.txt file.                                                     *
 *****************************************************************************/
package org.nanocontainer.nanowar;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;

import org.jmock.Mock;
import org.jmock.MockObjectTestCase;

/**
 * 
 * @author Mauro Talevi
 */
public class ServletContainerLauncherTestCase extends MockObjectTestCase {

    public void testStartContainerWithNullBuilderFails(){
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

    public void testKillContainerWithNullContainerIsIgnored(){
        ServletContext context = mockServletContext(KeyConstants.BUILDER, null);
        HttpServletRequest request = mockHttpServletRequest(KeyConstants.REQUEST_CONTAINER, null);
        ServletRequestContainerLauncher launcher = new ServletRequestContainerLauncher(context, request);
        launcher.killContainer();
    }
    
    private ServletContext mockServletContext(String key, Object attribute) {
        Mock mock = mock(ServletContext.class);
        mock.expects(once()).method("getAttribute").with(eq(key)).will(returnValue(attribute));
        return (ServletContext)mock.proxy();
    }

    private HttpServletRequest mockHttpServletRequest(String key, Object attribute) {
        Mock mock = mock(HttpServletRequest.class);
        if ( key != null ){
            mock.expects(once()).method("getAttribute").with(eq(key)).will(returnValue(attribute));
        }
        return (HttpServletRequest)mock.proxy();
    }

}
