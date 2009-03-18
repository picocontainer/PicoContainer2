/*******************************************************************************
 * Copyright (C) PicoContainer Organization. All rights reserved.
 * --------------------------------------------------------------------------
 * The software in this package is published under the terms of the BSD style
 * license a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 ******************************************************************************/
package org.picocontainer.web;

import java.io.IOException;
import java.io.Serializable;
import java.lang.reflect.Type;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpServletResponse;

import org.picocontainer.DefaultPicoContainer;
import org.picocontainer.MutablePicoContainer;
import org.picocontainer.Characteristics;
import org.picocontainer.PicoContainer;
import org.picocontainer.PicoCompositionException;
import static org.picocontainer.web.PicoServletContainerListener.*;
import org.picocontainer.lifecycle.DefaultLifecycleState;
import org.picocontainer.adapters.AbstractAdapter;
import org.picocontainer.behaviors.Storing;

@SuppressWarnings("serial")
public abstract class PicoServletContainerFilter implements Filter, Serializable {

    private boolean exposeServletInfrastructure;

    public void init(FilterConfig filterConfig) throws ServletException {
        ServletContext context = filterConfig.getServletContext();
        ScopedContainers scopedContainers = getScopedContainers(context);
        setAppContainer(scopedContainers.getApplicationContainer());

        String exposeServletInfrastructureString = filterConfig.getInitParameter("exposeServletInfrastructure");
        if (exposeServletInfrastructureString == null || Boolean.parseBoolean(exposeServletInfrastructureString)) {
            exposeServletInfrastructure = true;
        }

        scopedContainers.getRequestContainer().as(Characteristics.NO_CACHE).addAdapter(new HttpSessionInjector());
        scopedContainers.getRequestContainer().as(Characteristics.NO_CACHE).addAdapter(new HttpServletRequestInjector());
        scopedContainers.getRequestContainer().as(Characteristics.NO_CACHE).addAdapter(new HttpServletResponseInjector());

        initAdditionalScopedComponents(scopedContainers.getSessionContainer(), scopedContainers.getRequestContainer());
    }

    public void destroy() {
    }

    private ScopedContainers getScopedContainers(ServletContext context) {
        return (ScopedContainers) context.getAttribute(ScopedContainers.class.getName());
    }

    protected void initAdditionalScopedComponents(MutablePicoContainer sessionContainer, MutablePicoContainer reqContainer) {
    }

    public static Object getRequestComponentForThread(Class<?> type) {
        MutablePicoContainer requestContainer = ServletFilter.currentRequestContainer.get();
        MutablePicoContainer container = new DefaultPicoContainer(requestContainer);
        container.addComponent(type);
        return container.getComponent(type);
    }

    public void doFilter(ServletRequest req, ServletResponse resp, FilterChain filterChain) throws IOException, ServletException {

        HttpSession sess = ((HttpServletRequest) req).getSession();
        if (exposeServletInfrastructure) {
            session.set(sess);
            request.set(req);
            response.set(resp);
        }

        ServletContext context = sess.getServletContext();

        ScopedContainers scopedContainers = getScopedContainers(context);

        SessionStoreHolder ssh = (SessionStoreHolder) sess.getAttribute(SessionStoreHolder.class.getName());
        if (ssh == null) {
            if (scopedContainers.getSessionContainer().getComponentAdapters().size() > 0) {
                throw new PicoContainerWebException("Session not setup correctly.  There are components registered " +
                        "at the session level, but no working container to host them");
            }
            ssh = new SessionStoreHolder(scopedContainers.getSessionStoring().getCacheForThread(), new DefaultLifecycleState());
        }

        scopedContainers.getSessionStoring().putCacheForThread(ssh.getStoreWrapper());
        scopedContainers.getSessionState().putLifecycleStateModelForThread(ssh.getLifecycleState());

        scopedContainers.getRequestStoring().resetCacheForThread();
        scopedContainers.getRequestState().resetStateModelForThread();

        scopedContainers.getRequestContainer().start();

        setAppContainer(scopedContainers.getApplicationContainer());
        setSessionContainer(scopedContainers.getSessionContainer());
        setRequestContainer(scopedContainers.getRequestContainer());

        containersSetupForRequest(scopedContainers.getApplicationContainer(), scopedContainers.getSessionContainer(), scopedContainers.getRequestContainer(), req, resp);

        filterChain.doFilter(req, resp);

        setAppContainer(null);
        setSessionContainer(null);
        setRequestContainer(null);

        scopedContainers.getRequestContainer().stop();
        scopedContainers.getRequestContainer().dispose();

        scopedContainers.getRequestStoring().invalidateCacheForThread();
        scopedContainers.getRequestState().invalidateStateModelForThread();

        scopedContainers.getSessionStoring().invalidateCacheForThread();
        scopedContainers.getSessionState().invalidateStateModelForThread();

        if (exposeServletInfrastructure) {
            session.set(null);
            request.set(null);
            response.set(null);
        }


    }

    protected void containersSetupForRequest(MutablePicoContainer appcontainer, MutablePicoContainer sessionContainer,
                                             MutablePicoContainer requestContainer, ServletRequest req, ServletResponse resp) {
    }

    private static ThreadLocal<HttpSession> session = new ThreadLocal<HttpSession>();
    private static ThreadLocal<ServletRequest> request = new ThreadLocal<ServletRequest>();
    private static ThreadLocal<ServletResponse> response = new ThreadLocal<ServletResponse>();

    protected abstract void setAppContainer(MutablePicoContainer container);

    protected abstract void setSessionContainer(MutablePicoContainer container);

    protected abstract void setRequestContainer(MutablePicoContainer container);

    public static class ServletFilter extends PicoServletContainerFilter {

        private static ThreadLocal<MutablePicoContainer> currentRequestContainer = new ThreadLocal<MutablePicoContainer>();
        private static ThreadLocal<MutablePicoContainer> currentSessionContainer = new ThreadLocal<MutablePicoContainer>();
        private static ThreadLocal<MutablePicoContainer> currentAppContainer = new ThreadLocal<MutablePicoContainer>();

        protected void setAppContainer(MutablePicoContainer container) {
            if (currentRequestContainer == null) {
                currentRequestContainer = new ThreadLocal<MutablePicoContainer>();
            }
            currentAppContainer.set(container);
        }

        protected void setRequestContainer(MutablePicoContainer container) {
            if (currentRequestContainer == null) {
                currentRequestContainer = new ThreadLocal<MutablePicoContainer>();
            }
            currentRequestContainer.set(container);
        }

        protected void setSessionContainer(MutablePicoContainer container) {
            if (currentSessionContainer == null) {
                currentSessionContainer = new ThreadLocal<MutablePicoContainer>();
            }
            currentSessionContainer.set(container);
        }
    }

    public static class HttpSessionInjector extends AbstractAdapter {

        public HttpSessionInjector() {
            super(HttpSession.class, HttpSession.class);
        }

        public Object getComponentInstance(PicoContainer picoContainer, Type type) throws PicoCompositionException {
            return session.get();
        }

        public void verify(PicoContainer picoContainer) throws PicoCompositionException {
        }

        public String getDescriptor() {
            return "HttpSessionInjector";
        }
    }

    public static class HttpServletRequestInjector extends AbstractAdapter {

        public HttpServletRequestInjector() {
            super(HttpServletRequest.class, HttpServletRequest.class);
        }

        public Object getComponentInstance(PicoContainer picoContainer, Type type) throws PicoCompositionException {
            return request.get();
        }

        public void verify(PicoContainer picoContainer) throws PicoCompositionException {
        }

        public String getDescriptor() {
            return "HttpServletRequestInjector";
        }
    }

    public static class HttpServletResponseInjector extends AbstractAdapter {

        public HttpServletResponseInjector() {
            super(HttpServletResponse.class, HttpServletResponse.class);
        }

        public Object getComponentInstance(PicoContainer picoContainer, Type type) throws PicoCompositionException {
            return response.get();
        }

        public void verify(PicoContainer picoContainer) throws PicoCompositionException {
        }

        public String getDescriptor() {
            return "HttpServletResponseInjector";
        }
    }
}
