/*****************************************************************************
 * Copyright (C) NanoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the LICENSE.txt file.                                                     *
 *                                                                           *
 * Original code by Konstantin Pribluda                                      *
 *****************************************************************************/
package org.nanocontainer.nanowar.chain;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringReader;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.servlet.ServletContext;

import org.nanocontainer.DefaultNanoContainer;
import org.nanocontainer.NanoContainer;
import org.nanocontainer.ClassName;
import org.nanocontainer.integrationkit.ContainerBuilder;
import org.nanocontainer.integrationkit.ContainerPopulator;
import org.nanocontainer.integrationkit.ContainerRecorder;
import org.nanocontainer.reflection.DefaultContainerRecorder;
import org.picocontainer.MutablePicoContainer;
import org.picocontainer.Parameter;
import org.picocontainer.PicoContainer;
import org.picocontainer.parameters.ConstantParameter;
import org.picocontainer.DefaultPicoContainer;

/**
 * <p>
 * ServletChainBuilder builds ContainerChains from servlet path and caches 
 * container recorders for later use.
 * </p>
 * 
 * @author Kontantin Pribluda
 * @author Mauro Talevi
 */
public final class ServletChainBuilder {

	private final Map recorderCache;
	private final ServletContext context;
    private final String containerBuilderClassName;
    private final String containerScriptName;
    private final String emptyContainerScript;

	/**
	 * Constructor for the ServletChainBuilder object
	 * 
	 * @param context the ServletContext
     * @param containerBuilderClassName the class name of the ContainerBuilder
     * @param containerScriptName the name of the container script resource
     * @param emptyContainerScript the script for empty container if the container config is not found
	 */
	public ServletChainBuilder(ServletContext context, String containerBuilderClassName, String containerScriptName, String emptyContainerScript) {
        this.context = context;
        this.containerBuilderClassName = containerBuilderClassName;
        this.containerScriptName = containerScriptName;
        this.emptyContainerScript = emptyContainerScript;
        this.recorderCache = new HashMap();
	}

	/**
	 * populate container for given path. cache result in container recorders
	 * 
	 * @param container the MutablePicoContainer used by the recorder
	 * @param path the String representing the servlet path used as key for the recorder cache
	 */
	public void populateContainerForPath(MutablePicoContainer container,
			String path) {
		ContainerRecorder recorder;
		synchronized (recorderCache) {
			recorder = (ContainerRecorder) recorderCache.get(path);
			if (recorder == null) {
				recorder = new DefaultContainerRecorder(new DefaultPicoContainer());
				recorderCache.put(path, recorder);
				ContainerPopulator populator = createContainerPopulator(containerBuilderClassName,
                                    obtainReader(path), Thread.currentThread().getContextClassLoader());
				populator.populateContainer(recorder.getContainerProxy());
			}
		}
		recorder.replay(container);
	}

	/**
	 * Build ContainerChain for path elements
	 * 
	 * @param pathElements an array of Objects used as keys for
	 *            selecting Application objects
     * @param parent the parent PicoContainer or <code>null</code>
	 * @return The configured ContainerChain
	 */
	public ContainerChain buildChain(Object[] pathElements, PicoContainer parent) {
		ContainerChain chain = new ContainerChain();
		populateRecursively(chain, parent, Arrays.asList(pathElements).iterator());
		return chain;
	}

	/**
	 * Create and populate containers in recursive way
	 * 
     * @param chain the ContainerChain to which the containers are added
	 * @param parent the parent PicoContainer
     * @param pathElements the Iterator on the path elements
	 */
	public void populateRecursively(ContainerChain chain, PicoContainer parent,
			Iterator pathElements) {
		if (pathElements.hasNext()) {
			Object key = pathElements.next();
			DefaultPicoContainer container = new DefaultPicoContainer(parent);

			populateContainerForPath(container, key.toString());
			chain.addContainer(container);
			populateRecursively(chain, container, pathElements);
		}
	}

    /**
     * Instantiates ContainerPopulator for container builder
     * 
     * @param containerBuilderClassName the class name of the builder implementing ContainerPopulator
     * @param reader the Reader for the builder
     * @param classLoader the ClassLoader for the builder
     * @return An instance of ContainerPopulator
     */
    private ContainerPopulator createContainerPopulator(String containerBuilderClassName, Reader reader, ClassLoader classLoader) {
        NanoContainer nano = new DefaultNanoContainer(classLoader);
        Parameter[] parameters = new Parameter[] {
                new ConstantParameter(reader),
                new ConstantParameter(classLoader) };
        nano.addComponent(containerBuilderClassName,
                new ClassName(containerBuilderClassName), parameters);
        ContainerBuilder containerBuilder = (ContainerBuilder) nano
                .getComponent(containerBuilderClassName);
        //containerBuilder.buildContainer(new SimpleReference(), null, null,
        //        false);
        return (ContainerPopulator) containerBuilder;
    }
    
    /**
     * Obtain reader from servlet context path, by appending container script name to path.
     * If not found, returns reader for empty container instead.
     * 
     * @param path the String representing the path in servlet context
     * @return A Reader for corresponding script or for empty container if script not found
     */
    private Reader obtainReader(String path) {
        InputStream is = context.getResourceAsStream(path + containerScriptName);
        if (is != null) {
            return new InputStreamReader(is);
        } else {
            return new StringReader(emptyContainerScript);
        }
    }

}