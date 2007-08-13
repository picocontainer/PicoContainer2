/*****************************************************************************
 * Copyright (c) PicoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the LICENSE.txt file.                                                     *
 *                                                                           *
 * Idea by Rachel Davies, Original code by Aslak Hellesoy and Paul Hammant   *
 *****************************************************************************/

package org.picocontainer.behaviors;

import org.picocontainer.ComponentAdapter;
import org.picocontainer.Parameter;
import org.picocontainer.PicoCompositionException;
import org.picocontainer.Characteristics;
import org.picocontainer.ComponentMonitor;
import org.picocontainer.behaviors.AbstractBehaviorFactory;
import org.picocontainer.LifecycleStrategy;
import org.picocontainer.ObjectReference;

import java.util.Properties;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Paul Hammant
 */
public class StoreCaching extends AbstractBehaviorFactory {

    private final MapThreadLocalObjectReference mapThreadLocalObjectReference = new MapThreadLocalObjectReference();

    public ComponentAdapter createComponentAdapter(ComponentMonitor componentMonitor, LifecycleStrategy lifecycleStrategy, Properties componentProperties, Object componentKey, Class componentImplementation, Parameter... parameters)
            throws PicoCompositionException {
        if (removePropertiesIfPresent(componentProperties, Characteristics.NO_CACHE)) {
            return super.createComponentAdapter(componentMonitor,
                                                                             lifecycleStrategy,
                                                                             componentProperties,
                                                                             componentKey,
                                                                             componentImplementation,
                                                                             parameters);
        }
        removePropertiesIfPresent(componentProperties, Characteristics.CACHE);
        return new StoreCached(super.createComponentAdapter(componentMonitor, lifecycleStrategy,
                                                                componentProperties, componentKey, componentImplementation, parameters),
                          mapThreadLocalObjectReference);

    }

    public ComponentAdapter addComponentAdapter(ComponentMonitor componentMonitor,
                                    LifecycleStrategy lifecycleStrategy,
                                    Properties componentProperties,
                                    ComponentAdapter adapter) {
        if (removePropertiesIfPresent(componentProperties, Characteristics.NO_CACHE)) {
            return super.addComponentAdapter(componentMonitor, lifecycleStrategy, componentProperties, adapter);
        }
        removePropertiesIfPresent(componentProperties, Characteristics.CACHE);
        return new StoreCached(super.addComponentAdapter(componentMonitor, lifecycleStrategy, componentProperties, adapter), mapThreadLocalObjectReference);
    }


    public Map getCacheForThread() {
        return (Map)mapThreadLocalObjectReference.get();
    }

    public void putCacheForThread(Map keysAndInstances) {
        mapThreadLocalObjectReference.set(keysAndInstances);
    }

    public static class MapThreadLocalObjectReference extends ThreadLocal implements ObjectReference {

        protected Object initialValue() {
            return new HashMap();
        }
    }
}