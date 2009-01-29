/*******************************************************************************
 * Copyright (C) PicoContainer Organization. All rights reserved.
 * --------------------------------------------------------------------------
 * The software in this package is published under the terms of the BSD style
 * license a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 ******************************************************************************/
package org.picocontainer.web;

import java.io.Serializable;

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
import org.picocontainer.monitors.NullComponentMonitor;
import org.picocontainer.lifecycle.StartableLifecycleStrategy;
import org.picocontainer.containers.EmptyPicoContainer;
import org.picocontainer.behaviors.Caching;
import org.picocontainer.behaviors.Storing;
import org.picocontainer.behaviors.Guarding;

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
    private DefaultPicoContainer applicationContainer;
    private DefaultPicoContainer sessionContainer;
    private DefaultPicoContainer requestContainer;
    private Storing sessionStoring;
    private Storing requestStoring;
    private boolean useCompositionClass = true;

    /**
     * Default constructor used in webapp containers
     */
    public PicoServletContainerListener() {
    }

    /**
     * Creates a PicoServletContainerListener with dependencies injected
     * 
     * @param applicationContainer the application-scoped container
     * @param sessionContainer the session-scoped container
     * @param requestContainer the request-scoped container
     * @param sessionStoring the session storing behaviour
     * @param requestStoring the request storing behaviour
     */
    public PicoServletContainerListener(DefaultPicoContainer applicationContainer,
            DefaultPicoContainer sessionContainer, DefaultPicoContainer requestContainer, Storing sessionStoring,
            Storing requestStoring) {
        this.applicationContainer = applicationContainer;
        this.sessionContainer = sessionContainer;
        this.requestContainer = requestContainer;
        this.sessionStoring = sessionStoring;
        this.requestStoring = requestStoring;
        useCompositionClass = false;
    }

    protected PicoContainer makeParentContainer() {
        return new EmptyPicoContainer();
    }

    public void contextInitialized(final ServletContextEvent event) {

        ScopedContainers scopedContainers = makeScopedContainers();
        applicationContainer = scopedContainers.applicationContainer;
        sessionContainer = scopedContainers.sessionContainer;
        requestContainer = scopedContainers.requestContainer;
        sessionStoring = scopedContainers.sessionStoring;
        requestStoring = scopedContainers.requestStoring;

        ServletContext context = event.getServletContext();
        applicationContainer.setName("application");

        context.setAttribute(ApplicationContainerHolder.class.getName(), new ApplicationContainerHolder(
                applicationContainer));

        sessionContainer.setName("session");
        ThreadLocalLifecycleState sessionStateModel = new ThreadLocalLifecycleState();
        sessionContainer.setLifecycleState(sessionStateModel);

        SessionContainerHolder sch = new SessionContainerHolder(sessionContainer, sessionStoring, sessionStateModel) ;
        context.setAttribute(SessionContainerHolder.class.getName(), sch);

        requestContainer.setName("request");
        ThreadLocalLifecycleState requestStateModel = new ThreadLocalLifecycleState();
        requestContainer.setLifecycleState(requestStateModel);

        context.setAttribute(RequestContainerHolder.class.getName(), new RequestContainerHolder(requestContainer,
                requestStoring, requestStateModel));

        if (useCompositionClass) {
            compose(loadComposer(context), context);
        }
        applicationContainer.start();
    }

    /**
     * Overide this method if you need a more specialized container tree.
     * Here is the default block of code for this -
     *
     *     DefaultPicoContainer appCtnr = new DefaultPicoContainer(new Caching(), makeParentContainer());
     *     Storing sessStoring = new Storing();
     *     DefaultPicoContainer sessCtnr = new DefaultPicoContainer(sessStoring, appCtnr);
     *     Storing reqStoring = new Storing();
     *     DefaultPicoContainer reqCtnr = new DefaultPicoContainer(reqStoring, sessCtnr);
     *     return new ScopedContainers(appCtnr,sessCtnr,reqCtnr,sessStoring,reqStoring);
     *
     * @return an instance of ScopedContainers
     */
    protected ScopedContainers makeScopedContainers() {
        DefaultPicoContainer appCtnr = new DefaultPicoContainer(new Guarding().wrap(new Caching()), makeLifecycleStrategy(), makeParentContainer(), makeAppComponentMonitor());
        Storing sessStoring = new Storing();
        DefaultPicoContainer sessCtnr = new DefaultPicoContainer(new Guarding().wrap(sessStoring), makeLifecycleStrategy(), appCtnr, makeSessionComponentMonitor());
        Storing reqStoring = new Storing();
        DefaultPicoContainer reqCtnr = new DefaultPicoContainer(new Guarding().wrap(addRequestBehaviors(reqStoring)), makeLifecycleStrategy(), sessCtnr, makeRequestComponentMonitor());
        return new ScopedContainers(appCtnr, sessCtnr, reqCtnr, sessStoring, reqStoring);
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

    protected BehaviorFactory addRequestBehaviors(BehaviorFactory reqStoring) {
        return reqStoring;
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

    protected void compose(WebappComposer composer, ServletContext context) {
        composer.composeApplication(applicationContainer, context);
        composer.composeSession(sessionContainer);
        composer.composeRequest(requestContainer);
    }

    public void contextDestroyed(ServletContextEvent event) {
        applicationContainer.stop();
        applicationContainer.dispose();
    }

    public void sessionCreated(HttpSessionEvent event) {
        HttpSession session = event.getSession();
        ServletContext context = session.getServletContext();

        SessionContainerHolder sch = (SessionContainerHolder) context.getAttribute(SessionContainerHolder.class
                .getName());
        ThreadLocalLifecycleState tlLifecycleState = sch.getLifecycleStateModel();
        session.setAttribute(SessionStoreHolder.class.getName(), new SessionStoreHolder(sessionStoring
                .resetCacheForThread(), tlLifecycleState.resetStateModelForThread()));

        sessionContainer.start();
    }

    public void sessionDestroyed(HttpSessionEvent event) {
        HttpSession session = event.getSession();
        ServletContext context = session.getServletContext();

        SessionStoreHolder ssh = (SessionStoreHolder) session.getAttribute(SessionStoreHolder.class.getName());

        SessionContainerHolder sch = (SessionContainerHolder) context.getAttribute(SessionContainerHolder.class
                .getName());
        ThreadLocalLifecycleState tlLifecycleState = sch.getLifecycleStateModel();

        sessionStoring.putCacheForThread(ssh.getStoreWrapper());
        tlLifecycleState.putLifecycleStateModelForThread(ssh.getDefaultLifecycleState());

        sessionContainer.stop();
        sessionContainer.dispose();
        sessionStoring.invalidateCacheForThread();
    }

    public static class ScopedContainers {

        private DefaultPicoContainer applicationContainer;
        private DefaultPicoContainer sessionContainer;
        private DefaultPicoContainer requestContainer;
        private Storing sessionStoring;
        private Storing requestStoring;

        public ScopedContainers(DefaultPicoContainer applicationContainer, DefaultPicoContainer sessionContainer, DefaultPicoContainer requestContainer, Storing sessionStoring, Storing requestStoring) {
            this.applicationContainer = applicationContainer;
            this.sessionContainer = sessionContainer;
            this.requestContainer = requestContainer;
            this.sessionStoring = sessionStoring;
            this.requestStoring = requestStoring;
        }
    }


}
