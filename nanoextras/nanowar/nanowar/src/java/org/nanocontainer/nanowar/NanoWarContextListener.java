/*****************************************************************************
 * Copyright (C) NanoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the LICENSE.txt file.                                                     *
 *                                                                           *
 *****************************************************************************/

package org.nanocontainer.nanowar;

import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringReader;
import java.util.Enumeration;
import java.util.Properties;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.nanocontainer.ClassName;
import org.nanocontainer.DefaultNanoContainer;
import org.nanocontainer.NanoContainer;
import org.nanocontainer.integrationkit.ContainerBuilder;
import org.nanocontainer.integrationkit.ContainerComposer;
import org.nanocontainer.integrationkit.DefaultContainerBuilder;
import org.nanocontainer.integrationkit.PicoCompositionException;
import org.nanocontainer.script.ScriptBuilderResolver;
import org.nanocontainer.script.ScriptedContainerBuilderFactory;
import org.picocontainer.ComponentAdapter;
import org.picocontainer.MutablePicoContainer;
import org.picocontainer.ObjectReference;
import org.picocontainer.PicoContainer;
import org.picocontainer.references.SimpleReference;
import org.picocontainer.containers.PropertiesPicoContainer;
import org.picocontainer.containers.SystemPropertiesPicoContainer;
import org.picocontainer.parameters.ConstantParameter;

/**
 * Servlet listener class that hooks into the underlying servlet
 * container and instantiates, assembles, starts, stores and
 * disposes the appropriate pico containers when applications start/stop.
 * <p>
 * To use, simply add as a listener to web.xml the listener-class
 * <code>org.nanocontainer.nanowar.NanoWarContextListener</code>.
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
 * @see org.nanocontainer.nanowar.NanoWarSessionListener
 * @see org.nanocontainer.nanowar.ServletContainerListener
 * @author Michael Rimov
 * @author Joe Walnes
 * @author Aslak Helles&oslash;y
 * @author Philipp Meier
 * @author Paul Hammant
 * @author Mauro Talevi
 * @author Konstantin Pribluda
 */
@SuppressWarnings("serial")
public class NanoWarContextListener extends AbstractNanoWarListener implements ServletContextListener, KeyConstants {

	/**
	 * handle context initialisation.  we need to create container 
	 * and store it into proper reference
	 */
    public void contextInitialized(ServletContextEvent event) {
        ServletContext context = event.getServletContext();
        try {
            ContainerBuilder containerBuilder = createContainerBuilder(context);

            ObjectReference<ContainerBuilder> builderRef = new ApplicationScopeReference<ContainerBuilder>(context, BUILDER);
            builderRef.set(containerBuilder);

            SimpleReference<PicoContainer> parentRef = new SimpleReference<PicoContainer>();

            // check whether we have to provide system roperties container
            if(context.getInitParameter(SYSTEM_PROPERTIES_CONTAINER) != null) {
            	parentRef.set(new SystemPropertiesPicoContainer());
            }
            
            // maybe there are properties specified? 
            String propertiesResource = context.getInitParameter(PROPERTIES_CONTAINER);
            if(propertiesResource != null) {
            	Properties properties = new Properties();
            	properties.load(Thread.currentThread().getContextClassLoader().getResourceAsStream(propertiesResource));
            	parentRef.set(new PropertiesPicoContainer(properties,parentRef.get()));
            }
            
            ObjectReference containerRef = new ApplicationScopeReference(context, APPLICATION_CONTAINER);
            containerBuilder.buildContainer(containerRef, parentRef, context, false);
        // TODO bad catch - PH
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("--> " + e.getMessage());
            // Not all servlet containers print the nested exception. Do it here.
            event.getServletContext().log(e.getMessage(), e);
            throw new PicoCompositionException(e);
        }
    }

    private ContainerBuilder createContainerBuilder(ServletContext context) {
        Enumeration initParameters = context.getInitParameterNames();
        while (initParameters.hasMoreElements()) {
            String initParameter = (String) initParameters.nextElement();
            if (initParameter.startsWith(NANOCONTAINER_PREFIX)) {
                String builderClassName = getBuilderClassName(initParameter);
                String script = context.getInitParameter(initParameter);
                Reader scriptReader;
                if (script.trim().startsWith("/") && !(script.trim().startsWith("//") || script.trim().startsWith("/*"))) {
                    // the script isn't inlined, but in a separate file.
                    scriptReader = new InputStreamReader(context.getResourceAsStream(script));
                } else {
                    scriptReader = new StringReader(script);
                }
                ScriptedContainerBuilderFactory scriptedContainerBuilderFactory = new ScriptedContainerBuilderFactory(scriptReader, builderClassName, Thread.currentThread().getContextClassLoader());
                return scriptedContainerBuilderFactory.getContainerBuilder();
            }
            if (initParameter.equals(CONTAINER_COMPOSER)) {
                ContainerComposer containerComposer = createContainerComposer(context);
                return new DefaultContainerBuilder(containerComposer);
            }
        }
        throw new PicoCompositionException("Couldn't create a builder from context parameters in web.xml");
    }

    private String getBuilderClassName(String scriptName){
        String extension = scriptName.substring(scriptName.lastIndexOf('.'));
        ScriptBuilderResolver resolver = new ScriptBuilderResolver();
        return resolver.getBuilderClassName(extension);
    }

    private ContainerComposer createContainerComposer(ServletContext context) {
        String containerComposerClassName = context.getInitParameter(CONTAINER_COMPOSER);
        // disposable container used to instantiate the ContainerComposer
        NanoContainer nanoContainer = new DefaultNanoContainer(Thread.currentThread().getContextClassLoader());
        String script = context.getInitParameter(CONTAINER_COMPOSER_CONFIGURATION);
        PicoContainer picoConfiguration = null;
        if ( script != null ){
            Reader scriptReader = new InputStreamReader(context.getResourceAsStream(script));
            String builderClassName = getBuilderClassName(script);
            ScriptedContainerBuilderFactory scriptedContainerBuilderFactory = new ScriptedContainerBuilderFactory(scriptReader, builderClassName, Thread.currentThread().getContextClassLoader());
            picoConfiguration = buildContainer(scriptedContainerBuilderFactory.getContainerBuilder());
        }
        ComponentAdapter componentAdapter;
        if ( picoConfiguration != null ){
            componentAdapter = nanoContainer.addComponent(containerComposerClassName, new ClassName(containerComposerClassName), new ConstantParameter(picoConfiguration)).getComponentAdapter(containerComposerClassName);
        } else {
            ClassName className = new ClassName(containerComposerClassName);
            MutablePicoContainer mutablePicoContainer = nanoContainer.addComponent(className);
            componentAdapter = mutablePicoContainer.getComponentAdapter(className);
        }
        return (ContainerComposer) componentAdapter.getComponentInstance(nanoContainer);
    }

    public void contextDestroyed(ServletContextEvent event) {
        ServletContext context = event.getServletContext();
        ObjectReference containerRef = new ApplicationScopeReference(context, APPLICATION_CONTAINER);
        killContainer(containerRef);
    }
}
