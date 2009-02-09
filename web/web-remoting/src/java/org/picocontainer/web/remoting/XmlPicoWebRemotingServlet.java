/*******************************************************************************
 * Copyright (c) PicoContainer Organization. All rights reserved.
 * ---------------------------------------------------------------------------
 * The software in this package is published under the terms of the BSD style
 * license a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 ******************************************************************************/
package org.picocontainer.web.remoting;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.json.JsonWriter;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import java.util.logging.Logger;
import java.util.logging.Level;

/**
 * All for the calling of methods in a tree of components manages by PicoContainer.
 * XML is the form of the reply, the request is plainly mapped from Query Strings
 * and form fields to the method signature.
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