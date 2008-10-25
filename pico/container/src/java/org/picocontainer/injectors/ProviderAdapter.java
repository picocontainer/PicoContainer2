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
import org.picocontainer.Characteristics;

import java.lang.reflect.Type;
import java.lang.reflect.Method;
import java.util.Properties;


/**
 * Providers are a type of Injector that can participate in Injection via a custom method.
 *
 * Implementors of this class must implement a single method called provide.  That method must return
 * the component type intended to be provided.  The method can accept parameters that PicoContainer
 * will satisfy.
 */
public class ProviderAdapter implements org.picocontainer.Injector, Provider {

    private final Object provider;
    private final Method provideMethod;
    private final Class key;
    private Properties properties;

    protected ProviderAdapter() {
        provider = this;
        provideMethod = getProvideMethod(this.getClass());
        key = provideMethod.getReturnType();
        setUseNames(useNames());
    }

    public ProviderAdapter(Object provider) {
        this(provider, false);
    }

    public ProviderAdapter(Object provider, boolean useNames) {
        this.provider = provider;
        provideMethod = getProvideMethod(provider.getClass());
        key = provideMethod.getReturnType();
        setUseNames(useNames);
    }

    private void setUseNames(boolean b) {
        if (b) {
            properties = Characteristics.USE_NAMES;
        } else {
            properties = Characteristics.NONE;
        }
    }

    protected boolean useNames() {
        return false;
    }

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
        return new Reinjector(container).reinject(key, provider.getClass(), provider, properties, new MethodInjection(provideMethod));
    }

    public static Method getProvideMethod(Class clazz) {
        Method provideMethod = null;
        // TODO doPrivileged
        for (Method method : clazz.getDeclaredMethods()) {
            if (method.getName().equals("provide")) {
                if (provideMethod != null) {
                    throw newProviderMethodException("only one");
                }
                provideMethod = method;
            }
        }
        if (provideMethod == null) {
            throw newProviderMethodException("a");
        }
        if (provideMethod.getReturnType() == void.class) {
            throw newProviderMethodException("a non void returning");
        }
        return provideMethod;
    }

    private static PicoCompositionException newProviderMethodException(String str) {
        return new PicoCompositionException("There must be "+ str +" method named 'provide' in the AbstractProvider implementation");
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
