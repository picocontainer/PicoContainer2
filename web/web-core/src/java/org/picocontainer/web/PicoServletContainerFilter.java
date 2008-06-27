package org.picocontainer.web;

import java.io.IOException;
import java.io.Serializable;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.picocontainer.DefaultPicoContainer;
import org.picocontainer.MutablePicoContainer;
import org.picocontainer.behaviors.Storing;

@SuppressWarnings("serial")
public abstract class PicoServletContainerFilter implements Filter, Serializable {

    public void init(FilterConfig filterConfig) throws ServletException {
        ServletContext servletContext = filterConfig.getServletContext();
        ApplicationContainerHolder ach = (ApplicationContainerHolder) servletContext
                .getAttribute(ApplicationContainerHolder.class.getName());
        setAppContainer(ach.getContainer());
    }

    public void destroy() {
    }

    public static Object getRequestComponentForThread(Class<?> type) {
        MutablePicoContainer requestContainer = ServletFilter.getRequestContainerForThread();
        MutablePicoContainer container = new DefaultPicoContainer(requestContainer);
        container.addComponent(type);
        return container.getComponent(type);
    }

    public void doFilter(ServletRequest req, ServletResponse resp, FilterChain filterChain) throws IOException,
            ServletException {

        HttpServletRequest httpReq = (HttpServletRequest) req;
        HttpSession session = httpReq.getSession();
        ServletContext context = session.getServletContext();

        ApplicationContainerHolder ach = (ApplicationContainerHolder) context
                .getAttribute(ApplicationContainerHolder.class.getName());
        SessionContainerHolder sch = (SessionContainerHolder) context.getAttribute(SessionContainerHolder.class
                .getName());
        RequestContainerHolder rch = (RequestContainerHolder) context.getAttribute(RequestContainerHolder.class
                .getName());

        Storing sessionStoring = sch.getStoring();
        Storing requestStoring = rch.getStoring();

        SessionStoreHolder ssh = (SessionStoreHolder) session.getAttribute(SessionStoreHolder.class.getName());
        sessionStoring.putCacheForThread(ssh.getStoreWrapper());
        requestStoring.resetCacheForThread();
        rch.getLifecycleStateModel().resetStateModelForThread();
        rch.getContainer().start();

        setAppContainer(ach.getContainer());
        setSessionContainer(sch.getContainer());
        setRequestContainer(rch.getContainer());

        filterChain.doFilter(req, resp);

        setAppContainer(null);
        setSessionContainer(null);
        setRequestContainer(null);

        rch.getContainer().stop();
        rch.getContainer().dispose();
        rch.getLifecycleStateModel().invalidateStateModelForThread();
        sessionStoring.invalidateCacheForThread();
        requestStoring.invalidateCacheForThread();

    }

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

        public static MutablePicoContainer getRequestContainerForThread() {
            return currentRequestContainer.get();
        }

        public static MutablePicoContainer getSessionContainerForThread() {
            return currentSessionContainer.get();
        }

        public static MutablePicoContainer getApplicationContainerForThread() {
            return currentAppContainer.get();
        }
    }
}
