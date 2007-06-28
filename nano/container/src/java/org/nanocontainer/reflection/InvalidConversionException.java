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

// todo should this extend RTE ??
public class InvalidConversionException extends RuntimeException {
    public InvalidConversionException(String message) {
        super(message);
    }
}
