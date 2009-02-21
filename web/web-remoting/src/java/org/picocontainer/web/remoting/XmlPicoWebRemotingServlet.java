/*******************************************************************************
 * Copyright (c) PicoContainer Organization. All rights reserved.
 * ---------------------------------------------------------------------------
 * The software in this package is published under the terms of the BSD style
 * license a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 ******************************************************************************/
package org.picocontainer.web.remoting;

import com.thoughtworks.xstream.XStream;

/**
 * Servlet that uses plain XML as the form of the reply.
 * 
 * @author Paul Hammant
 */
@SuppressWarnings("serial")
public class XmlPicoWebRemotingServlet extends AbstractPicoWebRemotingServlet {

	@Override
	protected XStream createXStream() {
		return new XStream();
	}
}