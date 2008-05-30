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
import org.picocontainer.behaviors.Caching;
import org.picocontainer.behaviors.Storing;

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
    private boolean useCompositionClass;

    /**
     * Default constructor used in webapp containers
     */
    public PicoServletContainerListener() {
        applicationContainer = new DefaultPicoContainer(new Caching());
        sessionStoring = new Storing();
        sessionContainer = new DefaultPicoContainer(sessionStoring, applicationContainer);
        requestStoring = new Storing();
        requestContainer = new DefaultPicoContainer(requestStoring, sessionContainer);
        useCompositionClass = true;
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

    public void contextInitialized(final ServletContextEvent event) {

        ServletContext context = event.getServletContext();
        applicationContainer.setName("application");

        context.setAttribute(ApplicationContainerHolder.class.getName(), new ApplicationContainerHolder(
                applicationContainer));

        sessionContainer.setName("session");
        ThreadLocalLifecycleState sessionStateModel = new ThreadLocalLifecycleState();
        sessionContainer.setLifecycleState(sessionStateModel);

        context.setAttribute(SessionContainerHolder.class.getName(), new SessionContainerHolder(sessionContainer,
                sessionStoring, sessionStateModel));

        requestContainer.setName("request");
        ThreadLocalLifecycleState requestStateModel = new ThreadLocalLifecycleState();
        requestContainer.setLifecycleState(requestStateModel);

        context.setAttribute(RequestContainerHolder.class.getName(), new RequestContainerHolder(requestContainer,
                requestStoring, requestStateModel));

        if (useCompositionClass) {
            compose(loadComposer(context));
        }
        applicationContainer.start();
    }

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

    protected void compose(WebappComposer composer) {
        composer.composeApplication(applicationContainer);
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

}
