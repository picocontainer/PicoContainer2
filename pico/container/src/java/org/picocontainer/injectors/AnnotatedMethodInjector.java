/*****************************************************************************
 * Copyright (C) PicoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the LICENSE.txt file.                                                     *
 *                                                                           *
 * Original code by                                                          *
 *****************************************************************************/
package org.picocontainer.injectors;

import org.picocontainer.Parameter;
import org.picocontainer.ComponentMonitor;
import org.picocontainer.LifecycleStrategy;

import java.lang.reflect.Method;

public class AnnotatedMethodInjector extends SetterInjector {

    private final Class injectionAnnotation;

    public AnnotatedMethodInjector(Object key,
                                   Class impl,
                                   Parameter[] parameters,
                                   ComponentMonitor monitor,
                                   LifecycleStrategy lifecycleStrategy, Class injectionAnnotation, boolean useNames) {
        super(key, impl, parameters, monitor, lifecycleStrategy, "", useNames);
        this.injectionAnnotation = injectionAnnotation;
    }

    protected final boolean isInjectorMethod(Method method) {
        return method.getAnnotation(injectionAnnotation) != null;
    }

    public String toString() {
        return "MethodInjection-" + super.toString();
    }

}
