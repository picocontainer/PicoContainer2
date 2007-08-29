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

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.jmock.Mock;
import org.nanocontainer.integrationkit.ContainerBuilder;
import org.nanocontainer.integrationkit.ContainerComposer;
import org.nanocontainer.integrationkit.DefaultContainerBuilder;
import org.nanocontainer.integrationkit.PicoCompositionException;
import org.picocontainer.MutablePicoContainer;
import org.picocontainer.PicoContainer;
import org.picocontainer.references.SimpleReference;

/**
 * ContainerComposerMocker mocks the functionality of a specified container composer class.
 * If the specified class name is not that of an implementation ContainerComposer
 * a PicoCompositionException will be thrown.
 *
 * @author Konstantin Pribluda
 * @version $Revision$
 */
public class ContainerComposerMocker implements KeyConstants {

    private final ContainerBuilder containerKiller = new DefaultContainerBuilder(null);
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

    public ContainerComposerMocker(Class containerComposerClass) {
        try {
            containerBuilder = new DefaultContainerBuilder((ContainerComposer) containerComposerClass.newInstance());
        } catch (Exception ex) {
            throw new PicoCompositionException(ex);
        }
    }

    /**
     *  Mock application start
     */
    public void startApplication() {
        SimpleReference ref = new SimpleReference();
        containerBuilder.buildContainer(ref, new SimpleReference(), (new Mock(ServletContext.class)).proxy(), false);
        applicationContainer = (MutablePicoContainer) ref.get();
    }

    /**
     *  Mock application stop
     */
    public void stopApplication() {
        SimpleReference ref = new SimpleReference();
        ref.set(applicationContainer);
        containerKiller.killContainer(ref);
        // and reset all the containers
        applicationContainer = null;
        sessionContainer = null;
        requestContainer = null;
    }

    /**
     * Mock new session
     */
    public void startSession() {
        SimpleReference ref = new SimpleReference();
        SimpleReference parent = new SimpleReference();
        parent.set(applicationContainer);
        containerBuilder.buildContainer(ref, parent, ((new Mock(HttpSession.class)).proxy()), false);
        sessionContainer = (MutablePicoContainer) ref.get();
    }

    /**
     *  Mock session invalidation
     */
    public void stopSession() {
        SimpleReference ref = new SimpleReference();
        ref.set(sessionContainer);
        containerKiller.killContainer(ref);
        sessionContainer = null;
        requestContainer = null;
    }


    /**
     *  Mock request start
     */
    public void startRequest() {
        SimpleReference ref = new SimpleReference();
        SimpleReference parent = new SimpleReference();
        parent.set(sessionContainer);
        containerBuilder.buildContainer(ref, parent, (new Mock(HttpServletRequest.class)).proxy(), false);
        requestContainer = (MutablePicoContainer) ref.get();
    }

    /**
     * Mock request stop
     */
    public void stopRequest() {
        SimpleReference ref = new SimpleReference();
        ref.set(requestContainer);
        containerKiller.killContainer(ref);
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
