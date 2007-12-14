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
public enum LifecycleState {

	/**
	 * Default state of a container once it has been built.
	 */
	CONSTRUCTED,
	
	/**
	 * 'Start' Lifecycle has been called.
	 */
	STARTED,
	
	/**
	 * 'Stop' lifecycle has been called.
	 */
	STOPPED,
	
	/**
	 * 'Dispose' lifecycle has been called.
	 */
	DISPOSED;
	
	
	/**
	 * Start is normally allowed if the object is constructed or
	 * already stopped.  It is not allowed if the system is already
	 * started or disposed.
	 * @return true if start lifecycle methods should be allowed.
	 */
	public boolean isStartAllowed() {
		if (this.equals(CONSTRUCTED) || this.equals(STOPPED)) {
			return true;
		}
		
		return false;
	}
	
	/**
	 * Returns true if stop is normally allowed in the container
	 * lifecycle.  Stop is normally only allowed while the current
	 * container state is STARTED.
	 * @return true if stop is allowed.
	 */
	public boolean isStopAllowed() {
		if (this.equals(STARTED)) {
			return true;
		}
		
		return false;
	}
	
	public boolean isStarted() {
		return this.equals(STARTED);
	}
	
	/**
	 * Returns true if the dispose lifecycle method is normally called.
	 * Dispose is normally only allowed if the object has not been already
	 * disposed, and it is not started.
	 * @return
	 */
	public boolean isDisposedAllowed() {
		if (this.equals(STOPPED) || this.equals(CONSTRUCTED)) {
			return true;
		}
		
		return false;
	}
}
