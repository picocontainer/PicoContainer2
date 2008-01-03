/*****************************************************************************
 * Copyright (C) PicoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the LICENSE.txt file.                                                     *
 *****************************************************************************/
package org.picocontainer.lifecycle;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.picocontainer.lifecycle.LifecycleState.CONSTRUCTED;
import static org.picocontainer.lifecycle.LifecycleState.DISPOSED;
import static org.picocontainer.lifecycle.LifecycleState.STARTED;
import static org.picocontainer.lifecycle.LifecycleState.STOPPED;

import org.junit.Test;

/**
 * @author Michael Rimov
 */
public class LifecycleStateTestCase {

	@Test public void testIsStartAllowedOptions() {
		assertTrue(CONSTRUCTED.isStartAllowed());
		assertFalse(STARTED.isStartAllowed());
		assertTrue(STOPPED.isStartAllowed());
		assertFalse(DISPOSED.isStartAllowed());
	}

	@Test public void testIsStopAllowedOptions() {
		assertFalse(CONSTRUCTED.isStopAllowed());
		assertTrue(STARTED.isStopAllowed());
		assertFalse(STOPPED.isStopAllowed());
		assertFalse(DISPOSED.isStopAllowed());
	}

	@Test public void testIsDisposeAllowedOptions() {
		assertTrue(CONSTRUCTED.isDisposedAllowed());
		assertFalse(STARTED.isDisposedAllowed());
		assertTrue(STOPPED.isDisposedAllowed());
		assertFalse(DISPOSED.isDisposedAllowed());
	}
	
}
