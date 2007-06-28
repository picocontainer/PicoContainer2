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

import org.jmock.Mock;
import org.jmock.MockObjectTestCase;
import org.picocontainer.MutablePicoContainer;
import org.picocontainer.DefaultPicoContainer;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

/**
 * test case for XStreamContainerComposer
 *
 * @author Konstantin Pribluda ( konstantin.pribluda[at]infodesire.com )
 * @version $Revision$
 */
public class XStreamContainerComposerTestCase extends MockObjectTestCase implements KeyConstants {

    public void testThatProperConfigurationIsRead() throws Exception {
        XStreamContainerComposer composer = new XStreamContainerComposer();

        MutablePicoContainer application = new DefaultPicoContainer();
        Mock servletContextMock = mock(ServletContext.class);

        composer.composeContainer(application, servletContextMock.proxy());

        assertNotNull(application.getComponent("applicationScopedInstance"));

        MutablePicoContainer session = new DefaultPicoContainer();
        Mock httpSessionMock = mock(HttpSession.class);

        composer.composeContainer(session, httpSessionMock.proxy());

        assertNotNull(session.getComponent("sessionScopedInstance"));

        MutablePicoContainer request = new DefaultPicoContainer();
        Mock httpRequestMock = mock(HttpServletRequest.class);

        composer.composeContainer(request, httpRequestMock.proxy());

        assertNotNull(request.getComponent("requestScopedInstance"));
    }
    

    public void testCompositionWithInvalidScope() {
        XStreamContainerComposer composer = new XStreamContainerComposer();

        MutablePicoContainer applicationContainer = new DefaultPicoContainer();
        composer.composeContainer(applicationContainer, "invalid-scope");
        assertNull(applicationContainer.getComponent("applicationScopedInstance"));
    }
        
    public void testComposedHierarchy() {
        XStreamContainerComposer composer = new XStreamContainerComposer();

        MutablePicoContainer applicationContainer = new DefaultPicoContainer();
        Mock servletContextMock = mock(ServletContext.class);

        composer.composeContainer(applicationContainer, servletContextMock.proxy());

        MutablePicoContainer sessionContainer = new DefaultPicoContainer(applicationContainer);
        Mock httpSessionMock = mock(HttpSession.class);
        composer.composeContainer(sessionContainer, httpSessionMock.proxy());

        MutablePicoContainer requestContainer = new DefaultPicoContainer(sessionContainer);
        Mock httpRequestMock = mock(HttpServletRequest.class);
        composer.composeContainer(requestContainer, httpRequestMock.proxy());
        assertNotNull(requestContainer.getComponent("testFooHierarchy"));
    }
}

