/*****************************************************************************
 * Copyright (C) PicoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the LICENSE.txt file.                                                     *
 *                                                                           *
 * Original code by                                                          *
 *****************************************************************************/
package org.picocontainer.web;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.jmock.Mockery;
import org.picocontainer.MutablePicoContainer;
import org.picocontainer.PicoCompositionException;
import org.picocontainer.PicoContainer;
import org.picocontainer.script.ContainerBuilder;
import org.picocontainer.script.DefaultContainerBuilder;

/**
 * ContainerComposerMocker mocks the functionality of a specified container composer class.
 * If the specified class name is not that of an implementation ContainerComposer
 * a PicoCompositionException will be thrown.
 *
 * @author Konstantin Pribluda
 */
public class ContainerComposerMocker {

	private Mockery mockery = new Mockery();
	
    private final ContainerBuilder containerKiller = new DefaultContainerBuilder();
    /**
     * application level container
     */
    MutablePicoContainer applicationContainer;
    /**
     * session level container
     */
    MutablePicoContainer sessionContainer;
    /**
     * request level container
     */
    MutablePicoContainer requestContainer;

    ContainerBuilder containerBuilder;

    public ContainerComposerMocker(Class<?> containerComposerClass) {
        try {
            containerBuilder = new DefaultContainerBuilder();
        } catch (Exception ex) {
            throw new PicoCompositionException(ex);
        }
    }

    /**
     *  Mock application start
     */
    public void startApplication() {        
        applicationContainer = (MutablePicoContainer) containerBuilder.buildContainer(null, mockery.mock(ServletContext.class), false);
    }

    /**
     *  Mock application stop
     */
    public void stopApplication() {
        containerKiller.killContainer(applicationContainer);
        // and reset all the containers
        applicationContainer = null;
        sessionContainer = null;
        requestContainer = null;
    }

    /**
     * Mock new session
     */
    public void startSession() {
        sessionContainer = (MutablePicoContainer) containerBuilder.buildContainer(applicationContainer, mockery.mock(HttpSession.class), false);
    }

    /**
     *  Mock session invalidation
     */
    public void stopSession() {
        containerKiller.killContainer(sessionContainer);
        sessionContainer = null;
        requestContainer = null;
    }


    /**
     *  Mock request start
     */
    public void startRequest() {
        requestContainer = (MutablePicoContainer) containerBuilder.buildContainer( sessionContainer, mockery.mock(HttpServletRequest.class), false);
    }

    /**
     * Mock request stop
     */
    public void stopRequest() {
        containerKiller.killContainer(requestContainer);
        requestContainer = null;
    }

    public PicoContainer getApplicationContainer() {
        return applicationContainer;
    }

    public PicoContainer getSessionContainer() {
        return sessionContainer;
    }

    public PicoContainer getRequestContainer() {
        return requestContainer;
    }
}
