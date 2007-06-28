/*****************************************************************************
 * Copyright (c) PicoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the LICENSE.txt file.                                                     *
 *                                                                           *
 * Original code by Joerg Schaible                                           *
 *****************************************************************************/

package org.picocontainer.gems.adapters;

import org.picocontainer.ObjectReference;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;


/**
 * An {@link ObjectReference} based on a {@link ThreadLocal}.
 * 
 * @author J&ouml;rg Schaible
 */
public class ThreadLocalReference extends ThreadLocal implements ObjectReference, Serializable {

    private static final long serialVersionUID = 1L;

    private void writeObject(final ObjectOutputStream out) {
        if(out != null); // eliminate warning because of unused parameter
    }

    private void readObject(final ObjectInputStream in) {
        if(in != null); // eliminate warning because of unused parameter
    }
}
