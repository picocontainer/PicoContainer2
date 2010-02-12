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

import org.picocontainer.ComponentMonitor;
import org.picocontainer.NameBinding;
import org.picocontainer.Parameter;

import java.lang.reflect.AccessibleObject;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

@SuppressWarnings("serial")
public class NamedMethodInjector<T> extends SetterInjector<T> {

    public NamedMethodInjector(Object key,
                                   Class<?> impl,
                                   Parameter[] parameters,
                                   ComponentMonitor monitor) {
        super(key, impl, parameters, monitor, "set", true);
    }

    public NamedMethodInjector(Object key,
                               Class<?> impl,
                               Parameter[] parameters,
                               ComponentMonitor monitor,
                               String prefix) {
        super(key, impl, parameters, monitor, prefix, true);
    }

    @Override
    protected NameBinding makeParameterNameImpl(final AccessibleObject member) {
        return new NameBinding() {
            public String getName() {
                return NamedMethodInjector.this.getName((Method) member);
            }
        };
    }

    @Override
    protected String getName(Method method) {
        String name = method.getName().substring(prefix.length()); // string off 'set' or chosen prefix
        return name.substring(0,1).toLowerCase() + name.substring(1);
    }

    public String toString() {
        return "NamedMethodInjection-" + super.toString();
    }

}