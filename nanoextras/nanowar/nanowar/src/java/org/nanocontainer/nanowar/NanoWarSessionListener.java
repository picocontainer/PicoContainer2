/*****************************************************************************
 * Copyright (C) NanoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the LICENSE.txt file.                                                     *
 *                                                                           *
 *****************************************************************************/

package org.nanocontainer.nanowar;

import javax.servlet.http.HttpSessionListener;
import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSession;
import org.picocontainer.ObjectReference;
import org.picocontainer.behaviors.CachingBehavior;

import org.nanocontainer.integrationkit.ContainerBuilder;
import javax.servlet.http.HttpSessionBindingEvent;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpSessionBindingListener;
import java.io.Serializable;

/**
 * Servlet listener class that hooks into the underlying servlet
 * container and instantiates, assembles, starts, stores and
 * disposes the appropriate pico containers when sessions start/stop.
 * <p>
 * To use, simply add as a listener to web.xml the listener-class
 * <code>org.nanocontainer.nanowar.NanoWarSessionListener</code>.
 * </p>
 * <p>
 * The containers are configured via context-params in web.xml, in two ways:
 * <ol>
 *   <li>A NanoContainer script via a parameter whose name is nanocontainer.<language>,
 *       where <language> is one of the supported scripting languages,
 *       see {@link org.nanocontainer.script.ScriptedContainerBuilderFactory ScriptedContainerBuilderFactory}.
 *       The parameter value can be either an inlined script (enclosed in <![CDATA[]>), or a resource path for
 *       the script (relative to the webapp context).
 *   </li>
 *   <li>A ContainerComposer class via the parameter name
 *   {@link KeyConstants#CONTAINER_COMPOSER CONTAINER_COMPOSER},
 *   which can be configured via an optional parameter
 *   {@link KeyConstants#CONTAINER_COMPOSER_CONFIGURATION CONTAINER_COMPOSER_CONFIGURATION}.
 *   </li>
 * </ol>
 * </p>
 * <p><b>Note:</b> if one is interested in both application-scoped and session-scoped components, the 
 * {@link org.nanocontainer.nanowar.ServletContextListener} should be configured in the web.xml.
 * </p>
 * <p><strong>Warning:</strong> session-level containers can be problematic on
 * many fronts including persisted sessions, clustering, etc.
 * </p>
 * @see org.nanocontainer.nanowar.NanoWarContextListener
 * @see org.nanocontainer.nanowar.ServletContainerListener
 * @author Michael Rimov
 * @author Joe Walnes
 * @author Aslak Helles&oslash;y
 * @author Philipp Meier
 * @author Paul Hammant
 * @author Mauro Talevi
 * @author Konstantin Pribluda
 */
public class NanoWarSessionListener extends AbstractNanoWarListener implements HttpSessionListener, KeyConstants {

    private ContainerBuilder getBuilder(ServletContext context) {
        ObjectReference assemblerRef = new ApplicationScopeReference(context, BUILDER);
        return (ContainerBuilder) assemblerRef.get();
    }

    public void sessionCreated(HttpSessionEvent event) {
        HttpSession session = event.getSession();
        ServletContext context = session.getServletContext();
        ContainerBuilder containerBuilder = getBuilder(context);
        ObjectReference sessionContainerRef = new SessionScopeReference(session, SESSION_CONTAINER);
        ObjectReference webappContainerRef = new ApplicationScopeReference(context, APPLICATION_CONTAINER);
        containerBuilder.buildContainer(sessionContainerRef, webappContainerRef, session, false);

        session.setAttribute(KILLER_HELPER, new SessionContainerKillerHelper() {
            public void valueBound(HttpSessionBindingEvent bindingEvent) {
                HttpSession session = bindingEvent.getSession();
                containerRef = new CachingBehavior.SimpleReference();
                containerRef.set(new SessionScopeReference(session, SESSION_CONTAINER).get());
            }

            public void valueUnbound(HttpSessionBindingEvent event) {
                try {
                    killContainer(containerRef);
                } catch (IllegalStateException e) {
                    //
                    //Some servlet containers (Jetty) call contextDestroyed(ServletContextEvent event)
                    //and then afterwards call valueUnbound(HttpSessionBindingEvent event).

                    //contextDestroyed will kill the top level (app level) pico container which will
                    //cascade stop() down to the session children.

                    //This means that when valueUnbound is called later, the session level container will
                    //already be stopped.
                    //
                }
            }
        });
    }

    public void sessionDestroyed(HttpSessionEvent se) {
        // no implementation - session scoped container killed by SessionContainerKillerHelper
    }

    private abstract class SessionContainerKillerHelper implements HttpSessionBindingListener, Serializable {
        transient CachingBehavior.SimpleReference containerRef;
    }
}
