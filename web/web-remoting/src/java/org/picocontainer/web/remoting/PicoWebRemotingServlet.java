/*******************************************************************************
 * Copyright (c) PicoContainer Organization. All rights reserved.
 * ---------------------------------------------------------------------------
 * The software in this package is published under the terms of the BSD style
 * license a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 ******************************************************************************/

package org.picocontainer.web.remoting;

import java.util.Collection;
import java.io.IOException;

import org.picocontainer.ComponentAdapter;
import org.picocontainer.MutablePicoContainer;
import org.picocontainer.web.PicoServletContainerFilter;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.ServletConfig;

/**
 * All for the calling of methods in a tree of components manages by PicoContainer.
 * JSON is the nature of the reply, the request is plainly mapped from Query Strings
 * and Form params to the method signature.
 *
 * @author Paul Hammant
 */
@SuppressWarnings("serial")
public class PicoWebRemotingServlet extends HttpServlet {

    //private String scopesToPublish;

    private PicoWebRemoting pwr;

    public static class ServletFilter extends PicoServletContainerFilter {
        private static ThreadLocal<MutablePicoContainer> currentRequestContainer = new ThreadLocal<MutablePicoContainer>();
        private static ThreadLocal<MutablePicoContainer> currentSessionContainer = new ThreadLocal<MutablePicoContainer>();
        private static ThreadLocal<MutablePicoContainer> currentAppContainer = new ThreadLocal<MutablePicoContainer>();

        protected void setAppContainer(MutablePicoContainer container) {
            currentAppContainer.set(container);
        }

        protected void setRequestContainer(MutablePicoContainer container) {
            currentRequestContainer.set(container);
        }

        protected void setSessionContainer(MutablePicoContainer container) {
            currentSessionContainer.set(container);
        }

        protected static MutablePicoContainer getRequestContainerForThread() {
            return currentRequestContainer.get();
        }

        protected static MutablePicoContainer getSessionContainerForThread() {
            return currentSessionContainer.get();
        }

        protected static MutablePicoContainer getApplicationContainerForThread() {
            return currentAppContainer.get();
        }
    }

    private boolean initialized;

    protected void service(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        if (!initialized) {
            initialize();
            initialized = true;
        }

        String pathInfo = req.getPathInfo();

        resp.setContentType("text/plain");
        ServletOutputStream outputStream = resp.getOutputStream();
        try {
            String result = pwr.processRequest(pathInfo, ServletFilter.getRequestContainerForThread(), req.getMethod());
            if (result != null) {
                outputStream.print(result);
            } else {
                resp.sendError(400, "Nothing is mapped to this URL, remove the last term for directory list.");
            }
        } catch (RuntimeException e) {
            // TODO monitor
            outputStream.print(pwr.errorResult(e));
        }
    }


    public void init(ServletConfig servletConfig) throws ServletException {
        String packagePrefixToStrip = servletConfig.getInitParameter("package_prefix_to_strip");
        String toStripFromUrls;
        if (packagePrefixToStrip == null) {
            toStripFromUrls = "";
        } else {
            toStripFromUrls = packagePrefixToStrip.replace('.', '/') + "/";
        }

        String scopesToPublish = servletConfig.getInitParameter("scopes_to_publish");
        super.init(servletConfig);
        pwr = new PicoWebRemoting(toStripFromUrls, scopesToPublish);
    }

    private void initialize() {
        pwr.publishAdapters(ServletFilter.getRequestContainerForThread().getComponentAdapters(), "request");
        pwr.publishAdapters(ServletFilter.getSessionContainerForThread().getComponentAdapters(), "session");
        pwr.publishAdapters(ServletFilter.getApplicationContainerForThread().getComponentAdapters(), "application");
    }

}
