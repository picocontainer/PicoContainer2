/*****************************************************************************
 * Copyright (c) PicoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the LICENSE.txt file.                                                     *
 *                                                                           *
 *****************************************************************************/
package org.picocontainer.gems.util;

import org.picocontainer.ComponentAdapter;
import org.picocontainer.MutablePicoContainer;
import org.picocontainer.adapters.InstanceAdapter;
import org.picocontainer.DefaultPicoContainer;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;


public class PicoMap implements Map {

    private final MutablePicoContainer mutablePicoContainer;

    public PicoMap(MutablePicoContainer mutablePicoContainer) {
        this.mutablePicoContainer = mutablePicoContainer;
    }

    public PicoMap() {
        mutablePicoContainer = new DefaultPicoContainer();
    }

    public int size() {
        return mutablePicoContainer.getComponentAdapters().size();
    }

    public boolean isEmpty() {
        return mutablePicoContainer.getComponentAdapters().size() == 0;
    }

    public boolean containsKey(Object o) {
        if (o instanceof Class) {
            return mutablePicoContainer.getComponent((Class<?>)o) != null;
        } else {
            return mutablePicoContainer.getComponent(o) != null;
        }
    }

    public boolean containsValue(Object o) {
        return false;
    }

    public Object get(Object o) {
        if (o instanceof Class) {
            return mutablePicoContainer.getComponent((Class<?>)o);
        } else {
            return mutablePicoContainer.getComponent(o);
        }
    }

    public Object put(Object o, Object o1) {
        Object object = remove(o);
        mutablePicoContainer.addComponent(o, o1);
        return object;
    }

    public Object remove(Object o) {
        ComponentAdapter adapter = mutablePicoContainer.removeComponent(o);
        if (adapter != null) {
            // if previously an instance was registered, return it, otherwise return the type
            return adapter instanceof InstanceAdapter ? adapter
                    .getComponentInstance(mutablePicoContainer) : adapter
                    .getComponentImplementation();
        } else {
            return null;
        }
    }

    public void putAll(Map map) {
        for (Object o : map.entrySet()) {
            final Entry entry = (Entry) o;
            put(entry.getKey(), entry.getValue());
        }
    }

    public void clear() {
        Set adapters = keySet();
        for (Object adapter : adapters) {
            mutablePicoContainer.removeComponent(adapter);
        }
    }

    public Set keySet() {
        Set<Object> set = new HashSet<Object>();
        Collection<ComponentAdapter<?>> adapters = mutablePicoContainer.getComponentAdapters();
        for (final ComponentAdapter<?> adapter : adapters) {
            set.add(adapter.getComponentKey());
        }
        return Collections.unmodifiableSet(set);
    }

    @SuppressWarnings({ "unchecked" })
    public Collection values() {
        return Collections.unmodifiableCollection(mutablePicoContainer.getComponents());
    }

    public Set entrySet() {
        Set<Entry> set = new HashSet<Entry>();
        Collection<ComponentAdapter<?>> adapters = mutablePicoContainer.getComponentAdapters();
        for (ComponentAdapter<?> adapter : adapters) {
            final Object key = adapter.getComponentKey();
            final Object component = mutablePicoContainer.getComponent(key);
            set.add(new Entry() {
                public Object getKey() {
                    return key;
                }

                public Object getValue() {
                    return component;
                }

                public Object setValue(Object value) {
                    throw new UnsupportedOperationException("Cannot set addComponent");
                }
            });
        }
        return Collections.unmodifiableSet(set);
    }
}
