/*******************************************************************************
 * Copyright (C) NanoContainer Organization. All rights reserved. *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD * style
 * license a copy of which has been included with this distribution in * the
 * LICENSE.txt file. * *
 ******************************************************************************/
package org.nanocontainer.nanowar.struts2;

import java.io.IOException;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts2.views.JspSupportServlet;
import org.nanocontainer.nanowar.ServletRequestContainerLauncher;

/**
 * Extension to the standard Struts2 JspSupportServlet that instantiates a new
 * container in the request scope for each request and disposes of it correctly
 * at the end of the request. <p/> To use, replace the Struts2 JspSupportServlet
 * in web.xml with this.
 * 
 * @author Joe Walnes
 */
public class PicoStruts2Servlet extends JspSupportServlet { // was ServletDispatcher

    public PicoStruts2Servlet(ServletContext servletContext) {
        super();
        // TODO set action proxy factory to use Pico's
        // DefaultActionProxyFactory.setFactory(new PicoActionProxyFactory());
    }

    public void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        ServletRequestContainerLauncher containerLauncher = new ServletRequestContainerLauncher(getServletContext(),
                request);
        try {
            containerLauncher.startContainer();
            // process the servlet using struts2
            super.service(request, response);
        } finally {
            containerLauncher.killContainer();
        }
    }
}
