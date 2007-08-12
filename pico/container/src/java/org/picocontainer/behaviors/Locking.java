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
import org.picocontainer.Parameter;
import org.picocontainer.ComponentMonitor;
import org.picocontainer.LifecycleStrategy;
import org.picocontainer.Characteristics;
import org.picocontainer.behaviors.AbstractBehaviorFactory;

import java.util.Properties;

/**
 * @author Aslak Helles&oslash;y
 */
public class Locking extends AbstractBehaviorFactory {

    public ComponentAdapter createComponentAdapter(ComponentMonitor componentMonitor,
                                                   LifecycleStrategy lifecycleStrategy,
                                                   Properties componentProperties,
                                                   Object componentKey,
                                                   Class componentImplementation,
                                                   Parameter... parameters) {
        removePropertiesIfPresent(componentProperties, Characteristics.SYNCHRONIZE);
        return new Locked(super.createComponentAdapter(
            componentMonitor,
            lifecycleStrategy,
            componentProperties,
            componentKey,
            componentImplementation,
            parameters));
    }

    public ComponentAdapter addComponentAdapter(ComponentMonitor componentMonitor,
                                                LifecycleStrategy lifecycleStrategy,
                                                Properties componentProperties,
                                                ComponentAdapter adapter) {
        removePropertiesIfPresent(componentProperties, Characteristics.SYNCHRONIZE);
        return new Synchronized(super.addComponentAdapter(componentMonitor,
                                                          lifecycleStrategy,
                                                          componentProperties,
                                                          adapter));
    }
}