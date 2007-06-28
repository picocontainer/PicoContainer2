/*****************************************************************************
 * Copyright (C) NanoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the LICENSE.txt file.                                                     *
 *                                                                           *
 * Original code by                                                          *
 *****************************************************************************/
package org.nanocontainer.integrationkit;

import org.picocontainer.PicoException;

public class PicoCompositionException extends PicoException {

    public PicoCompositionException(String message, Throwable cause) {
        super(message,cause);
    }

    public PicoCompositionException(Throwable cause) {
        super(cause);
    }

    public PicoCompositionException(String message) {
        super(message);
    }

}
