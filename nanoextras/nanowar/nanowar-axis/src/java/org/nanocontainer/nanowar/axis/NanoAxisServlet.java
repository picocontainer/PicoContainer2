/*****************************************************************************
 * Copyright (C) NanoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the LICENSE.txt file.                                                     *
 *                                                                           *
 *****************************************************************************/
package org.nanocontainer.nanowar.axis;

import org.apache.axis.transport.http.AxisServlet;
import org.nanocontainer.nanowar.ServletRequestContainerLauncher;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Extends AxisServlet to build a container for the request and kill it when
 * the request is complete.
 * 
 * @author <a href="mailto:evan@bottch.com">Evan Bottcher</a>
 */
public class NanoAxisServlet extends AxisServlet {

    public void service(HttpServletRequest request, HttpServletResponse response) throws ServletException {
        ServletRequestContainerLauncher containerLauncher = new ServletRequestContainerLauncher(getServletContext(), request);
        try {
            containerLauncher.startContainer();
            super.service(request, response);
        } catch (IOException e) {
            throw new ServletException(e);
        } finally {
            containerLauncher.killContainer();
        }
    }

}
