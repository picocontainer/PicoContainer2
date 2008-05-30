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


import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.picocontainer.tck.MockFactory.mockeryWithCountingNamingScheme;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.jmock.Mockery;
import org.jmock.integration.junit4.JMock;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.picocontainer.DefaultPicoContainer;
import org.picocontainer.MutablePicoContainer;


/**
 * test case for XStreamContainerComposer
 *
 * @author Konstantin Pribluda ( konstantin.pribluda[at]infodesire.com )
 * @author Mauro Talevi
 */
@RunWith(JMock.class)
public class XStreamContainerComposerTestCase {

	private Mockery mockery = mockeryWithCountingNamingScheme();
	
    @Test public void testThatProperConfigurationIsRead() throws Exception {
        XStreamContainerComposer composer = new XStreamContainerComposer();

        MutablePicoContainer application = new DefaultPicoContainer();
        ServletContext servletContext = mockery.mock(ServletContext.class);

        composer.composeContainer(application, servletContext);

        assertNotNull(application.getComponent("applicationScopedInstance"));

        MutablePicoContainer session = new DefaultPicoContainer();
        HttpSession httpSession = mockery.mock(HttpSession.class);

        composer.composeContainer(session, httpSession);

        assertNotNull(session.getComponent("sessionScopedInstance"));

        MutablePicoContainer request = new DefaultPicoContainer();
        HttpServletRequest httpRequest = mockery.mock(HttpServletRequest.class);

        composer.composeContainer(request, httpRequest);

        assertNotNull(request.getComponent("requestScopedInstance"));
    }
    

    @Test public void testCompositionWithInvalidScope() {
        XStreamContainerComposer composer = new XStreamContainerComposer();

        MutablePicoContainer applicationContainer = new DefaultPicoContainer();
        composer.composeContainer(applicationContainer, "invalid-scope");
        assertNull(applicationContainer.getComponent("applicationScopedInstance"));
    }
        
    @Test public void testComposedHierarchy() {
        XStreamContainerComposer composer = new XStreamContainerComposer();

        MutablePicoContainer applicationContainer = new DefaultPicoContainer();
        ServletContext servletContext = mockery.mock(ServletContext.class);

        composer.composeContainer(applicationContainer, servletContext);

        MutablePicoContainer sessionContainer = new DefaultPicoContainer(applicationContainer);
        HttpSession httpSession = mockery.mock(HttpSession.class);
        composer.composeContainer(sessionContainer, httpSession);

        MutablePicoContainer requestContainer = new DefaultPicoContainer(sessionContainer);
        HttpServletRequest httpRequest = mockery.mock(HttpServletRequest.class);
        composer.composeContainer(requestContainer, httpRequest);
        assertNotNull(requestContainer.getComponent("testFooHierarchy"));
    }
}

