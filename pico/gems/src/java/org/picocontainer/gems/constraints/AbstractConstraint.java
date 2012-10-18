/*****************************************************************************
 * Copyright (C) PicoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the LICENSE.txt file.                                                     *
 *****************************************************************************/

package org.picocontainer.gems.constraints;

import org.picocontainer.Behavior;
import org.picocontainer.ComponentAdapter;
import org.picocontainer.LifecycleStrategy;
import org.picocontainer.NameBinding;
import org.picocontainer.Parameter;
import org.picocontainer.PicoCompositionException;
import org.picocontainer.PicoContainer;
import org.picocontainer.adapters.InstanceAdapter;
import org.picocontainer.injectors.AbstractInjector;
import org.picocontainer.parameters.CollectionComponentParameter;

import java.lang.annotation.Annotation;
import java.lang.reflect.Array;
import java.lang.reflect.Type;
import java.util.Collection;
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

    @Override
	public Resolver resolve(final PicoContainer container,
                         final ComponentAdapter<?> forAdapter,
                         ComponentAdapter<?> injecteeAdapter, final Type expectedType,
                         final NameBinding expectedNameBinding, final boolean useNames, final Annotation binding) throws PicoCompositionException {
        final Resolver resolver;
        return new Parameter.DelegateResolver(super.resolve(container, forAdapter,
                null, getArrayType((Class) expectedType), expectedNameBinding, useNames, binding)) {
            @Override
            public Object resolveInstance() {
                final Object[] array = (Object[]) super.resolveInstance();
                if (array.length == 1) {
                    return array[0];
                }
                return null;
            }
        };
    }

    @Override
	public void verify(final PicoContainer container,
                       final ComponentAdapter<?> adapter,
                       final Type expectedType,
                       final NameBinding expectedNameBinding, final boolean useNames, final Annotation binding) throws PicoCompositionException {
        super.verify(container, adapter, getArrayType((Class) expectedType), expectedNameBinding, useNames, binding);
    }

    @Override
	public abstract boolean evaluate(ComponentAdapter adapter);

    @Override
	protected Map<Object, ComponentAdapter<?>> getMatchingComponentAdapters(final PicoContainer container,
                                                                            final ComponentAdapter adapter,
                                                                            final Class keyType,
                                                                            final Class valueType) {
        final Map<Object, ComponentAdapter<?>> map =
            super.getMatchingComponentAdapters(container, adapter, keyType, valueType);
        if (map.size() > 1) {
            String[] foundStrings = makeFoundAmbiguousStrings(map.values());
            throw new AbstractInjector.AmbiguousComponentResolutionException(valueType, foundStrings);
        }
        return map;
    }

    public static String[] makeFoundAmbiguousStrings(Collection<ComponentAdapter<?>> found) {
        String[] foundStrings = new String[found.size()];
        int ix = 0;
        for (ComponentAdapter<?> f : found) {
            while (f instanceof Behavior || (f instanceof LifecycleStrategy && !(f instanceof InstanceAdapter))) {
                f = f.getDelegate();
            }
            foundStrings[ix++] = f.toString();
        }
        return foundStrings;
    }


    private Type getArrayType(final Class expectedType) {
        return Array.newInstance(expectedType, 0).getClass();
    }
}
