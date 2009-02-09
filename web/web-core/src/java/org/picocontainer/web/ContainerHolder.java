/*******************************************************************************
 * Copyright (C) PicoContainer Organization. All rights reserved.
 * --------------------------------------------------------------------------
 * The software in this package is published under the terms of the BSD style
 * license a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 ******************************************************************************/
package org.picocontainer.web;

import org.picocontainer.MutablePicoContainer;

import java.io.Serializable;

public class ContainerHolder implements Serializable {

    private final MutablePicoContainer container;

    public ContainerHolder(MutablePicoContainer container) {
        this.container = container;
    }

    MutablePicoContainer getContainer() {
        return container;
    }
}
