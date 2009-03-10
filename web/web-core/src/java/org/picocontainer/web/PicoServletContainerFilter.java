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
import java.util.logging.Logger;

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
import org.picocontainer.lifecycle.DefaultLifecycleState;
import org.picocontainer.adapters.AbstractAdapter;
import org.picocontainer.behaviors.Storing;

@SuppressWarnings("serial")
public abstract class PicoServletContainerFilter implements Filter, Serializable {

    private static final String APP_CONTAINER = ApplicationContainerHolder.class.getName();
    private static final String SESS_CONTAINER = SessionContainerHolder.class.getName();
    private static final String REQ_CONTAINER = RequestContainerHolder.class.getName();
    private boolean exposeServletInfrastructure;


    public void init(FilterConfig filterConfig) throws ServletException {
        Logger logger = Logger.getLogger(this.getClass().getName());
        logger.info("fi");

        ServletContext context = filterConfig.getServletContext();
        ApplicationContainerHolder ach = (ApplicationContainerHolder) context.getAttribute(APP_CONTAINER);
        setAppContainer(ach.getContainer());
        SessionContainerHolder sch = (SessionContainerHolder) context.getAttribute(SESS_CONTAINER);
        RequestContainerHolder rch = (RequestContainerHolder) context.getAttribute(REQ_CONTAINER);

        String exposeServletInfrastructureString = filterConfig.getInitParameter("exposeServletInfrastructure");
        if (exposeServletInfrastructureString == null || Boolean.parseBoolean(exposeServletInfrastructureString)) {
            exposeServletInfrastructure = true;
        }

        rch.getContainer().as(Characteristics.NO_CACHE).addAdapter(new HttpSessionInjector());
        rch.getContainer().as(Characteristics.NO_CACHE).addAdapter(new HttpServletRequestInjector());
        rch.getContainer().as(Characteristics.NO_CACHE).addAdapter(new HttpServletResponseInjector());

        initAdditionalScopedComponents(sch.getContainer(), rch.getContainer());
    }

    protected void initAdditionalScopedComponents(MutablePicoContainer sessionContainer, MutablePicoContainer reqContainer) {
    }

    public void destroy() {
    }

    public static Object getRequestComponentForThread(Class<?> type) {
        MutablePicoContainer requestContainer = ServletFilter.currentRequestContainer.get();
        MutablePicoContainer container = new DefaultPicoContainer(requestContainer);
        container.addComponent(type);
        return container.getComponent(type);
    }

    public void doFilter(ServletRequest req, ServletResponse resp, FilterChain filterChain) throws IOException, ServletException {
        Logger logger = Logger.getLogger(this.getClass().getName());
        logger.info("f0");

        try {
            HttpSession sess = ((HttpServletRequest) req).getSession();
            if (exposeServletInfrastructure) {
                session.set(sess);
                request.set(req);
                response.set(resp);
            }

            ServletContext context = sess.getServletContext();

            ApplicationContainerHolder ach = (ApplicationContainerHolder) context.getAttribute(APP_CONTAINER);
            SessionContainerHolder sch = (SessionContainerHolder) context.getAttribute(SESS_CONTAINER);
            RequestContainerHolder rch = (RequestContainerHolder) context.getAttribute(REQ_CONTAINER);

            Storing sessionStoring = sch.getStoring();
            Storing requestStoring = rch.getStoring();

            SessionStoreHolder ssh = (SessionStoreHolder) sess.getAttribute(SessionStoreHolder.class.getName());
            if (ssh == null) {
                if (sch.getContainer().getComponentAdapters().size() > 0) {
                    throw new PicoContainerWebException("Session not setup correctly.  There are components registered " +
                            "at the session level, but no working container to host them");
                }
                ssh = new SessionStoreHolder(sch.getStoring().getCacheForThread(),
                        new DefaultLifecycleState());

            }

            sessionStoring.putCacheForThread(ssh.getStoreWrapper());
            requestStoring.resetCacheForThread();
            rch.getLifecycleStateModel().resetStateModelForThread();
            rch.getContainer().start();

            setAppContainer(ach.getContainer());
            setSessionContainer(sch.getContainer());
            setRequestContainer(rch.getContainer());

            containersSetupForRequest(ach.getContainer(), sch.getContainer(), rch.getContainer(), req, resp);

            filterChain.doFilter(req, resp);

            setAppContainer(null);
            setSessionContainer(null);
            setRequestContainer(null);

            rch.getContainer().stop();
            rch.getContainer().dispose();
            rch.getLifecycleStateModel().invalidateStateModelForThread();
            sessionStoring.invalidateCacheForThread();
            requestStoring.invalidateCacheForThread();

            if (exposeServletInfrastructure) {
                session.set(null);
                request.set(null);
                response.set(null);
            }
        } catch (Throwable e) {
            logger.info("f1:"+e.getMessage());
            logger.info("f2:"+e.getClass().getName());

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
            currentAppContainer.set(container);
        }

        protected void setRequestContainer(MutablePicoContainer container) {
            currentRequestContainer.set(container);
        }

        protected void setSessionContainer(MutablePicoContainer container) {
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
