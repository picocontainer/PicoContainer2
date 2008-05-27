/*****************************************************************************
 * Copyright (C) PicoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the LICENSE.txt file.                                                     *
 *****************************************************************************/
package org.picocontainer.lifecycle;

import org.picocontainer.PicoCompositionException;

import java.io.Serializable;

public class DefaultLifecycleState implements LifecycleState, Serializable {

    private static final long serialVersionUID = 8340827708493546386L;

    /**
	 * Default state of a container once it has been built.
	 */
	private static final String CONSTRUCTED = "CONSTRUCTED";

	/**
	 * 'Start' Lifecycle has been called.
	 */
	private static final String STARTED = "STARTED";

	/**
	 * 'Stop' lifecycle has been called.
	 */
	private static final String STOPPED = "STOPPED";

	/**
	 * 'Dispose' lifecycle has been called.
	 */
	private static final String DISPOSED = "DISPOSED";

    private String state = CONSTRUCTED;

    public void removingComponent() {
        if (state == STARTED) {
            throw new PicoCompositionException("Cannot remove components after the container has started");
        }

        if (state == DISPOSED) {
            throw new PicoCompositionException("Cannot remove components after the container has been disposed");
        }
    }

    /** {@inheritDoc} **/
    public void starting() {
		if (state == CONSTRUCTED || state == STOPPED) {
            state = STARTED;
			return;
		}
	    throw new IllegalStateException("Cannot start.  Current container state was: " + state);
    }


    /** {@inheritDoc} **/
    public void stopping() {
        if (!(state == STARTED)) {
            throw new IllegalStateException("Cannot stop.  Current container state was: " + state);
        }
    }

    public void stopped() {
        state = STOPPED;
    }

    public boolean isStarted() {
        return state == STARTED;
    }

    /** {@inheritDoc} **/
    public void disposing() {
        if (!(state == STOPPED || state == CONSTRUCTED)) {
            throw new IllegalStateException("Cannot dispose.  Current lifecycle state is: " + state);
        }

    }

    public void disposed() {
        state = DISPOSED;
    }

}
