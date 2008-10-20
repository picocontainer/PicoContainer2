/*****************************************************************************
 * Copyright (C) PicoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the LICENSE.txt file.                                                     *
 *                                                                           *
 *****************************************************************************/
package org.picocontainer.injectors;

import org.picocontainer.PicoCompositionException;
import org.picocontainer.PicoContainer;
import org.picocontainer.PicoVisitor;
import org.picocontainer.ComponentAdapter;

import java.lang.reflect.Type;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;


/**
 * Providers are a type of Injector that can participate in Injection via a custom method.
 *
 * Implementors of this class must implement a single method called provide.  That method must return
 * the component type intended to be provided.  The method can accept parameters that PicoContainer
 * will satisfy.
 */
public abstract class Provider implements org.picocontainer.Injector {

    private Method provideMethod = getProvideMethod(this.getClass());
    private Class key = provideMethod.getReturnType();

    public Object decorateComponentInstance(PicoContainer container, Type into, Object instance) {
        return null;
    }

    public Object getComponentKey() {
        return key;
    }

    public Class getComponentImplementation() {
        return key;
    }

    @Deprecated
    public Object getComponentInstance(PicoContainer container) throws PicoCompositionException {
        return getComponentInstance(container, NOTHING.class);
    }

    public Object getComponentInstance(PicoContainer container, Type into) throws PicoCompositionException {
        return new Reinjector(container).reinject(key, provideMethod, this.getClass(), this);
    }

    public static Method getProvideMethod(Class clazz) {
        Method[] methods = clazz.getMethods();
        List<Method> provideMethods = new ArrayList<Method>();
        for (Method method : methods) {
            if (method.getName().equals("provide")) {
                provideMethods.add(method);
            }
        }
        if (provideMethods.size() != 1) {
            throw new PicoCompositionException("There must be one and only one method named 'provide' in AbstractProvider implementation");
        }
        return provideMethods.get(0);
    }

    public void verify(PicoContainer container) throws PicoCompositionException {
    }

    public void accept(PicoVisitor visitor) {
    }

    public ComponentAdapter getDelegate() {
        return null;
    }

    public ComponentAdapter findAdapterOfType(Class componentAdapterType) {
        return null;
    }

    public String getDescriptor() {
        return "AbstractProvider";
    }
}
