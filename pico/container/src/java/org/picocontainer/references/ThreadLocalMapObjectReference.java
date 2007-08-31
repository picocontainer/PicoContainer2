/*****************************************************************************
 * Copyright (C) PicoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the LICENSE.txt file.                                                     *
 *                                                                           *
 *****************************************************************************/
package org.picocontainer.references;

import org.picocontainer.ObjectReference;

import java.util.Map;

/** @author Paul Hammant */
public  class ThreadLocalMapObjectReference implements ObjectReference {
    private final ThreadLocal threadLocal;
    private final Object componentKey;

    public ThreadLocalMapObjectReference(ThreadLocal threadLocal, Object componentKey) {
        this.threadLocal = threadLocal;
        this.componentKey = componentKey;
    }

    public Object get() {
        return ((Map)threadLocal.get()).get(componentKey) ;
    }

    public void set(Object item) {
        ((Map)threadLocal.get()).put(componentKey, item) ;

    }
}
