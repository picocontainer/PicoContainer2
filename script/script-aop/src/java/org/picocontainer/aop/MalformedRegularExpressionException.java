/*****************************************************************************
 * Copyright (c) PicoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the LICENSE.txt file.                                                     *
 *                                                                           *
 * Idea by Rachel Davies, Original code by various                           *
 *****************************************************************************/
package org.picocontainer.aop;

import org.picocontainer.PicoException;

/**
 * Exception thrown by <code>PointcutsFactory</code> when it is passed an
 * invalid regular expression.
 *
 * @author Stephen Molitor
 */
public class MalformedRegularExpressionException extends PicoException {

    /**
     * Creates a new <code>MalformedRegularExpression</code> object.
     *
     * @param message the error message.
     * @param cause   the original exception that caused this error.
     */
    public MalformedRegularExpressionException(final String message, final Throwable cause) {
        super(message, cause);
    }

}