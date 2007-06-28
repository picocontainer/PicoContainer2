/*****************************************************************************
 * Copyright (C) NanoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the LICENSE.txt file.                                                     *
 *                                                                           *
 *****************************************************************************/
package org.nanocontainer.nanowar;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.jmock.Mock;
import org.jmock.MockObjectTestCase;
import org.picocontainer.MutablePicoContainer;
import org.picocontainer.PicoCompositionException;
import org.picocontainer.DefaultPicoContainer;

/**
 * @author Stephen Molitor
 * @author Mauro Talevi
 */
public final class ServletContainerFinderTestCase extends MockObjectTestCase {

    private final Mock requestMock = mock(HttpServletRequest.class);
    private final HttpServletRequest request = (HttpServletRequest) requestMock.proxy();

    private final Mock sessionMock = mock(HttpSession.class);
    private final HttpSession session = (HttpSession) sessionMock.proxy();

    private final Mock servletContextMock = mock(ServletContext.class);
    private final ServletContext servletContext = (ServletContext) servletContextMock.proxy();

    private ServletContainerFinder finder;

    public void testRequestContainerExists() {
        MutablePicoContainer container = new DefaultPicoContainer();
        requestMock.expects(once()).method("getAttribute").with(eq(KeyConstants.REQUEST_CONTAINER)).will(
                returnValue(container));
        assertSame(container, finder.findContainer(request));
    }

    public void testSessionContainerExists() {
        MutablePicoContainer container = new DefaultPicoContainer();
        requestMock.expects(once()).method("getAttribute").with(eq(KeyConstants.REQUEST_CONTAINER)).will(
                returnValue(null));
        sessionMock.expects(once()).method("getAttribute").with(eq(KeyConstants.SESSION_CONTAINER)).will(
                returnValue(container));
        assertSame(container, finder.findContainer(request));
    }

    public void testApplicationContainerExists() {
        MutablePicoContainer container = new DefaultPicoContainer();
        requestMock.expects(once()).method("getAttribute").with(eq(KeyConstants.REQUEST_CONTAINER)).will(
                returnValue(null));
        sessionMock.expects(once()).method("getAttribute").with(eq(KeyConstants.SESSION_CONTAINER)).will(
                returnValue(null));
        servletContextMock.expects(once()).method("getAttribute").with(eq(KeyConstants.APPLICATION_CONTAINER)).will(
                returnValue(container));
        assertSame(container, finder.findContainer(request));
    }

    public void testNoContainerExists() {
        requestMock.expects(once()).method("getAttribute").with(eq(KeyConstants.REQUEST_CONTAINER)).will(
                returnValue(null));
        sessionMock.expects(once()).method("getAttribute").with(eq(KeyConstants.SESSION_CONTAINER)).will(
                returnValue(null));
        servletContextMock.expects(once()).method("getAttribute").with(eq(KeyConstants.APPLICATION_CONTAINER)).will(
                returnValue(null));

        try {
            finder.findContainer(request);
            fail("PicoCompositionException should have been raised");
        } catch (PicoCompositionException e) {
            // expected
        }
    }

    protected void setUp() {
        finder = new ServletContainerFinder();
        requestMock.stubs().method("getSession").will(returnValue(session));
        sessionMock.stubs().method("getServletContext").will(returnValue(servletContext));
    }

}