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

import org.picocontainer.MutablePicoContainer;
import org.picocontainer.behaviors.Storing;

@SuppressWarnings("serial")
public class PicoServletContainerFilter implements Filter, Serializable {

    public void init(FilterConfig filterConfig) throws ServletException {
    }

    public void destroy() {
    }

    private static ThreadLocal<MutablePicoContainer> currentRequestContainer = new ThreadLocal<MutablePicoContainer>();

    public static MutablePicoContainer getRequestContainerForThread() {
        return currentRequestContainer.get();
    }

    private static ThreadLocal<MutablePicoContainer> currentSessionContainer = new ThreadLocal<MutablePicoContainer>();

    public static MutablePicoContainer getSessionContainerForThread() {
        return currentSessionContainer.get();
    }

    private static ThreadLocal<MutablePicoContainer> currentAppContainer = new ThreadLocal<MutablePicoContainer>();

    public static MutablePicoContainer getApplicationContainerForThread() {
        return currentAppContainer.get();
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

        currentAppContainer.set(ach.getContainer());
        currentSessionContainer.set(sch.getContainer());
        currentRequestContainer.set(rch.getContainer());

        filterChain.doFilter(req, resp);

        currentAppContainer.set(null);
        currentSessionContainer.set(null);
        currentRequestContainer.set(null);

        rch.getContainer().stop();
        rch.getContainer().dispose();
        rch.getLifecycleStateModel().invalidateStateModelForThread();
        sessionStoring.invalidateCacheForThread();
        requestStoring.invalidateCacheForThread();

    }

}
