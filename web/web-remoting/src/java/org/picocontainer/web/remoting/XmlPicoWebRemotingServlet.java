/*******************************************************************************
 * Copyright (c) PicoContainer Organization. All rights reserved.
 * ---------------------------------------------------------------------------
 * The software in this package is published under the terms of the BSD style
 * license a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 ******************************************************************************/
package org.picocontainer.web.remoting;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;

import com.thoughtworks.xstream.XStream;

/**
 * Servlet that uses plain XML as the form of the reply.
 *
 * @author Paul Hammant
 */
@SuppressWarnings("serial")
public class XmlPicoWebRemotingServlet extends AbstractPicoWebRemotingServlet  {

   public void init(ServletConfig servletConfig) throws ServletException {
        setXStream(new XStream());
        super.init(servletConfig);
    }
}