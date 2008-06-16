/*****************************************************************************
 * Copyright (C) PicoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the LICENSE.txt file.                                                     *
 *                                                                           *
 *****************************************************************************/
package org.picocontainer.injectors;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.Array;
import java.lang.reflect.GenericArrayType;
import java.lang.reflect.TypeVariable;
import java.util.List;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.picocontainer.ComponentAdapter;
import org.picocontainer.Injector;
import org.picocontainer.PicoCompositionException;
import org.picocontainer.PicoContainer;
import org.picocontainer.PicoVisitor;

/**
 * <p>
 * A Injector which provides an custom instance in a factory style
 * </p>
 *
 * @author Paul Hammant
 */
public abstract class FactoryInjector<T> implements Injector<T> {
    private Class key;

    public FactoryInjector() throws PicoCompositionException {
        key = getTypeArguments(FactoryInjector.class, getClass()).get(0);
        if (key == null) {
            key = CantWorkItOut.class;
        }
    }

    public FactoryInjector(Class<T> key) {
        this.key = key;
    }

    // from http://www.artima.com/weblogs/viewpost.jsp?thread=208860

    public static Class<?> getClass(Type type) {
        if (type instanceof Class) {
            return (Class) type;
        } else if (type instanceof ParameterizedType) {
            return getClass(((ParameterizedType) type).getRawType());
        } else if (type instanceof GenericArrayType) {
            Type componentType = ((GenericArrayType) type).getGenericComponentType();
            Class<?> componentClass = getClass(componentType);
            if (componentClass != null) {
                return Array.newInstance(componentClass, 0).getClass();
            } else {
                return null;
            }
        } else {
            return null;
        }
    }

    /**
   * Get the actual type arguments a child class has used to extend a generic base class.
   *
   * @param baseClass the base class
   * @param childClass the child class
   * @return a list of the raw classes for the actual type arguments.
   */
  public static <T> List<Class<?>> getTypeArguments(
    Class<T> baseClass, Class<? extends T> childClass) {
    Map<Type, Type> resolvedTypes = new HashMap<Type, Type>();
    Type type = childClass;
    // start walking up the inheritance hierarchy until we hit baseClass
    while (! getClass(type).equals(baseClass)) {
      if (type instanceof Class) {
        // there is no useful information for us in raw types, so just keep going.
        type = ((Class) type).getGenericSuperclass();
      }
      else {
        ParameterizedType parameterizedType = (ParameterizedType) type;
        Class<?> rawType = (Class) parameterizedType.getRawType();

        Type[] actualTypeArguments = parameterizedType.getActualTypeArguments();
        TypeVariable<?>[] typeParameters = rawType.getTypeParameters();
        for (int i = 0; i < actualTypeArguments.length; i++) {
          resolvedTypes.put(typeParameters[i], actualTypeArguments[i]);
        }

        if (!rawType.equals(baseClass)) {
          type = rawType.getGenericSuperclass();
        }
      }
    }

    // finally, for each actual type argument provided to baseClass, determine (if possible)
    // the raw class for that type argument.
    Type[] actualTypeArguments;
    if (type instanceof Class) {
      actualTypeArguments = ((Class) type).getTypeParameters();
    }
    else {
      actualTypeArguments = ((ParameterizedType) type).getActualTypeArguments();
    }
    List<Class<?>> typeArgumentsAsClasses = new ArrayList<Class<?>>();
    // resolve types by chasing down type variables.
    for (Type baseType: actualTypeArguments) {
      while (resolvedTypes.containsKey(baseType)) {
        baseType = resolvedTypes.get(baseType);
      }
      typeArgumentsAsClasses.add(getClass(baseType));
    }
    return typeArgumentsAsClasses;
  }

    public Object getComponentKey() {
        return key;
    }

    public Class<T> getComponentImplementation() {
        return key;
    }

    public void accept(PicoVisitor visitor) {
        visitor.visitComponentAdapter(this);
    }

    public ComponentAdapter<T> getDelegate() {
        return null;
    }

    public <U extends ComponentAdapter> U findAdapterOfType(Class<U> componentAdapterType) {
        return null;
    }

    public T getComponentInstance(PicoContainer container) {
        throw new UnsupportedOperationException();
    }

    public abstract T getComponentInstance(PicoContainer container, Type clazz);

    public void decorateComponentInstance(PicoContainer container, Type into, T instance) {
    }


    public void verify(PicoContainer container) {
    }

    public String getDescriptor() {
        return "FactoryInjector-";
    }

    public void start(PicoContainer container) {
    }

    public void stop(PicoContainer container) {
    }

    public void dispose(PicoContainer container) {
    }

    public boolean componentHasLifecycle() {
        return false;
    }

    public static class CantWorkItOut {}

}