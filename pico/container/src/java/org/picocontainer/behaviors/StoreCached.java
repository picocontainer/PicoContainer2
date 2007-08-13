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
import org.picocontainer.Behavior;
import org.picocontainer.PicoContainer;
import org.picocontainer.PicoCompositionException;
import org.picocontainer.ObjectReference;

import java.util.Map;

/**
 * <p>
 * This behavior supports caches values per thread.
 * </p>
 *
 * @author Paul Hammant
 */
public final class StoreCached extends Cached implements Behavior {


    public StoreCached(ComponentAdapter delegate, ObjectReference instanceReference) {
        super(delegate, instanceReference);
    }

    public Object getComponentInstance(PicoContainer container) throws PicoCompositionException {
        Map map = (Map)instanceReference.get();
        Object instance = map.get(getComponentKey());
        if (instance == null) {
            instance = delegate.getComponentInstance(container);
            map.put(getComponentKey(), instance);
        }
        return instance;
        
    }

    public String toString() {
        return "Store" + super.toString();
    }
}