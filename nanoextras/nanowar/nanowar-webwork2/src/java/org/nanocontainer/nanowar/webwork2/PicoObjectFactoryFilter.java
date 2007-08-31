/*****************************************************************************
 * Copyright (C) NanoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the LICENSE.txt file.                                                     *
 *                                                                           *
 *****************************************************************************/
package org.nanocontainer.nanowar.webwork2;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

import org.picocontainer.ObjectReference;
import org.picocontainer.references.ThreadLocalReference;

import com.opensymphony.xwork.ObjectFactory;

/**
 * Filter which initialises a PicoObjectFactory as the XWork ObjectFactory
 * and passes to it the HttpServletRequest.
 * 
 * @author Jonas Engman
 */
public class PicoObjectFactoryFilter implements Filter {

	private final static String ALREADY_FILTERED_KEY = "nanocontainer_objectfactory_filtered";

	private ObjectReference objectReference;

	public void init(FilterConfig config) throws ServletException {
		objectReference = new ThreadLocalReference();
		ObjectFactory.setObjectFactory(new PicoObjectFactory(objectReference));
	}

	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
		HttpServletRequest httpServletRequest = (HttpServletRequest) request;
		if (httpServletRequest.getAttribute(ALREADY_FILTERED_KEY) == null) {
			httpServletRequest.setAttribute(ALREADY_FILTERED_KEY, Boolean.TRUE);
			objectReference.set(httpServletRequest);
			try {
				chain.doFilter(request, response);
			} finally {
				objectReference.set(null);
			}
		} else {
			chain.doFilter(request, response);
		}
	}

	public void destroy() {
	}
}
