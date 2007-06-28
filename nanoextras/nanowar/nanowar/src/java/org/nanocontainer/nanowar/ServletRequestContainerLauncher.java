/*****************************************************************************
 * Copyright (C) NanoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the LICENSE.txt file.                                                     *
 *                                                                           *
 *****************************************************************************/
package org.nanocontainer.nanowar;

import org.nanocontainer.integrationkit.ContainerBuilder;
import org.picocontainer.ObjectReference;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

/**
 * @author Joe Walnes
 */
public final class ServletRequestContainerLauncher {

    private final ContainerBuilder containerBuilder;
    private final ObjectReference containerRef;
    private final HttpServletRequest request;
    private final ServletContext context;

    public ServletRequestContainerLauncher(ServletContext context, HttpServletRequest request) {
        ObjectReference builderRef = new ApplicationScopeReference(context, KeyConstants.BUILDER);
        containerRef = new RequestScopeReference(request, KeyConstants.REQUEST_CONTAINER);
        containerBuilder = (ContainerBuilder) builderRef.get();
        this.request = request;
        this.context = context;
    }

    public void startContainer() throws ServletException {
        if (containerBuilder == null) {
            throw new ServletException(ServletContainerListener.class.getName()+" not deployed");
        }
        HttpSession session = request.getSession(true);

        //
        //Session container reference may or may not exist.  if it doesn't, get the
        //application level container instead. However, it is designed to operate
        //with null session container. in which case we have old behavior instead.
        //
        ObjectReference containerReferenceToUse;
        if (session == null || session.getAttribute(KeyConstants.SESSION_CONTAINER) == null) {
            containerReferenceToUse = new ApplicationScopeReference(context, KeyConstants.APPLICATION_CONTAINER);
        } else {
            containerReferenceToUse = new SessionScopeReference(session, KeyConstants.SESSION_CONTAINER);
        } 

        containerBuilder.buildContainer(containerRef, containerReferenceToUse, request, false);

    }

    public void killContainer() {
        if (containerRef.get() != null) {
            containerBuilder.killContainer(containerRef);
        }
    }
}
