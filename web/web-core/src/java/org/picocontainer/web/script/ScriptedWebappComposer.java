/*******************************************************************************
 * Copyright (C) PicoContainer Organization. All rights reserved.
 * --------------------------------------------------------------------------
 * The software in this package is published under the terms of the BSD style
 * license a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 ******************************************************************************/
package org.picocontainer.web.script;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;

import org.picocontainer.ComponentAdapter;
import org.picocontainer.MutablePicoContainer;
import org.picocontainer.Parameter;
import org.picocontainer.PicoCompositionException;
import org.picocontainer.PicoContainer;
import org.picocontainer.parameters.ConstantParameter;
import org.picocontainer.script.ClassName;
import org.picocontainer.script.ContainerBuilder;
import org.picocontainer.script.DefaultScriptedPicoContainer;
import org.picocontainer.script.ScriptedPicoContainer;
import org.picocontainer.web.WebappComposer;

/**
 * Script-based webapp composer. Allows to build containers for each webapp
 * scope from picocontainer scripts, using configurable builder and script
 * resources, which default to XML scripts.
 * 
 * @author Mauro Talevi
 */
public class ScriptedWebappComposer implements WebappComposer {

    public final static String DEFAULT_CONTAINER_BUILDER = "org.picocontainer.script.xml.XMLContainerBuilder";
    public final static String DEFAULT_APPLICATION_SCRIPT = "pico-application.xml";
    public final static String DEFAULT_SESSION_SCRIPT = "pico-session.xml";
    public final static String DEFAULT_REQUEST_SCRIPT = "pico-request.xml";

    private String containerBuilderClassName;
    private String applicationScript;
    private String sessionScript;
    private String requestScript;

    public ScriptedWebappComposer() {
        this(DEFAULT_CONTAINER_BUILDER, DEFAULT_APPLICATION_SCRIPT, DEFAULT_SESSION_SCRIPT, DEFAULT_REQUEST_SCRIPT);
    }

    public ScriptedWebappComposer(String containerBuilderClassName, String applicationScript, String sessionScript,
            String requestScript) {
        this.containerBuilderClassName = containerBuilderClassName;
        this.applicationScript = applicationScript;
        this.sessionScript = sessionScript;
        this.requestScript = requestScript;
    }

    public void composeApplication(MutablePicoContainer applicationContainer) {
        populateContainer(applicationScript, applicationContainer);
    }

    public void composeSession(MutablePicoContainer sessionContainer) {
        populateContainer(sessionScript, sessionContainer);
    }

    public void composeRequest(MutablePicoContainer requestContainer) {
        populateContainer(requestScript, requestContainer);
    }

    private void populateContainer(String resourcePath, MutablePicoContainer container) {
        PicoContainer buildContainer = buildContainer(resourcePath, container.getParent());
        for (ComponentAdapter<?> adapter : buildContainer.getComponentAdapters()) {
            container.addAdapter(adapter);
        }
    }

    private PicoContainer buildContainer(String resourcePath, PicoContainer parent) {
        ContainerBuilder builder = createContainerBuilder(getResource(resourcePath));
        return builder.buildContainer(parent, null, false);
    }

    private ContainerBuilder createContainerBuilder(Reader reader) {
        ScriptedPicoContainer scripted = new DefaultScriptedPicoContainer(getClassLoader());
        Parameter[] parameters = new Parameter[] { new ConstantParameter(reader),
                new ConstantParameter(getClassLoader()) };
        scripted.addComponent(containerBuilderClassName, new ClassName(containerBuilderClassName), parameters);
        return scripted.getComponent(ContainerBuilder.class);
    }

    private Reader getResource(String resource) {
        ClassLoader classLoader = getClassLoader();
        InputStream stream = classLoader.getResourceAsStream(resource);
        if (stream == null) {
            throw new PicoCompositionException("Resource " + resource + " not found in classloader " + classLoader);
        }
        return new InputStreamReader(stream);
    }

    private ClassLoader getClassLoader() {
        return Thread.currentThread().getContextClassLoader();
    }
}
