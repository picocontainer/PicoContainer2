package org.picocontainer.injectors;

import org.picocontainer.*;

import java.lang.reflect.Type;

public class CompositeInjector<T> extends AbstractInjector<T> {

    private final Injector<T>[] injectors;

    public CompositeInjector(Object componentKey, Class<?> componentImplementation, Parameter[] parameters, ComponentMonitor monitor, LifecycleStrategy lifecycleStrategy,
                             boolean useNames, Injector... injectors) {
        super(componentKey, componentImplementation, parameters, monitor, lifecycleStrategy, useNames);
        this.injectors = injectors;
    }


    public T getComponentInstance(PicoContainer container) throws PicoCompositionException {
        return getComponentInstance(container, null);
    }

    public T getComponentInstance(PicoContainer container, Type into) throws PicoCompositionException {
        T instance = null;
        for (int i = 0; i < injectors.length; i++) {
            Injector<T> injector = injectors[i];
            if (instance == null) {
                instance = injector.getComponentInstance(container);
            } else {
                instance = injector.decorateComponentInstance(container, into, instance);
            }
        }
        return (T) instance;
    }


    public T decorateComponentInstance(PicoContainer container, Type into, T instance) {
        return null; // not Applicable
    }

    public void verify(PicoContainer container) throws PicoCompositionException {
        for (int i = 0; i < injectors.length; i++) {
            injectors[i].verify(container);
        }
    }

    public void accept(PicoVisitor visitor) {
        for (int i = 0; i < injectors.length; i++) {
            injectors[i].accept(visitor);
        }
    }

    public String getDescriptor() {
        return "CompositeInjector";
    }
}
