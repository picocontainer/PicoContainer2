/*****************************************************************************
 * Copyright (C) PicoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the LICENSE.txt file.                                                     *
 *****************************************************************************/

package org.picocontainer.gems.constraints;

import org.picocontainer.ComponentAdapter;
import org.picocontainer.ParameterName;
import org.picocontainer.PicoContainer;
import org.picocontainer.PicoCompositionException;
import org.picocontainer.injectors.AbstractInjector;
import org.picocontainer.parameters.CollectionComponentParameter;

import java.lang.reflect.Array;
import java.util.Map;

/**
 * Base class for parameter constraints.
 *
 * @author Nick Sieger
 */
public abstract class AbstractConstraint extends CollectionComponentParameter implements Constraint {

    /** Construct an AbstractContraint. */
    protected AbstractConstraint() {
        super(false);
    }

    public Object resolveInstance(PicoContainer container,
                                  ComponentAdapter adapter,
                                  Class expectedType,
                                  ParameterName expectedParameterName, boolean useNames) throws PicoCompositionException
    {
        final Object[] array =
            (Object[])super.resolveInstance(container, adapter, getArrayType(expectedType), expectedParameterName,
                                            useNames);
        if (array.length == 1) {
            return array[0];
        }
        return null;
    }

    public boolean isResolvable(PicoContainer container,
                                ComponentAdapter adapter,
                                Class expectedType,
                                ParameterName expectedParameterName, boolean useNames) throws PicoCompositionException
    {
        return super.isResolvable(container, adapter, getArrayType(expectedType), expectedParameterName, useNames);
    }

    public void verify(PicoContainer container,
                       ComponentAdapter adapter,
                       Class expectedType,
                       ParameterName expectedParameterName, boolean useNames) throws PicoCompositionException
    {
        super.verify(container, adapter, getArrayType(expectedType), expectedParameterName, useNames);
    }

    public abstract boolean evaluate(ComponentAdapter adapter);

    protected Map<Object, ComponentAdapter<?>> getMatchingComponentAdapters(PicoContainer container,
                                                                            ComponentAdapter adapter,
                                                                            Class keyType,
                                                                            Class valueType)
    {
        final Map<Object, ComponentAdapter<?>> map =
            super.getMatchingComponentAdapters(container, adapter, keyType, valueType);
        if (map.size() > 1) {
            throw new AbstractInjector.AmbiguousComponentResolutionException(valueType, map.keySet().toArray(new Object[map.size()]));
        }
        return map;
    }

    private Class getArrayType(Class expectedType) {
        return Array.newInstance(expectedType, 0).getClass();
    }
}
