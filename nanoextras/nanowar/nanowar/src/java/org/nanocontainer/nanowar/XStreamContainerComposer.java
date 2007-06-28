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

import org.nanocontainer.integrationkit.ContainerComposer;
import org.nanocontainer.integrationkit.ContainerRecorder;
import org.nanocontainer.reflection.DefaultContainerRecorder;
import org.nanocontainer.script.xml.XStreamContainerBuilder;
import org.picocontainer.MutablePicoContainer;
import org.picocontainer.DefaultPicoContainer;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.io.InputStreamReader;

/**
 * container composer reading from xml files via xstream. it pulls configuration from
 * xml files placed on classpath of web application ( WEB-INF/classes/ ) , and their names are:
 * nano-application.xml , nano-session.xml and nano-request.xml
 *
 * @author Konstantin Pribluda ( konstantin[at]infodesire.com )
 * @version $Revision$
 */
public final class XStreamContainerComposer implements ContainerComposer {

    // for now, we just use hardvired configuration files.
    public final static String APPLICATION_CONFIG = "nano-application.xml";
    public final static String SESSION_CONFIG = "nano-session.xml";
    public final static String REQUEST_CONFIG = "nano-request.xml";


    // request and session level container recorders.
    // we do not need one for application scope - this happens really seldom
    private final ContainerRecorder requestRecorder;
    private final ContainerRecorder sessionRecorder;

    /**
     * Constructor for the ContainerAssembler object
     */
    public XStreamContainerComposer() {

        requestRecorder = new DefaultContainerRecorder(new DefaultPicoContainer());
        sessionRecorder = new DefaultContainerRecorder(new DefaultPicoContainer());
		
        // create and populate request scope
        InputStreamReader requestScopeScript = new InputStreamReader(Thread.currentThread().getContextClassLoader().getResourceAsStream(REQUEST_CONFIG));
        XStreamContainerBuilder requestPopulator = new XStreamContainerBuilder(requestScopeScript, Thread.currentThread().getContextClassLoader());
        requestPopulator.populateContainer(requestRecorder.getContainerProxy());

        // create and populate session scope
        InputStreamReader sessionScopeScript = new InputStreamReader(Thread.currentThread().getContextClassLoader().getResourceAsStream(SESSION_CONFIG));
        XStreamContainerBuilder sessionPopulator = new XStreamContainerBuilder(sessionScopeScript, Thread.currentThread().getContextClassLoader());
        sessionPopulator.populateContainer(sessionRecorder.getContainerProxy());

    }

    /**
     * compose desired container
     *
     * @param container Description of Parameter
     @param scope     Description of Parameter
     */
    public void composeContainer(MutablePicoContainer container, Object scope) {
        if (scope instanceof ServletContext) {
            InputStreamReader applicationScopeScript = new InputStreamReader(Thread.currentThread().getContextClassLoader().getResourceAsStream(APPLICATION_CONFIG));
            XStreamContainerBuilder applicationPopulator = new XStreamContainerBuilder(applicationScopeScript, Thread.currentThread().getContextClassLoader());
            applicationPopulator.populateContainer(container);
        } else if (scope instanceof HttpSession) {
            sessionRecorder.replay(container);
        } else if (scope instanceof HttpServletRequest) {
            requestRecorder.replay(container);
        }
    }
}
