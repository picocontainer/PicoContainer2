/*****************************************************************************
 * Copyright (c) PicoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the LICENSE.txt file.                                                     *
 *                                                                           *
 *****************************************************************************/

package org.picocontainer.struts2;

import org.picocontainer.behaviors.Storing;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.servlet.ServletRequest;
import javax.servlet.ServletException;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletResponse;
import java.io.IOException;

/**
 * Filter to handle scoped Struts2 component managed by PicoContainer.
 * See http://picocontainer.org/struts2.html
 */
public class PicoStruts2Filter implements Filter {

    private final Storing sessionStoring = new Storing();
    private final Storing requestStoring = new Storing();

    // Apologies IoC fans, the only way we can pass info to the PicoStruts2ObjectFactory is this way.
    // We can't pass in instance, but we can pass it via a ThreadLocal because we do know the
    // action/bean/component lookup is going to be on the same invocation stack.
    static final ThreadLocal<Stores> localContext = new ThreadLocal<Stores>();

    private static final String STRUTS2_SESSION = "struts2-session";


    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {

        // set session and request stores for this invocation
        doSessionMagic(((HttpServletRequest) servletRequest).getSession());
        requestStoring.resetCacheForThread();
        // make the available for PicoStruts2ObjectFactory deeper into the call stack
        localContext.set(new Stores(sessionStoring, requestStoring));

        filterChain.doFilter(servletRequest, servletResponse);

        // set session and request stores for this invocation        
        sessionStoring.resetCacheForThread();
        requestStoring.resetCacheForThread();
    }

    private void doSessionMagic(HttpSession session) {
        synchronized (sessionStoring) {
            Storing.StoreWrapper sr = (Storing.StoreWrapper) session.getAttribute(STRUTS2_SESSION);
            if (sr != null) {
                sessionStoring.putCacheForThread(sr);
            } else {
                session.setAttribute(STRUTS2_SESSION, sessionStoring.resetCacheForThread());
            }
        }
    }

    public static Stores getStores() {
        return localContext.get();
    }

    public static class Stores {
        private final Storing sessionStoring;
        private final Storing requestStoring;

        public Stores(Storing sessionStoring, Storing requestStoring) {
            this.sessionStoring = sessionStoring;
            this.requestStoring = requestStoring;
        }

        public Storing session() {
            return sessionStoring;
        }

        public Storing request() {
            return requestStoring;
        }
    }

    // Not Needed

    public void init(FilterConfig filterConfig) throws ServletException {
    }

    public void destroy() {
    }


}
