/*****************************************************************************
 * Copyright (C) PicoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the LICENSE.txt file.                                                     *
 *****************************************************************************/
package org.picocontainer.lifecycle;

/**
 * Current lifecycle state of the container.
 * @author Michael Rimov
 */
public interface LifecycleState {

    void removingComponent();

	/**
	 * Start is normally allowed if the object is constructed or
	 * already stopped.  It is not allowed if the system is already
	 * started or disposed.
	 * @return true if start lifecycle methods should be allowed.
	 */
    void starting();

    /**
     * Stop is normally only allowed while the current
     * container state is STARTED.
     * @return true if stop is allowed.
     */
    void stopping();

    void stopped();

    boolean isStarted();

    /**
     * Dispose is normally only allowed if the object has not been already
     * disposed, and it is not started.
     * @return
     */
    void disposing();

    void disposed();
}
