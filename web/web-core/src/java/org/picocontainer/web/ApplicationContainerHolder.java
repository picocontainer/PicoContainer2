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

public class ApplicationContainerHolder extends ContainerHolder {
    private Storing sessionStoring;

    public ApplicationContainerHolder(MutablePicoContainer container, Storing sessionStoring) {
        super(container);
        this.sessionStoring = sessionStoring;
    }

    public Storing getSessionStoring() {
        return sessionStoring;
    }
}
