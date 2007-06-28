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

import org.picocontainer.ComponentAdapter;
import org.picocontainer.Parameter;
import org.picocontainer.ParameterName;
import org.picocontainer.PicoContainer;
import org.picocontainer.PicoVisitor;
import org.picocontainer.injectors.AbstractInjector;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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
 * @version $Revision$
 */
public class BasicComponentParameter
    implements Parameter, Serializable
{

    /** <code>BASIC_DEFAULT</code> is an instance of BasicComponentParameter using the default constructor. */
    public static final BasicComponentParameter BASIC_DEFAULT = new BasicComponentParameter();

    private Object componentKey;

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
     * @see org.picocontainer.Parameter#isResolvable(org.picocontainer.PicoContainer,org.picocontainer.ComponentAdapter,Class,org.picocontainer.ParameterName)
     */
    public boolean isResolvable(PicoContainer container,
                                ComponentAdapter adapter,
                                Class expectedType,
                                ParameterName expectedParameterName)
    {
        return resolveAdapter(container, adapter, (Class<?>)expectedType, expectedParameterName) != null;
    }

    public Object resolveInstance(PicoContainer container,
                                  ComponentAdapter adapter,
                                  Class expectedType,
                                  ParameterName expectedParameterName)
    {
        final ComponentAdapter componentAdapter =
            resolveAdapter(container, adapter, (Class<?>)expectedType, expectedParameterName);
        if (componentAdapter != null) {
            return container.getComponent(componentAdapter.getComponentKey());
        }
        return null;
    }

    public void verify(PicoContainer container,
                       ComponentAdapter adapter,
                       Class expectedType,
                       ParameterName expectedParameterName)
    {
        final ComponentAdapter componentAdapter =
            resolveAdapter(container, adapter, (Class<?>)expectedType, expectedParameterName);
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
                                                   ParameterName expectedParameterName)
    {

        final ComponentAdapter<T> result = getTargetAdapter(container, expectedType, expectedParameterName, adapter);
        if (result == null) {
            return null;
        }

        if (!expectedType.isAssignableFrom(result.getComponentImplementation())) {
            // check for primitive value
            if (expectedType.isPrimitive()) {
                try {
                    final Field field = result.getComponentImplementation().getField("TYPE");
                    final Class type = (Class)field.get(result.getComponentInstance(null));
                    if (expectedType.isAssignableFrom(type)) {
                        return result;
                    }
                } catch (NoSuchFieldException e) {
                    //ignore
                } catch (IllegalArgumentException e) {
                    //ignore
                } catch (IllegalAccessException e) {
                    //ignore
                } catch (ClassCastException e) {
                    //ignore
                }
            }
            return null;
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
                                                     ComponentAdapter excludeAdapter)
    {
        if (componentKey != null) {
            // key tells us where to look so we follow
            return typeComponentAdapter(container.getComponentAdapter(componentKey));
        } else if (excludeAdapter == null) {
            return container.getComponentAdapter(expectedType);
        } else {
            Object excludeKey = excludeAdapter.getComponentKey();
            ComponentAdapter byKey = container.getComponentAdapter((Object)expectedType);
            if (byKey != null && !excludeKey.equals(byKey.getComponentKey())) {
                return typeComponentAdapter(byKey);
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
                    return container.getParent().getComponentAdapter(expectedType);
                } else {
                    return null;
                }
            } else if (found.size() == 1) {
                return found.get(0);
            } else {
                for (ComponentAdapter<T> componentAdapter : found) {
                    Object key = componentAdapter.getComponentKey();
                    if (key instanceof String && key.equals(expectedParameterName.getParameterName())) {
                        return componentAdapter;
                    }
                }

                Class[] foundClasses = new Class[found.size()];
                for (int i = 0; i < foundClasses.length; i++) {
                    foundClasses[i] = found.get(i).getComponentImplementation();
                }
                throw new AbstractInjector.AmbiguousComponentResolutionException(expectedType, foundClasses);
            }
        }
    }
}
