/*****************************************************************************
 * Copyright (C) PicoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the LICENSE.txt file.                                                     *
 *                                                                           *
 * Original code by                                                          *
 *****************************************************************************/
package org.picocontainer.parameters;

import java.io.File;
import java.io.Serializable;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.picocontainer.ComponentAdapter;
import org.picocontainer.Parameter;
import org.picocontainer.ParameterName;
import org.picocontainer.PicoContainer;
import org.picocontainer.PicoVisitor;
import org.picocontainer.injectors.AbstractInjector;

/**
 * A BasicComponentParameter should be used to pass in a particular component as argument to a
 * different component's constructor. This is particularly useful in cases where several
 * components of the same type have been registered, but with a different key. Passing a
 * ComponentParameter as a parameter when registering a component will give PicoContainer a hint
 * about what other component to use in the constructor. This Parameter will never resolve
 * against a collecting type, that is not directly registered in the PicoContainer itself.
 *
 * @author Jon Tirs&eacute;n
 * @author Aslak Helles&oslash;y
 * @author J&ouml;rg Schaible
 * @author Thomas Heller
 */
public class BasicComponentParameter implements Parameter, Serializable {

    private static interface Converter {
        Object convert(String paramValue);
    }
    private static class ValueOfConverter implements Converter {
        private Method m;
        private ValueOfConverter(Class clazz) {
            try {
                m = clazz.getMethod("valueOf", String.class);
            } catch (NoSuchMethodException e) {
            }
        }

        public Object convert(String paramValue) {
            try {
                return m.invoke(null, paramValue);
            } catch (IllegalAccessException e) {
            } catch (InvocationTargetException e) {
            }
            return null;

        }
    }
    private static class NewInstanceConverter implements Converter {
        private Constructor c;

        private NewInstanceConverter(Class clazz) {
            try {
                c = clazz.getConstructor(String.class);
            } catch (NoSuchMethodException e) {
            }
        }

        public Object convert(String paramValue) {
            try {
                return c.newInstance(paramValue);
            } catch (IllegalAccessException e) {
            } catch (InvocationTargetException e) {
            } catch (InstantiationException e) {
            }
            return null;
        }
    }

    /** <code>BASIC_DEFAULT</code> is an instance of BasicComponentParameter using the default constructor. */
    public static final BasicComponentParameter BASIC_DEFAULT = new BasicComponentParameter();

    private Object componentKey;


    private static final Map<Class, Converter> stringConverters = new HashMap<Class, Converter>();
    static {
        stringConverters.put(Integer.class, new ValueOfConverter(Integer.class));
        stringConverters.put(Double.class, new ValueOfConverter(Double.class));
        stringConverters.put(Boolean.class, new ValueOfConverter(Boolean.class));
        stringConverters.put(Long.class, new ValueOfConverter(Long.class));
        stringConverters.put(Float.class, new ValueOfConverter(Float.class));
        stringConverters.put(Character.class, new ValueOfConverter(Character.class));
        stringConverters.put(Byte.class, new ValueOfConverter(Byte.class));
        stringConverters.put(Byte.class, new ValueOfConverter(Short.class));
        stringConverters.put(File.class, new NewInstanceConverter(File.class));

    }


    /**
     * Expect a parameter matching a component of a specific key.
     *
     * @param componentKey the key of the desired addComponent
     */
    public BasicComponentParameter(Object componentKey) {
        this.componentKey = componentKey;
    }

    /** Expect any paramter of the appropriate type. */
    public BasicComponentParameter() {
    }

    /**
     * Check wether the given Parameter can be statisfied by the container.
     *
     * @return <code>true</code> if the Parameter can be verified.
     *
     * @throws org.picocontainer.PicoCompositionException
     *          {@inheritDoc}
     * @see Parameter#isResolvable(PicoContainer,ComponentAdapter,Class,ParameterName,boolean)
     */
    public boolean isResolvable(PicoContainer container,
                                ComponentAdapter adapter,
                                Class expectedType,
                                ParameterName expectedParameterName, boolean useNames) {
        return resolveAdapter(container, adapter, (Class<?>)expectedType, expectedParameterName, useNames) != null;
    }

    public <T> T resolveInstance(PicoContainer container,
                                  ComponentAdapter adapter,
                                  Class<T> expectedType,
                                  ParameterName expectedParameterName, boolean useNames) {
        final ComponentAdapter componentAdapter =
            resolveAdapter(container, adapter, (Class<?>)expectedType, expectedParameterName, useNames);
        if (componentAdapter != null) {
            Object o = container.getComponent(componentAdapter.getComponentKey());
            if (o instanceof String && expectedType != String.class) {
                Converter converter = stringConverters.get(expectedType);
                return (T) converter.convert((String) o);
            }
            return (T) o;
        }
        return null;
    }

    public void verify(PicoContainer container,
                       ComponentAdapter adapter,
                       Class expectedType,
                       ParameterName expectedParameterName, boolean useNames) {
        final ComponentAdapter componentAdapter =
            resolveAdapter(container, adapter, (Class<?>)expectedType, expectedParameterName, useNames);
        if (componentAdapter == null) {
            final Set<Class> set = new HashSet<Class>();
            set.add(expectedType);
            throw new AbstractInjector.UnsatisfiableDependenciesException(adapter, null, set, container);
        }
        componentAdapter.verify(container);
    }

    /**
     * Visit the current {@link Parameter}.
     *
     * @see org.picocontainer.Parameter#accept(org.picocontainer.PicoVisitor)
     */
    public void accept(final PicoVisitor visitor) {
        visitor.visitParameter(this);
    }

    private <T> ComponentAdapter<T> resolveAdapter(PicoContainer container,
                                                   ComponentAdapter adapter,
                                                   Class<T> expectedType,
                                                   ParameterName expectedParameterName, boolean useNames) {
        Class type = expectedType;
        if (type.isPrimitive()) {
            String expectedTypeName = expectedType.getName();
            if (expectedTypeName == "int") {
                type = Integer.class;
            } else if (expectedTypeName == "long") {
                type = Long.class;
            } else if (expectedTypeName == "float") {
                type = Float.class;
            } else if (expectedTypeName == "double") {
                type = Double.class;
            } else if (expectedTypeName == "boolean") {
                type = Boolean.class;
            } else if (expectedTypeName == "char") {
                type = Character.class;
            } else if (expectedTypeName == "short") {
                type = Short.class;
            } else if (expectedTypeName == "byte") {
                type = Byte.class;
            }
        }

        final ComponentAdapter<T> result = getTargetAdapter(container, type, expectedParameterName, adapter, useNames);
        if (result == null) {
            return null;
        }

        if (!type.isAssignableFrom(result.getComponentImplementation())) {
            if (!(result.getComponentImplementation() == String.class && stringConverters.containsKey(type))) {
                return null;
            }
        }
        return result;
    }

    @SuppressWarnings({ "unchecked" })
    private static <T> ComponentAdapter<T> typeComponentAdapter(ComponentAdapter<?> componentAdapter) {
        return (ComponentAdapter<T>)componentAdapter;
    }

    private <T> ComponentAdapter<T> getTargetAdapter(PicoContainer container,
                                                     Class<T> expectedType,
                                                     ParameterName expectedParameterName,
                                                     ComponentAdapter excludeAdapter, boolean useNames) {
        if (componentKey != null) {
            // key tells us where to look so we follow
            return typeComponentAdapter(container.getComponentAdapter(componentKey));
        } else if (excludeAdapter == null) {
            return container.getComponentAdapter(expectedType, null);
        } else {

            Object excludeKey = excludeAdapter.getComponentKey();
            ComponentAdapter byKey = container.getComponentAdapter((Object)expectedType);
            if (byKey != null && !excludeKey.equals(byKey.getComponentKey())) {
                return typeComponentAdapter(byKey);
            }
            if (useNames) {
                ComponentAdapter found = container.getComponentAdapter(expectedParameterName.getName());
                if ((found != null)
                    && areCompatible(expectedType, found)
                    && found != excludeAdapter) {
                    return (ComponentAdapter<T>) found;                    
                }
            }
            List<ComponentAdapter<T>> found = container.getComponentAdapters(expectedType);
            ComponentAdapter exclude = null;
            for (ComponentAdapter work : found) {
                if (work.getComponentKey().equals(excludeKey)) {
                    exclude = work;
                }
            }
            found.remove(exclude);
            if (found.size() == 0) {
                if (container.getParent() != null) {
                    return container.getParent().getComponentAdapter(expectedType, expectedParameterName);
                } else {
                    return null;
                }
            } else if (found.size() == 1) {
                return found.get(0);
            } else {
                Class[] foundClasses = new Class[found.size()];
                for (int i = 0; i < foundClasses.length; i++) {
                    foundClasses[i] = found.get(i).getComponentImplementation();
                }
                throw new AbstractInjector.AmbiguousComponentResolutionException(expectedType, foundClasses);
            }
        }
    }

    private <T> boolean areCompatible(Class<T> expectedType, ComponentAdapter found) {
        Class foundImpl = found.getComponentImplementation();
        return expectedType.isAssignableFrom(foundImpl) ||
               (foundImpl == String.class && stringConverters.containsKey(expectedType))  ;
    }
}
