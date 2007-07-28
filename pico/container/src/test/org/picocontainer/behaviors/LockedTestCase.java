/*****************************************************************************
 * Copyright (C) PicoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the LICENSE.txt file.                                                     *
 *                                                                           *
 * Original code by                                                          *
 *****************************************************************************/
package org.picocontainer.behaviors;

import org.picocontainer.ComponentAdapter;
import org.picocontainer.BehaviorFactory;

/** @author Paul Hammant */
public final class LockedTestCase extends SynchronizedTestCase {

    protected ComponentAdapter makeComponentAdapter(ComponentAdapter componentAdapter) {
        return new Locked(componentAdapter);
    }

    protected BehaviorFactory makeBehaviorFactory() {
        return new Locking();
    }


}