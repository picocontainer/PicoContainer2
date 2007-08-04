/*****************************************************************************
 * Copyright (C) PicoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the LICENSE.txt file.                                                     *
 *                                                                           *
 * Original code by                                                          *
 *****************************************************************************/
package org.picocontainer.containers;

import org.picocontainer.DefaultPicoContainer;
import org.picocontainer.PicoContainer;
import org.picocontainer.injectors.ConstructorInjection;
import org.picocontainer.behaviors.Caching;
import org.picocontainer.lifecycle.NullLifecycleStrategy;
import org.picocontainer.monitors.NullComponentMonitor;

public class TransientPicoContainer extends DefaultPicoContainer {

    public TransientPicoContainer() {
        super(new Caching().wrap(new ConstructorInjection()), new NullLifecycleStrategy(), null, new NullComponentMonitor());
    }

    public TransientPicoContainer(PicoContainer parent) {
        super(new Caching().wrap(new ConstructorInjection()), new NullLifecycleStrategy(), parent, new NullComponentMonitor());
    }
}
