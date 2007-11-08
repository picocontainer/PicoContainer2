/*****************************************************************************
 * Copyright (C) NanoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the LICENSE.txt file.                                                     *
 *                                                                           *
 *****************************************************************************/
package org.nanocontainer.nanowar;

import java.io.Serializable;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;

/**
 * Servlet listener class that hooks into the underlying servlet
 * container and instantiates, assembles, starts, stores and
 * disposes the appropriate pico containers when
 * applications/sessions start/stop.
 * <p>
 * To use, simply add as a listener to web.xml the listener-class
 * <code>org.nanocontainer.nanowar.ServletContainerListener</code>.
 * </p>
 * <p>
 * The containers are configured via context-params in web.xml, in two ways:
 * <ol>
 * 	 <li>A NanoContainer script via a parameter whose name is nanocontainer.<language>,
 *       where <language> is one of the supported scripting languages,
 *       see {@link org.nanocontainer.script.ScriptedContainerBuilderFactory ScriptedContainerBuilderFactory}.
 *       The parameter value can be either an inlined script (enclosed in <![CDATA[]>), or a resource path for
 * 	  	 the script (relative to the webapp context).
 *   </li>
 *   <li>A ContainerComposer class via the parameter name
 *   {@link KeyConstants#CONTAINER_COMPOSER CONTAINER_COMPOSER},
 * 	 which can be configured via an optional parameter
 *   {@link KeyConstants#CONTAINER_COMPOSER_CONFIGURATION CONTAINER_COMPOSER_CONFIGURATION}.
 *   </li>
 * </ol>
 * </p>
 * <p>
 *  To allow external configurability of application (which is quite usefull in 
 *  and often required in big environments) you can add 2  containers to hierarchy
 *  <dl>
 *  	<dt>SystemPropertiesContainer</dt>
 *  	<dd>
 *  		System properties container exposes system properties
 *          obtainable through <code>System.getProperties()</code>
 *          to components in lower level containers so they have possibility
 *          to depend on them this allowing external configuration. To
 *          activate system properties you have to pass any value to 
 *          {@link KeyConstants#SYSTEM_PROPERTIES_CONTAINER SYSTEM_PROPERTIES_CONTAINER}
 *      </dd>
 *  </dl>
 * </p>
 * <p>
 * <b>Note:</b> This listener simply delegates to {@link org.nanocontainer.nanowar.NanoWarContextListener} and 
 * {@link org.nanocontainer.nanowar.NanoWarSessionListener}.  The application-level listeners can also be 
 * configured separately in web.xml if one is interested in application-scoped components only or has 
 * issues with session-scoped components (serialization, clustering, etc).
 * </p>
 * @see org.nanocontainer.nanowar.NanoWarContextListener
 * @see org.nanocontainer.nanowar.NanoWarSessionListener
 * @author Joe Walnes
 * @author Aslak Helles&oslash;y
 * @author Philipp Meier
 * @author Paul Hammant
 * @author Mauro Talevi
 * @author Konstantin Pribluda
 */
@SuppressWarnings("serial")
public final class ServletContainerListener implements ServletContextListener, HttpSessionListener, KeyConstants, Serializable {

    /**
     * Implementation of the context listener.
     */
    private final NanoWarContextListener contextListener = new NanoWarContextListener();

    /**
     * Implementation of the session listener.
     */
    private final NanoWarSessionListener sessionListener = new NanoWarSessionListener();

    public void contextInitialized(final ServletContextEvent event) {
        contextListener.contextInitialized(event);
    }

    public void contextDestroyed(ServletContextEvent event) {
        contextListener.contextDestroyed(event);
    }

    public void sessionCreated(HttpSessionEvent event) {
        sessionListener.sessionCreated(event);
    }

    public void sessionDestroyed(HttpSessionEvent event) {
        sessionListener.sessionDestroyed(event);
    }

}
