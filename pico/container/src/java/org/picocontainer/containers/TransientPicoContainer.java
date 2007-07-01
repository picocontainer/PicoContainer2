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
import org.picocontainer.injectors.ConstructorInjectionFactory;
import org.picocontainer.behaviors.CachingBehaviorFactory;
import org.picocontainer.lifecycle.NullLifecycleStrategy;
import org.picocontainer.monitors.NullComponentMonitor;

public class TransientPicoContainer extends DefaultPicoContainer {

    public TransientPicoContainer() {
        super(new CachingBehaviorFactory().forThis(new ConstructorInjectionFactory()), NullLifecycleStrategy.getInstance(), null, NullComponentMonitor.getInstance());
    }

    public TransientPicoContainer(PicoContainer parent) {
        super(new CachingBehaviorFactory().forThis(new ConstructorInjectionFactory()), NullLifecycleStrategy.getInstance(), parent, NullComponentMonitor.getInstance());
    }
}
