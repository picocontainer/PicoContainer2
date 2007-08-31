/*****************************************************************************
 * Copyright (C) PicoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the LICENSE.txt file.                                                     *
 *                                                                           *
 *****************************************************************************/
package org.picocontainer.references;

import org.picocontainer.ObjectReference;

import java.io.Serializable;
import java.io.ObjectOutputStream;
import java.io.ObjectInputStream;

/** @author Paul Hammant */
public class ThreadLocalReference<T> extends ThreadLocal<T> implements ObjectReference<T>, Serializable {

    private static final long serialVersionUID = 1L;

    private void writeObject(final ObjectOutputStream out) {
        if(out != null); // eliminate warning because of unused parameter
    }

    private void readObject(final ObjectInputStream in) {
        if(in != null); // eliminate warning because of unused parameter
    }

}
