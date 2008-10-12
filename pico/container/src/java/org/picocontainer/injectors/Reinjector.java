/*****************************************************************************
 * Copyright (C) PicoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the LICENSE.txt file.                                                     *
 *                                                                           *
 *****************************************************************************/
package org.picocontainer.injectors;

import org.picocontainer.DefaultPicoContainer;
import org.picocontainer.PicoContainer;
import org.picocontainer.containers.TransientPicoContainer;

import java.lang.reflect.Method;

/**
 * Reinjector allows methods on pre-instantiated classes to be invoked, with appropriately scoped parameters.
 */
public class Reinjector {
    
    private final PicoContainer parent;

    public Reinjector(PicoContainer parentContainer) {
        this.parent = parentContainer;
    }

    /**
     * Reinjecting into a method.
     * @param clazz the component-key from the parent set of components to inject into
     * @param reinjectionMethod the reflection method to use for injection.
     * @return the instance, if you did not have it already.
     */
    public <T> T reinject(Class<T> clazz, Method reinjectionMethod) {
        TransientPicoContainer tpc = new TransientPicoContainer(
                new Reinjection(new MethodInjection(reinjectionMethod), parent), parent);
        tpc.addComponent(clazz);
        return tpc.getComponent(clazz);
    }
}
