/*****************************************************************************
 * Copyright (C) NanoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the LICENSE.txt file.                                                     *
 *                                                                           *
 * Original code by                                                          *
 *****************************************************************************/
package org.nanocontainer.nanowar;

import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.nanocontainer.ClassName;
import org.nanocontainer.DefaultNanoContainer;
import org.nanocontainer.NanoContainer;
import org.nanocontainer.integrationkit.ContainerBuilder;
import org.nanocontainer.integrationkit.ContainerComposer;
import org.nanocontainer.integrationkit.ContainerPopulator;
import org.nanocontainer.integrationkit.ContainerRecorder;
import org.nanocontainer.reflection.DefaultContainerRecorder;
import org.picocontainer.DefaultPicoContainer;
import org.picocontainer.MutablePicoContainer;
import org.picocontainer.ObjectReference;
import org.picocontainer.Parameter;
import org.picocontainer.PicoContainer;
import org.picocontainer.references.SimpleReference;
import org.picocontainer.parameters.ConstantParameter;

/**
 * <p>
 * ScopedContainerComposer is a 
 * {@link org.nanocontainer.integrationkit.ContainerComposer ContainerComposer} 
 * which can build PicoContainers for different web context scopes: 
 * application, session and request. 
 * </p>
 * <p>
 * The configuration for each scope is contained in one of more 
 * NanoContainer scripts.
 * The {@link org.nanocontainer.integrationkit.ContainerBuilder ContainerBuilder} 
 * used to build the PicoContainer and the names of scoped script files are configurable 
 * via a ScopedContainerConfigurator.
 * </p>
 * <p>
 * <b>Note:</b> ScopedContainerComposer requires ContainerBuilders that also implement
 * {@link org.nanocontainer.integrationkit.ContainerPopulator ContainerPopulator},
 * as this is used by the 
 * {@link org.nanocontainer.integrationkit.ContainerRecorder ContainerRecorder} proxy.
 * </p>
 * 
 * @author Mauro Talevi
 * @author Konstantin Pribluda ( konstantin.pribluda[at]infodesire.com )
 * @version $Revision$
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

    private ScopedContainerConfigurator getConfigurator(PicoContainer pico){
        ScopedContainerConfigurator configurator = pico.getComponent(ScopedContainerConfigurator.class);
        if ( configurator == null ){
            configurator = new ScopedContainerConfigurator();
        }
        return configurator;
    }
    
	private void populateContainer(String resources, ContainerRecorder recorder, MutablePicoContainer parent) {
	    MutablePicoContainer container = recorder.getContainerProxy();
	    String[] resourcePaths = toCSV(resources);
        for (String resourcePath : resourcePaths) {
            ContainerPopulator populator = createContainerPopulator(getResource(resourcePath), parent);
            populator.populateContainer(container);
        }
    }

	private String[] toCSV(String resources){
	    StringTokenizer st = new StringTokenizer(resources, COMMA);
	    List<String> tokens = new ArrayList<String>();
	    while ( st.hasMoreTokens() ){
	        tokens.add(st.nextToken().trim());	        
	    }
	    return tokens.toArray(new String[tokens.size()]);
	}
	
	private ContainerPopulator createContainerPopulator(Reader reader, MutablePicoContainer parent) {
        NanoContainer nano = new DefaultNanoContainer(getClassLoader());
        Parameter[] parameters = new Parameter[] {
                new ConstantParameter(reader),
                new ConstantParameter(getClassLoader()) };
        nano.addComponent(containerBuilderClassName,
                new ClassName(containerBuilderClassName), parameters);
        ContainerBuilder containerBuilder = (ContainerBuilder) nano
                .getComponent(containerBuilderClassName);
        ObjectReference parentRef = new SimpleReference();
        parentRef.set(parent);
        containerBuilder.buildContainer(new SimpleReference(), parentRef, null, false);
        return (ContainerPopulator) containerBuilder;
    }

    private Reader getResource(String resource){
        return new InputStreamReader(getClassLoader().getResourceAsStream(resource));    	
    }

	private ClassLoader getClassLoader() {
		return Thread.currentThread().getContextClassLoader();
	}
    
}