/*****************************************************************************
 * Copyright (C) NanoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the LICENSE.txt file.                                                     *
 *                                                                           *
 *****************************************************************************/
package org.nanocontainer.nanowar;

import org.picocontainer.MutablePicoContainer;
import org.picocontainer.PicoContainer;
import org.picocontainer.PicoCompositionException;
import org.picocontainer.ObjectReference;

import javax.servlet.ServletContext;
import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

/**
 * ServletContainerFinder looks up a scoped PicoContainer in a web context. By
 * default, it looks in succession in the request, session and application
 * scopes. This class may be extended the
 * {@link ServletContainerFinder#findContainer(HttpServletRequest)}overridden
 * to provide different lookup criteria or priority.
 * 
 * @author Stephen Molitor
 * @author Mauro Talevi
 */
public class ServletContainerFinder {

    /**
     * Looks for a PicoContainer in succession in the request, session and
     * application scopes of an HttpServletRequest.
     * 
     * @param request the HttpServletRequest
     * @return A MutablePicoContainer
     * @throws PicoCompositionException
     */
    public PicoContainer findContainer(HttpServletRequest request) throws PicoCompositionException {
        MutablePicoContainer container = getRequestContainer(request);
        if (container == null) {
            container = getSessionContainer(request.getSession());
        }
        if (container == null) {
            container = getApplicationContainer(request.getSession().getServletContext());
        }
        if (container == null) {
            throw new PicoCompositionException("No Container found in request, session or application."
                    + " Please make sure nanocontainer-nanowar is configured properly in web.xml.");
        }
        return container;
    }

    protected MutablePicoContainer getApplicationContainer(ServletContext context) {
        ObjectReference ref = new ApplicationScopeReference(context, KeyConstants.APPLICATION_CONTAINER);
        return (MutablePicoContainer) ref.get();
    }

    protected MutablePicoContainer getSessionContainer(HttpSession session) {
        ObjectReference ref = new SessionScopeReference(session, KeyConstants.SESSION_CONTAINER);
        return (MutablePicoContainer) ref.get();
    }

    protected MutablePicoContainer getRequestContainer(ServletRequest request) {
        ObjectReference ref = new RequestScopeReference(request, KeyConstants.REQUEST_CONTAINER);
        return (MutablePicoContainer) ref.get();
    }

}