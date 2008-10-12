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

public class StoringContainerHolder extends ContainerHolder {

    private final Storing storing;
    private final ThreadLocalLifecycleState lifecycleState;

    public StoringContainerHolder(MutablePicoContainer container, Storing storing, ThreadLocalLifecycleState lifecycleState) {
        super(container);
        this.storing = storing;
        this.lifecycleState = lifecycleState;
    }

    Storing getStoring() {
        return storing;
    }

    ThreadLocalLifecycleState getLifecycleStateModel() {
        return lifecycleState;
    }

}

