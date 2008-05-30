/*******************************************************************************
 * Copyright (C) PicoContainer Organization. All rights reserved.
 * ---------------------------------------------------------------------------
 * The software in this package is published under the terms of the BSD style
 * license a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 ******************************************************************************/
package org.picocontainer.script;

import org.picocontainer.Characteristics;
import org.picocontainer.DefaultPicoContainer;
import org.picocontainer.PicoContainer;

/**
 * Default builder creates an empty caching DefaultPicoContainer 
 */
public class DefaultContainerBuilder extends AbstractContainerBuilder {

    public DefaultContainerBuilder() {
    }

    // TODO better solution to activate default caching
    protected PicoContainer createContainer(PicoContainer parentContainer, Object assemblyScope) {
        return (new DefaultPicoContainer(parentContainer)).change(Characteristics.CACHE);
    }
}