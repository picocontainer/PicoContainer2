/*****************************************************************************
 * Copyright (c) PicoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the LICENSE.txt file.                                                     *
 *                                                                           *
 * Idea by Rachel Davies, Original code by Aslak Hellesoy and Paul Hammant   *
 *****************************************************************************/

package org.picocontainer.behaviors;

import org.picocontainer.ComponentAdapter;
import org.picocontainer.Behavior;
import org.picocontainer.PicoContainer;
import org.picocontainer.PicoCompositionException;
import org.picocontainer.LifecycleStrategy;
import org.picocontainer.ObjectReference;

import java.io.Serializable;

/**
 * <p>
 * This behavior supports caches values per thread.
 * </p>
 *
 * @author Paul Hammant
 */
public final class ThreadCached extends Cached implements Behavior {

    public ThreadCached(ComponentAdapter delegate) {
        super(delegate, new ThreadLocalReference());
    }

    public static class ThreadLocalReference extends ThreadLocal implements ObjectReference, Serializable {
    }

    public String toString() {
        return "Thread" + super.toString();
    }
}