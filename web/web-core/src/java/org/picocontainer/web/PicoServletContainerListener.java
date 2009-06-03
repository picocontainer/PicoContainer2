/*******************************************************************************
 * Copyright (C) PicoContainer Organization. All rights reserved.
 * --------------------------------------------------------------------------
 * The software in this package is published under the terms of the BSD style
 * license a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 ******************************************************************************/
package org.picocontainer.web;

import java.io.Serializable;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.List;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;

import org.picocontainer.DefaultPicoContainer;
import org.picocontainer.PicoCompositionException;
import org.picocontainer.PicoContainer;
import org.picocontainer.BehaviorFactory;
import org.picocontainer.LifecycleStrategy;
import org.picocontainer.ComponentMonitor;
import org.picocontainer.ComponentAdapter;
import org.picocontainer.NameBinding;
import org.picocontainer.PicoException;
import org.picocontainer.injectors.ConstructorInjection;
import org.picocontainer.monitors.NullComponentMonitor;
import org.picocontainer.lifecycle.StartableLifecycleStrategy;
import org.picocontainer.containers.EmptyPicoContainer;
import org.picocontainer.containers.AbstractDelegatingPicoContainer;
import org.picocontainer.behaviors.Storing;
import org.picocontainer.behaviors.Guarding;
import org.picocontainer.behaviors.Caching;

/**
 * Servlet listener class that hooks into the underlying servlet container and
 * instantiates, assembles, starts, stores and disposes the appropriate pico
 * containers when applications/sessions start/stop.
 * <p>
 * To use, simply add as a listener to the web.xml the listener-class
 * 
 * <pre>
 * &lt;listener&gt;
 *  &lt;listener-class&gt;org.picocontainer.web.PicoServletContainerListener&lt;/listener-class&gt;
 * &lt;/listener&gt; 
 * </pre>
 * 
 * </p>
 * <p>
 * The listener also requires a the class name of the
 * {@link org.picocontainer.web.WebappComposer} as a context-param in web.xml:
 * 
 * <pre>
 *  &lt;context-param&gt;
 *   &lt;param-name&gt;webapp-composer-class&lt;/param-name&gt;
 *   &lt;param-value&gt;com.company.MyWebappComposer&lt;/param-value&gt;
 *  &lt;/context-param&gt;
 * </pre>
 * 
 * The composer will be used to compose the components for the different webapp
 * scopes after the context has been initialised.
 * </p>
 * 
 * @author Joe Walnes
 * @author Aslak Helles&oslash;y
 * @author Philipp Meier
 * @author Paul Hammant
 * @author Mauro Talevi
 * @author Konstantin Pribluda
 */
@SuppressWarnings("serial")
public class PicoServletContainerListener implements ServletContextListener, HttpSessionListener, Serializable {

    public static final String WEBAPP_COMPOSER_CLASS = "webapp-composer-class";
    
    /**
     * Default constructor used in webapp containers
     */
    public PicoServletContainerListener() {
    }

    public void contextInitialized(final ServletContextEvent event) {

        ServletContext context = event.getServletContext();

        ScopedContainers scopedContainers = makeScopedContainers();

        scopedContainers.getApplicationContainer().setName("application");
        scopedContainers.getSessionContainer().setName("session");
        scopedContainers.getRequestContainer().setName("request");

        compose(loadComposer(context), context, scopedContainers);

        scopedContainers.getApplicationContainer().start();

        context.setAttribute(ScopedContainers.class.getName(), scopedContainers);
    }

    /**
     * Overide this method if you need a more specialized container tree.
     * Here is the default block of code for this -
     *
     *   DefaultPicoContainer appCtnr = new DefaultPicoContainer(new Guarding().wrap(new Caching()), makeLifecycleStrategy(), makeParentContainer(), makeAppComponentMonitor());
     *   Storing sessStoring = new Storing();
     *   DefaultPicoContainer sessCtnr = new DefaultPicoContainer(new Guarding().wrap(sessStoring), makeLifecycleStrategy(), appCtnr, makeSessionComponentMonitor());
     *   Storing reqStoring = new Storing();
     *   DefaultPicoContainer reqCtnr = new DefaultPicoContainer(new Guarding().wrap(addRequestBehaviors(reqStoring)), makeLifecycleStrategy(), sessCtnr, makeRequestComponentMonitor());
     *   ThreadLocalLifecycleState sessionState = new ThreadLocalLifecycleState();
     *   ThreadLocalLifecycleState requestState = new ThreadLocalLifecycleState();
     *
     *   return new ScopedContainers(appCtnr, sessCtnr, reqCtnr, sessStoring, reqStoring, sessionState, requestState);
     * @return an instance of ScopedContainers
     */
    protected ScopedContainers makeScopedContainers() {
        DefaultPicoContainer appCtnr = new DefaultPicoContainer(new Guarding().wrap(new Caching()), makeLifecycleStrategy(), makeParentContainer(), makeAppComponentMonitor());
        Storing sessStoring = new Storing();
        DefaultPicoContainer sessCtnr = new DefaultPicoContainer(new Guarding().wrap(sessStoring), makeLifecycleStrategy(), appCtnr, makeSessionComponentMonitor());
        Storing reqStoring = new Storing();
        DefaultPicoContainer reqCtnr = new DefaultPicoContainer(new Guarding().wrap(addRequestBehaviors(reqStoring)), makeLifecycleStrategy(), sessCtnr, makeRequestComponentMonitor());
        ThreadLocalLifecycleState sessionState = new ThreadLocalLifecycleState();
        ThreadLocalLifecycleState requestState = new ThreadLocalLifecycleState();
        sessCtnr.setLifecycleState(sessionState);
        reqCtnr.setLifecycleState(requestState);

        return new ScopedContainers(appCtnr, sessCtnr, reqCtnr, sessStoring, reqStoring, sessionState, requestState);
    }

    protected PicoContainer makeParentContainer() {
        return new EmptyPicoContainer();
    }

    protected LifecycleStrategy makeLifecycleStrategy() {
        return new StartableLifecycleStrategy(makeRequestComponentMonitor());
    }

    protected ComponentMonitor makeAppComponentMonitor() {
        return new NullComponentMonitor();
    }

    protected ComponentMonitor makeSessionComponentMonitor() {
        return new NullComponentMonitor();
    }

    protected ComponentMonitor makeRequestComponentMonitor() {
        return new NullComponentMonitor();
    }

    protected BehaviorFactory addRequestBehaviors(BehaviorFactory beforeThisBehaviorFactory) {
        return beforeThisBehaviorFactory;
    }

    /**
     * Get the class to do compostition with - from a "webapp-composer-class" config param
     * from web.xml :
     *
     *   <context-param>
     *       <param-name>webapp-composer-class</param-name>
     *       <param-value>com.yourcompany.YourWebappComposer</param-value>
     *   </context-param>
     *
     * @param context
     * @return
     */
    protected WebappComposer loadComposer(ServletContext context) {
        String composerClassName = context.getInitParameter(WEBAPP_COMPOSER_CLASS);
        try {
            return (WebappComposer) Thread.currentThread().getContextClassLoader().loadClass(composerClassName)
                    .newInstance();
        } catch (Exception e) {
            throw new PicoCompositionException("Failed to load webapp composer class " + composerClassName
                    + ": ensure the context-param '" + WEBAPP_COMPOSER_CLASS + "' is configured in the web.xml.", e);
        }
    }

    protected void compose(WebappComposer composer, ServletContext context, ScopedContainers scopedContainers) {
        composer.composeApplication(scopedContainers.getApplicationContainer(), context);
        composer.composeSession(scopedContainers.getSessionContainer());
        composer.composeRequest(scopedContainers.getRequestContainer());
    }

    public void contextDestroyed(ServletContextEvent event) {
        ScopedContainers scopedContainers = getScopedContainers(event.getServletContext());
        scopedContainers.getApplicationContainer().stop();
        scopedContainers.getApplicationContainer().dispose();
    }

    private ScopedContainers getScopedContainers(ServletContext context) {
        return (ScopedContainers) context.getAttribute(ScopedContainers.class.getName());
    }

    public void sessionCreated(HttpSessionEvent event) {

        HttpSession session = event.getSession();
        ScopedContainers scopedContainers = getScopedContainers(session.getServletContext());

        SessionStoreHolder ssh = new SessionStoreHolder(scopedContainers.getSessionStoring().resetCacheForThread(), scopedContainers.getSessionState().resetStateModelForThread());

        scopedContainers.getSessionContainer().start();
        session.setAttribute(SessionStoreHolder.class.getName(), ssh);

    }

    public void sessionDestroyed(HttpSessionEvent event) {
        HttpSession session = event.getSession();
        ScopedContainers scopedContainers = getScopedContainers(session.getServletContext());

        SessionStoreHolder ssh = (SessionStoreHolder) session.getAttribute(SessionStoreHolder.class.getName());

        scopedContainers.getSessionStoring().putCacheForThread(ssh.getStoreWrapper());
        scopedContainers.getSessionState().putLifecycleStateModelForThread(ssh.getLifecycleState());

        scopedContainers.getSessionContainer().stop();
        scopedContainers.getSessionContainer().dispose();

        scopedContainers.getSessionStoring().invalidateCacheForThread();
        scopedContainers.getSessionState().invalidateStateModelForThread();
        
        session.setAttribute(SessionStoreHolder.class.getName(), null);
    }

}
