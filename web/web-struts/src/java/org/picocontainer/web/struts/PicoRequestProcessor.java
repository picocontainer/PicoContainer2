/*****************************************************************************
 * Copyright (C) PicoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the LICENSE.txt file.                                                     *
 *                                                                           *
 *****************************************************************************/
package org.picocontainer.web.struts;

import org.apache.struts.action.Action;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.RequestProcessor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Uses Pico to produce Actions and inject dependencies into them. If you are using the Tiles
 * library, use {@link PicoTilesRequestProcessor} instead.
 *
 * @author Stephen Molitor
 * @see ActionFactory
 * @see PicoTilesRequestProcessor
 */
public class PicoRequestProcessor extends RequestProcessor {

    private final ActionFactory actionFactory = new ActionFactory();

    /**
     * Creates or retrieves the action instance.  The action is retrieved from the actions
     * Pico container, using the mapping path as the component key.  If no such action exists,
     * a new one will be instantiated and placed in the actions container, thus injecting
     * its dependencies.
     *
     * @param request  the HTTP request object.
     * @param response the HTTP response object.
     * @param mapping  the action mapping.
     * @return the action instance.
     */
    protected Action processActionCreate(HttpServletRequest request,
                                         HttpServletResponse response,
                                         ActionMapping mapping)
    {
        return actionFactory.getAction(request, mapping, this.servlet);
    }

}
