/*******************************************************************************
 * Copyright (c) PicoContainer Organization. All rights reserved.
 * ---------------------------------------------------------------------------
 * The software in this package is published under the terms of the BSD style
 * license a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 ******************************************************************************/

package org.picocontainer.web.remoting;

import java.io.IOException;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.picocontainer.MutablePicoContainer;
import org.picocontainer.web.PicoServletContainerFilter;

/**
 * All for the calling of methods in a tree of components manages by PicoContainer.
 * JSON is the nature of the reply, the request is plainly mapped from Query Strings
 * and Form params to the method signature.
 *
 * @author Paul Hammant
 */
@SuppressWarnings("serial")
public class PicoWebRemotingServlet extends HttpServlet {

	private static final String APPLICATION_SCOPE = "application";
	private static final String SESSION_SCOPE = "session";
	private static final String REQUEST_SCOPE = "request";
    private static final String SCOPES_TO_PUBLISH = "scopes_to_publish";
    private static final String XML_INSTEAD_OF_JSON = "xml_instead_of_json";
	private static final String PACKAGE_PREFIX_TO_STRIP = "package_prefix_to_strip";

    private PicoWebRemoting pwr;
    private static ThreadLocal<MutablePicoContainer> currentRequestContainer = new ThreadLocal<MutablePicoContainer>();
    private static ThreadLocal<MutablePicoContainer> currentSessionContainer = new ThreadLocal<MutablePicoContainer>();
    private static ThreadLocal<MutablePicoContainer> currentAppContainer = new ThreadLocal<MutablePicoContainer>();



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
        String result = pwr.processRequest(pathInfo, currentRequestContainer.get(), req.getMethod());
        if (result != null) {
            outputStream.print(result);
        } else {
            resp.sendError(400, "Nothing is mapped to this URL, remove the last term for directory list.");
        }
    }

    public void init(ServletConfig servletConfig) throws ServletException {
        String packagePrefixToStrip = servletConfig.getInitParameter(PACKAGE_PREFIX_TO_STRIP);
        String toStripFromUrls;
        if (packagePrefixToStrip == null) {
            toStripFromUrls = "";
        } else {
            toStripFromUrls = packagePrefixToStrip.replace('.', '/') + "/";
        }

        String scopesToPublish = servletConfig.getInitParameter(SCOPES_TO_PUBLISH);
        if (scopesToPublish == null) {
            scopesToPublish = "";
        }
        String xmlNotJsonString = servletConfig.getInitParameter(XML_INSTEAD_OF_JSON);
        boolean xmlInsteadOfJson = false;
        if (xmlNotJsonString != null && !"".equals(xmlNotJsonString)) {
            xmlInsteadOfJson = Boolean.parseBoolean(xmlNotJsonString);
        }
        super.init(servletConfig);
        pwr = new PicoWebRemoting(toStripFromUrls, scopesToPublish, xmlInsteadOfJson);
    }

    private void initialize() {
        pwr.publishAdapters(currentRequestContainer.get().getComponentAdapters(), REQUEST_SCOPE);
        pwr.publishAdapters(currentSessionContainer.get().getComponentAdapters(), SESSION_SCOPE);
        pwr.publishAdapters(currentAppContainer.get().getComponentAdapters(), APPLICATION_SCOPE);
    }

}
