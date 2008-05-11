/*****************************************************************************
 * Copyright (C) NanoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the LICENSE.txt file.                                                     *
 *                                                                           *
 * Original code by Joe Walnes                                               *
 *****************************************************************************/
package org.nanocontainer.reflection;

import org.picocontainer.PicoCompositionException;

public class InvalidConversionException extends PicoCompositionException {
    private static final long serialVersionUID = -6121032191716322930L;

    public InvalidConversionException(String message) {
        super(message);
    }
}
