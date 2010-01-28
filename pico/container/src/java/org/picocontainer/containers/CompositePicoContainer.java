/*****************************************************************************
 * Copyright (c) PicoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the LICENSE.txt file.                                                     *
 *                                                                           *
 * Idea by Rachel Davies, Original code by various                           *
 *****************************************************************************/
package org.picocontainer.containers;

import org.picocontainer.*;

import java.lang.reflect.Type;
import java.lang.annotation.Annotation;
import java.io.Serializable;
import java.util.List;
import java.util.Collection;
import java.util.Collections;

/**
 * CompositePicoContainer takes a var-args list of containers and will query them
 * in turn for getComponent(*) and getComponentAdapter(*) requests.  Methods returning
 * lists and getParent/accept will not function.
 */
public class CompositePicoContainer implements PicoContainer, Converting, Serializable {

    private final PicoContainer[] containers;
    private CompositeConverter compositeConverter = new CompositeConverter();

    public class CompositeConverter implements Converting.Converter {
        public boolean canConvert(Type type) {
            for (PicoContainer container : containers) {
                if (container instanceof Converting && ((Converting) container).getConverter().canConvert(type)) {
                    return true;
                }
            }
            return false;
        }

        public Object convert(String paramValue, Type type) {
            for (PicoContainer container : containers) {
                if (container instanceof Converting) {
                    Converting.Converter converter = ((Converting) container).getConverter();
                    if (converter.canConvert(type)) {
                        return converter.convert(paramValue, type);
                    }
                }
            }
            return null;
        }
    }

    public CompositePicoContainer(PicoContainer... containers) {
        this.containers = containers;
    }

    public <T> T getComponent(Class<T> componentType) {
        for (PicoContainer container : containers) {
            T inst = container.getComponent(componentType);
            if (inst != null) {
                return inst;
            }
        }
        return null;
    }

    public Object getComponent(Object componentKeyOrType, Type into) {
        for (PicoContainer container : containers) {
            Object inst = container.getComponent(componentKeyOrType, into);
            if (inst != null) {
                return inst;
            }
        }
        return null;
    }

    public Object getComponent(Object componentKeyOrType) {
        for (PicoContainer container : containers) {
            Object inst = container.getComponent(componentKeyOrType);
            if (inst != null) {
                return inst;
            }
        }
        return null;
    }

    public ComponentAdapter getComponentAdapter(Object componentKey) {
        for (PicoContainer container : containers) {
            ComponentAdapter inst = container.getComponentAdapter(componentKey);
            if (inst != null) {
                return inst;
            }
        }
        return null;
    }

    public <T> ComponentAdapter<T> getComponentAdapter(Class<T> componentType, NameBinding nameBinding) {
        for (PicoContainer container : containers) {
            ComponentAdapter<T> inst = container.getComponentAdapter(componentType, nameBinding);
            if (inst != null) {
                return inst;
            }
        }
        return null;
    }

    public <T> ComponentAdapter<T> getComponentAdapter(Class<T> componentType, Class<? extends Annotation> binding) {
        for (PicoContainer container : containers) {
            ComponentAdapter<T> inst = container.getComponentAdapter(componentType, binding);
            if (inst != null) {
                return inst;
            }
        }
        return null;
    }

    public <T> T getComponent(Class<T> componentType, Class<? extends Annotation> binding) {
        return null;
    }

    public List<Object> getComponents() {
        return Collections.emptyList();
    }

    public PicoContainer getParent() {
        return null;
    }

    public Collection<ComponentAdapter<?>> getComponentAdapters() {
        return Collections.emptyList();
    }

    public <T> List<ComponentAdapter<T>> getComponentAdapters(Class<T> componentType) {
        return Collections.emptyList();
    }

    public <T> List<ComponentAdapter<T>> getComponentAdapters(Class<T> componentType, Class<? extends Annotation> binding) {
        return Collections.emptyList();
    }

    public <T> List<T> getComponents(Class<T> componentType) {
        return Collections.emptyList();
    }

    public void accept(PicoVisitor visitor) {
    }

    public Converting.Converter getConverter() {
        return compositeConverter;
    }
}
