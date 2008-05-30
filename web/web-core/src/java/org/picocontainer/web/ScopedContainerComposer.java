/*******************************************************************************
 * Copyright (C) PicoContainer Organization. All rights reserved. 
 * ---------------------------------------------------------------------------
 * The software in this package is published under the terms of the BSD style
 * license a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 ******************************************************************************/
package org.picocontainer.web;

import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.picocontainer.ComponentAdapter;
import org.picocontainer.DefaultPicoContainer;
import org.picocontainer.MutablePicoContainer;
import org.picocontainer.Parameter;
import org.picocontainer.PicoContainer;
import org.picocontainer.parameters.ConstantParameter;
import org.picocontainer.script.ClassName;
import org.picocontainer.script.ContainerBuilder;
import org.picocontainer.script.DefaultScriptedPicoContainer;
import org.picocontainer.script.ScriptedPicoContainer;

/**
 * <p>
 * ScopedContainerComposer is a
 * {@link org.picocontainer.web.ContainerComposer ContainerComposer} which can
 * build PicoContainers for different web context scopes: application, session
 * and request.
 * </p>
 * <p>
 * The configuration for each scope is contained in one of more scripts. The
 * {@link org.picocontainer.script.ContainerBuilder ContainerBuilder} used to
 * build the PicoContainer and the names of scoped script files are configurable
 * via a ScopedContainerConfigurator.
 * </p>
 * <p>
 * 
 * @author Mauro Talevi
 * @author Konstantin Pribluda ( konstantin.pribluda[at]infodesire.com )
 * @deprecated Use ScriptedWebappComposer
 */
public final class ScopedContainerComposer implements ContainerComposer {

    private static final String COMMA = ",";

    // ContainerBuilder class name
    private final String containerBuilderClassName;
    // scoped container recorders
    private final ContainerRecorder applicationRecorder;
    private ContainerRecorder requestRecorder;
    private ContainerRecorder sessionRecorder;

    /**
     * Creates a default ScopedContainerComposer
     */
    public ScopedContainerComposer() {
        this(new DefaultPicoContainer());
    }

    /**
     * Creates a configurable ScopedContainerComposer
     * 
     * @param configuration the PicoContainer holding the configuration
     */
    public ScopedContainerComposer(PicoContainer configuration) {
        ScopedContainerConfigurator config = getConfigurator(configuration);
        containerBuilderClassName = config.getContainerBuilder();

        MutablePicoContainer applicationContainerPrototype = new DefaultPicoContainer();
        applicationRecorder = new DefaultContainerRecorder(applicationContainerPrototype);
        populateContainer(config.getApplicationConfig(), applicationRecorder, null);

        MutablePicoContainer sessionContainerPrototype = new DefaultPicoContainer(applicationContainerPrototype);
        sessionRecorder = new DefaultContainerRecorder(sessionContainerPrototype);
        populateContainer(config.getSessionConfig(), sessionRecorder, applicationContainerPrototype);

        MutablePicoContainer requestContainerPrototype = new DefaultPicoContainer(sessionContainerPrototype);
        requestRecorder = new DefaultContainerRecorder(requestContainerPrototype);
        populateContainer(config.getRequestConfig(), requestRecorder, sessionContainerPrototype);
    }

    public void composeContainer(MutablePicoContainer container, Object scope) {
        if (scope instanceof ServletContext) {
            applicationRecorder.replay(container);
        } else if (scope instanceof HttpSession) {
            sessionRecorder.replay(container);
        } else if (scope instanceof HttpServletRequest) {
            requestRecorder.replay(container);
        }
    }

    private ScopedContainerConfigurator getConfigurator(PicoContainer pico) {
        ScopedContainerConfigurator configurator = pico.getComponent(ScopedContainerConfigurator.class);
        if (configurator == null) {
            configurator = new ScopedContainerConfigurator();
        }
        return configurator;
    }

    private void populateContainer(String resources, ContainerRecorder recorder, MutablePicoContainer parent) {
        MutablePicoContainer container = recorder.getContainerProxy();
        String[] resourcePaths = toCSV(resources);
        for (String resourcePath : resourcePaths) {
            populateContainer(resourcePath, container);
        }
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

    private String[] toCSV(String resources) {
        StringTokenizer st = new StringTokenizer(resources, COMMA);
        List<String> tokens = new ArrayList<String>();
        while (st.hasMoreTokens()) {
            tokens.add(st.nextToken().trim());
        }
        return tokens.toArray(new String[tokens.size()]);
    }

    private Reader getResource(String resource) {
        return new InputStreamReader(getClassLoader().getResourceAsStream(resource));
    }

    private ClassLoader getClassLoader() {
        return Thread.currentThread().getContextClassLoader();
    }

}
