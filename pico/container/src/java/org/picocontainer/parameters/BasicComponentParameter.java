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
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.annotation.Annotation;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.picocontainer.ComponentAdapter;
import org.picocontainer.Parameter;
import org.picocontainer.NameBinding;
import org.picocontainer.PicoContainer;
import org.picocontainer.PicoVisitor;
import org.picocontainer.DefaultPicoContainer;
import org.picocontainer.injectors.AbstractInjector;
import org.picocontainer.injectors.InjectInto;

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
@SuppressWarnings("serial")
public class BasicComponentParameter extends AbstractParameter implements Parameter, Serializable {

    private static interface Converter {
        Object convert(String paramValue);
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
        stringConverters.put(Integer.class, new Converter(){
            public Object convert(String paramValue) {
                return Integer.valueOf(paramValue);
            }
        });
        stringConverters.put(Double.class, new Converter() {
            public Object convert(String paramValue) {
                return Double.valueOf(paramValue);
            }
        });
        stringConverters.put(Boolean.class, new Converter(){
            public Object convert(String paramValue) {
                return Boolean.valueOf(paramValue);
            }
        });
        stringConverters.put(Long.class, new Converter() {
            public Object convert(String paramValue) {
                return Long.valueOf(paramValue);
            }
        });
        stringConverters.put(Float.class, new Converter() {
            public Object convert(String paramValue) {
                return Float.valueOf(paramValue);
            }
        });
        stringConverters.put(Character.class, new Converter() {
            public Object convert(String paramValue) {
                return paramValue.charAt(0);
            }
        });
        stringConverters.put(Byte.class, new Converter() {
            public Object convert(String paramValue) {
                return Byte.valueOf(paramValue);
            }
        });
        stringConverters.put(Short.class, new Converter() {
            public Object convert(String paramValue) {
                return Short.valueOf(paramValue);
            }
        });
        stringConverters.put(File.class, new Converter() {
            public Object convert(String paramValue) {
                return new File(paramValue);
            }
        });

    }


    /**
     * Expect a parameter matching a component of a specific key.
     *
     * @param componentKey the key of the desired addComponent
     */
    public BasicComponentParameter(Object componentKey) {
        this.componentKey = componentKey;
    }

    /** Expect any parameter of the appropriate type. */
    public BasicComponentParameter() {
    }

    /**
     * Check whether the given Parameter can be satisfied by the container.
     *
     * @return <code>true</code> if the Parameter can be verified.
     *
     * @throws org.picocontainer.PicoCompositionException
     *          {@inheritDoc}
     * @see Parameter#isResolvable(PicoContainer, ComponentAdapter, Class, NameBinding ,boolean, Annotation)
     */
    public Resolver resolve(final PicoContainer container,
                            final ComponentAdapter<?> forAdapter,
                            ComponentAdapter<?> injecteeAdapter, final Type expectedType,
                            NameBinding expectedNameBinding, boolean useNames, Annotation binding) {
    	
    	Class<?> resolvedClassType = null;
        // TODO take this out for Pico3
        if (!(expectedType instanceof Class)) {
        	if (expectedType instanceof ParameterizedType) {
        		resolvedClassType = (Class<?>) ((ParameterizedType)expectedType).getRawType();
        	} else {
        		return new Parameter.NotResolved();
        	}
        } else {
        	resolvedClassType = (Class<?>)expectedType;
        }
        assert resolvedClassType != null;

        ComponentAdapter<?> componentAdapter0;
        if (injecteeAdapter == null) {
            componentAdapter0 = resolveAdapter(container, forAdapter, resolvedClassType, expectedNameBinding, useNames, binding);
        } else {
            componentAdapter0 = injecteeAdapter;
        }
        final ComponentAdapter<?> componentAdapter = componentAdapter0;
        return new Resolver() {
            public boolean isResolved() {
                return componentAdapter != null;
            }
            public Object resolveInstance() {
                if (componentAdapter == null) {
                    return null;
                }
                Object o;
                if (componentAdapter instanceof DefaultPicoContainer.LateInstance) {
                    o = ((DefaultPicoContainer.LateInstance) componentAdapter).getComponentInstance();
                } else {
                    o = container.getComponent(componentAdapter.getComponentKey(), new InjectInto(forAdapter.getComponentImplementation(), forAdapter.getComponentKey()));
                }
                if (o instanceof String && expectedType != String.class) {
                    Converter converter = stringConverters.get(expectedType);
                    return converter.convert((String) o);
                }
                return o;

            }

            public ComponentAdapter<?> getComponentAdapter() {
                return componentAdapter;
            }
        };
    }

    public void verify(PicoContainer container,
                       ComponentAdapter<?> forAdapter,
                       Type expectedType,
                       NameBinding expectedNameBinding, boolean useNames, Annotation binding) {
        final ComponentAdapter componentAdapter =
            resolveAdapter(container, forAdapter, (Class<?>)expectedType, expectedNameBinding, useNames, binding);
        if (componentAdapter == null) {
            final Set<Type> set = new HashSet<Type>();
            set.add(expectedType);
            throw new AbstractInjector.UnsatisfiableDependenciesException(forAdapter, null, set, container);
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

    protected <T> ComponentAdapter<T> resolveAdapter(PicoContainer container,
                                                   ComponentAdapter adapter,
                                                   Class<T> expectedType,
                                                   NameBinding expectedNameBinding, boolean useNames, Annotation binding) {
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

        final ComponentAdapter<T> result = getTargetAdapter(container, type, expectedNameBinding, adapter, useNames,
                                                            binding);
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

    private <T> ComponentAdapter<T> getTargetAdapter(PicoContainer container, Class<T> expectedType,
                                                     NameBinding expectedNameBinding,
                                                     ComponentAdapter excludeAdapter, boolean useNames, Annotation binding) {
        if (componentKey != null) {
            // key tells us where to look so we follow
            return typeComponentAdapter(container.getComponentAdapter(componentKey));
        } else if (excludeAdapter == null) {
            return container.getComponentAdapter(expectedType, (NameBinding) null);
        } else {
            return findTargetAdapter(container, expectedType, expectedNameBinding, excludeAdapter, useNames, binding);
        }
    }

    private <T> ComponentAdapter<T> findTargetAdapter(PicoContainer container, Class<T> expectedType,
                                                      NameBinding expectedNameBinding, ComponentAdapter excludeAdapter,
                                                      boolean useNames, Annotation binding) {
        Object excludeKey = excludeAdapter.getComponentKey();
        ComponentAdapter byKey = container.getComponentAdapter((Object)expectedType);
        if (byKey != null && !excludeKey.equals(byKey.getComponentKey())) {
            return typeComponentAdapter(byKey);
        }
        if (useNames) {
            ComponentAdapter found = container.getComponentAdapter(expectedNameBinding.getName());
            if ((found != null) && areCompatible(expectedType, found) && found != excludeAdapter) {
                return (ComponentAdapter<T>) found;
            }
        }
        List<ComponentAdapter<T>> found = binding == null ? container.getComponentAdapters(expectedType) :
                                          container.getComponentAdapters(expectedType, binding.annotationType());
        removeExcludedAdapterIfApplicable(excludeKey, found);
        if (found.size() == 0) {
            return noMatchingAdaptersFound(container, expectedType, expectedNameBinding, binding);
        } else if (found.size() == 1) {
            return found.get(0);
        } else {
            throw tooManyMatchingAdaptersFound(expectedType, found);
        }
    }

    private <T> ComponentAdapter<T> noMatchingAdaptersFound(PicoContainer container, Class<T> expectedType,
                                                            NameBinding expectedNameBinding, Annotation binding) {
        if (container.getParent() != null) {
            if (binding != null) {
                return container.getParent().getComponentAdapter(expectedType, binding.getClass());
            } else {
                return container.getParent().getComponentAdapter(expectedType, expectedNameBinding);
            }
        } else {
            return null;
        }
    }

    private <T> AbstractInjector.AmbiguousComponentResolutionException tooManyMatchingAdaptersFound(Class<T> expectedType, List<ComponentAdapter<T>> found) {
        Class[] foundClasses = new Class[found.size()];
        for (int i = 0; i < foundClasses.length; i++) {
            foundClasses[i] = found.get(i).getComponentImplementation();
        }
        AbstractInjector.AmbiguousComponentResolutionException exception = new AbstractInjector.AmbiguousComponentResolutionException(expectedType, foundClasses);
        return exception;
    }

    private <T> void removeExcludedAdapterIfApplicable(Object excludeKey, List<ComponentAdapter<T>> found) {
        ComponentAdapter exclude = null;
        for (ComponentAdapter work : found) {
            if (work.getComponentKey().equals(excludeKey)) {
                exclude = work;
                break;
            }
        }
        found.remove(exclude);
    }

    private <T> boolean areCompatible(Class<T> expectedType, ComponentAdapter found) {
        Class foundImpl = found.getComponentImplementation();
        return expectedType.isAssignableFrom(foundImpl) ||
               (foundImpl == String.class && stringConverters.containsKey(expectedType))  ;
    }
}
