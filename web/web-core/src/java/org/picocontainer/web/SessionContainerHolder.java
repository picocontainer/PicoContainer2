/*******************************************************************************
 * Copyright (C) PicoContainer Organization. All rights reserved.
 * --------------------------------------------------------------------------
 * The software in this package is published under the terms of the BSD style
 * license a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 ******************************************************************************/
package org.picocontainer.web;

import org.picocontainer.MutablePicoContainer;
import org.picocontainer.behaviors.Storing;

public class SessionContainerHolder extends StoringContainerHolder {
    public SessionContainerHolder(MutablePicoContainer container, Storing storing, ThreadLocalLifecycleState lifecycleState) {
        super(container, storing, lifecycleState);
    }
}