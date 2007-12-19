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
import org.picocontainer.LifecycleStrategy;
import org.picocontainer.references.ThreadLocalMapObjectReference;

import java.io.Serializable;
import java.util.Properties;
import java.util.HashMap;
import java.util.Map;
import java.util.Collections;

/**
 * @author Paul Hammant
 */
public class Storing extends AbstractBehaviorFactory {

    private final StoreThreadLocal mapThreadLocalObjectReference = new StoreThreadLocal();

    public <T> ComponentAdapter<T>  createComponentAdapter(ComponentMonitor componentMonitor, LifecycleStrategy lifecycleStrategy, Properties componentProperties, final Object componentKey, Class<T> componentImplementation, Parameter... parameters)

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
        return new Stored<T>(super.createComponentAdapter(componentMonitor, lifecycleStrategy,
                                                                componentProperties, componentKey, componentImplementation, parameters),
                          new ThreadLocalMapObjectReference(mapThreadLocalObjectReference, componentKey));

    }

    public <T> ComponentAdapter<T> addComponentAdapter(ComponentMonitor componentMonitor,
                                    LifecycleStrategy lifecycleStrategy,
                                    Properties componentProperties,
                                    final ComponentAdapter<T> adapter) {
        if (removePropertiesIfPresent(componentProperties, Characteristics.NO_CACHE)) {
            return super.addComponentAdapter(componentMonitor, lifecycleStrategy, componentProperties, adapter);
        }
        removePropertiesIfPresent(componentProperties, Characteristics.CACHE);

        return new Stored<T>(super.addComponentAdapter(componentMonitor, lifecycleStrategy, componentProperties, adapter),
                          new ThreadLocalMapObjectReference(mapThreadLocalObjectReference, adapter.getComponentKey()));
    }

    public StoreWrapper getCacheForThread() {
        StoreWrapper wrappedMap = new StoreWrapper();
        wrappedMap.wrapped = (Map)mapThreadLocalObjectReference.get();
        return wrappedMap;
    }

    public void putCacheForThread(StoreWrapper wrappedMap) {
        mapThreadLocalObjectReference.set(wrappedMap.wrapped);
    }

    public Map resetCacheForThread() {
        HashMap map = new HashMap();
        mapThreadLocalObjectReference.set(map);
        return map;
    }

    public void invalidateCacheForThread() {
        mapThreadLocalObjectReference.set(Collections.unmodifiableMap(Collections.emptyMap()));
    }

    public static class StoreThreadLocal extends ThreadLocal {
        protected Object initialValue() {
            return new HashMap();
        }
    }
    public static class StoreWrapper implements Serializable {
        private Map wrapped;
    }

}