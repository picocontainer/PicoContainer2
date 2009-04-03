/*****************************************************************************
 * Copyright (C) PicoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the LICENSE.txt file.                                                     *
 *                                                                           *
 *****************************************************************************/
package org.picocontainer.injectors;

import java.lang.reflect.Type;
import java.util.logging.Logger;

import org.picocontainer.ComponentMonitor;
import org.picocontainer.Injector;
import org.picocontainer.LifecycleStrategy;
import org.picocontainer.Parameter;
import org.picocontainer.PicoCompositionException;
import org.picocontainer.PicoContainer;
import org.picocontainer.PicoVisitor;
import org.picocontainer.ComponentAdapter;

@SuppressWarnings("serial")
public class CompositeInjector<T> extends AbstractInjector<T> {

    private final Injector<T>[] injectors;

    public CompositeInjector(Object componentKey, Class<?> componentImplementation, Parameter[] parameters, ComponentMonitor monitor, LifecycleStrategy lifecycleStrategy,
                             boolean useNames, Injector... injectors) {
        super(componentKey, componentImplementation, parameters, monitor, lifecycleStrategy, useNames);
        this.injectors = injectors;
    }


    public T getComponentInstance(PicoContainer container) throws PicoCompositionException {
        return getComponentInstance(container, NOTHING.class);
    }

    public T getComponentInstance(PicoContainer container, Type into) throws PicoCompositionException {
        T instance = null;
        for (int i = 0; i < injectors.length; i++) {
            Injector<T> injector = injectors[i];
            if (instance == null) {
                instance = injector.getComponentInstance(container, ComponentAdapter.NOTHING.class);
            } else {
                injector.decorateComponentInstance(container, into, instance);
            }
        }
        return (T) instance;
    }


    /**
     * @return the object returned is the result of the last of the injectors delegated to
     */
    @Override
    public Object decorateComponentInstance(PicoContainer container, Type into, T instance) {
        Object result = null;
        for (int i = 0; i < injectors.length; i++) {
            result = injectors[i].decorateComponentInstance(container, into, instance);
        }        
        return result;
    }

    public void verify(PicoContainer container) throws PicoCompositionException {
        for (int i = 0; i < injectors.length; i++) {
            injectors[i].verify(container);
        }
    }

    public final void accept(PicoVisitor visitor) {
        super.accept(visitor);
        for (int i = 0; i < injectors.length; i++) {
            injectors[i].accept(visitor);
        }
    }

    public String getDescriptor() {
        return "CompositeInjector";
    }
}
