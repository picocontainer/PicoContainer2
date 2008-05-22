/*****************************************************************************
 * Copyright (C) NanoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the LICENSE.txt file.                                                     *
 *                                                                           *
 * Original code by Thomas Heller & Jacob Kjome                              *
 *****************************************************************************/
package org.nanocontainer.nanowar;

import org.picocontainer.injectors.FactoryInjector;
import org.picocontainer.PicoContainer;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.lang.reflect.Type;

/**
 * @author <a href="mailto:hoju@visi.com">Jacob Kjome</a>
 * @author Thomas Heller
 * @author Konstantin Pribluda
 */
public class ServletRequestContainerFilter implements Filter {
    private ServletContext context;

    private static ThreadLocal<HttpServletRequest> currentRequest = new ThreadLocal<HttpServletRequest>();
    private static ThreadLocal<HttpServletResponse> currentResponse = new ThreadLocal<HttpServletResponse>();
    private static ThreadLocal<HttpSession> currentSession = new ThreadLocal<HttpSession>();

    final static String ALREADY_FILTERED_KEY = "nanocontainer_request_filter_already_filtered";

    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        currentRequest.set(httpRequest);
        HttpServletResponse httpResponse = (HttpServletResponse) response;

        if (httpRequest.getAttribute(ALREADY_FILTERED_KEY) == null) {
            // we were not here, filter
            httpRequest.setAttribute(ALREADY_FILTERED_KEY, Boolean.TRUE);
            ServletRequestContainerLauncher launcher = new ServletRequestContainerLauncher(this.context, httpRequest);

            try {
                launcher.startContainer();
                chain.doFilter(httpRequest, httpResponse);
            } finally {
                launcher.killContainer();
            }


        } else {
            // do not filter, passthrough
            chain.doFilter(httpRequest, httpResponse);
        }
    }

    public void init(FilterConfig config) throws ServletException {
        this.context = config.getServletContext();
    }

    public void destroy() {
    }

    public static class SessionInjector extends FactoryInjector<HttpSession> {
        public HttpSession getComponentInstance(PicoContainer container, Type clazz) {
            return currentSession.get();
        }
    }

    public static class RequestInjector extends FactoryInjector<HttpServletRequest> {
        public HttpServletRequest getComponentInstance(PicoContainer container, Type clazz) {
            return currentRequest.get();
        }
    }

    public static class ResponseInjector extends FactoryInjector<HttpServletResponse> {
        public HttpServletResponse getComponentInstance(PicoContainer container, Type clazz) {
            return currentResponse.get();
        }
    }


}
