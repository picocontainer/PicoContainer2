/*****************************************************************************
 * Copyright (C) PicoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the LICENSE.txt file.                                                     *
 *                                                                           *
 * Original code by Centerline Computers, Inc.                               *
 *****************************************************************************/
package org.picocontainer.gems.util;

import org.picocontainer.PicoException;

/**
 * Runtime exception version of {@link java.lang.NoSuchMethodException}.
 * 
 * @author Michael Rimov
 */
public class NoSuchMethodRuntimeException extends PicoException {

	/**
	 * Serialization UUID.
	 */
	private static final long serialVersionUID = -7033093964367172262L;

	/**
	 * 
	 */
	public NoSuchMethodRuntimeException() {
		super();
	}

	/**
	 * @param message
	 */
	public NoSuchMethodRuntimeException(final String message) {
		super(message);
	}

	/**
	 * @param cause
	 */
	public NoSuchMethodRuntimeException(final Throwable cause) {
		super(cause);
	}

	/**
	 * @param message
	 * @param cause
	 */
	public NoSuchMethodRuntimeException(final String message,
			final Throwable cause) {
		super(message, cause);
	}

}
