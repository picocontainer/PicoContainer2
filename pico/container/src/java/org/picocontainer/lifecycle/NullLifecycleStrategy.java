/*****************************************************************************
 * Copyright (C) PicoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the LICENSE.txt file.                                                     *
 *                                                                           *
 * Original code by                                                          *
 *****************************************************************************/
package org.picocontainer.lifecycle;

import org.picocontainer.LifecycleStrategy;

import java.io.Serializable;

public class NullLifecycleStrategy implements LifecycleStrategy, Serializable {

    public void start(Object component) {
    }

    public void stop(Object component) {
    }

    public void dispose(Object component) {
    }

    public boolean hasLifecycle(Class type) {
        return false;
    }
}
