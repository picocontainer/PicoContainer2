/*****************************************************************************
 * Copyright (C) NanoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the LICENSE.txt file.                                                     *
 *                                                                           *
 * Original code by                                                          *
 *****************************************************************************/

package org.nanocontainer.integrationkit;

import org.picocontainer.MutablePicoContainer;
import org.picocontainer.PicoContainer;
import org.picocontainer.DefaultPicoContainer;
import org.picocontainer.Characteristics;

public class DefaultContainerBuilder extends AbstractContainerBuilder {
    private final ContainerComposer composer;

    public DefaultContainerBuilder(ContainerComposer composer) {
        this.composer = composer;
    }

    protected void composeContainer(MutablePicoContainer container, Object assemblyScope) {
        composer.composeContainer(container, assemblyScope);
    }

    /**
     * @param parentContainer
     * @param assemblyScope
     * @return
     */
    // TODO better solution to activate default caching
    protected PicoContainer createContainer(PicoContainer parentContainer, Object assemblyScope) {
        return (new DefaultPicoContainer(parentContainer)).change(Characteristics.CACHE);
    }
}