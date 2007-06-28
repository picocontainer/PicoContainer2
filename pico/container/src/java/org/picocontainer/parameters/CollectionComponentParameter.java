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
import org.picocontainer.PicoCompositionException;
import org.picocontainer.PicoVisitor;

import java.io.Serializable;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;


/**
 * A CollectionComponentParameter should be used to support inject an {@link Array}, a
 * {@link Collection}or {@link Map}of components automatically. The collection will contain
 * all components of a special type and additionally the type of the key may be specified. In
 * case of a map, the map's keys are the one of the component adapter.
 *
 * @author Aslak Helles&oslash;y
 * @author J&ouml;rg Schaible
 * @since 1.1
 */
public class CollectionComponentParameter
    implements Parameter, Serializable
{

    /** Use <code>ARRAY</code> as {@link Parameter}for an Array that must have elements. */
    public static final CollectionComponentParameter ARRAY = new CollectionComponentParameter();
    /**
     * Use <code>ARRAY_ALLOW_EMPTY</code> as {@link Parameter}for an Array that may have no
     * elements.
     */
    public static final CollectionComponentParameter ARRAY_ALLOW_EMPTY = new CollectionComponentParameter(true);

    private final boolean emptyCollection;
    private final Class componentKeyType;
    private final Class componentValueType;

    /**
     * Expect an {@link Array}of an appropriate type as parameter. At least one component of
     * the array's component type must exist.
     */
    public CollectionComponentParameter() {
        this(false);
    }

    /**
     * Expect an {@link Array}of an appropriate type as parameter.
     *
     * @param emptyCollection <code>true</code> if an empty array also is a valid dependency
     *                        resolution.
     */
    public CollectionComponentParameter(boolean emptyCollection) {
        this(Void.TYPE, emptyCollection);
    }

    /**
     * Expect any of the collection types {@link Array},{@link Collection}or {@link Map}as
     * parameter.
     *
     * @param componentValueType the type of the components (ignored in case of an Array)
     * @param emptyCollection    <code>true</code> if an empty collection resolves the
     *                           dependency.
     */
    public CollectionComponentParameter(Class componentValueType, boolean emptyCollection) {
        this(Object.class, componentValueType, emptyCollection);
    }

    /**
     * Expect any of the collection types {@link Array},{@link Collection}or {@link Map}as
     * parameter.
     *
     * @param componentKeyType   the type of the component's key
     * @param componentValueType the type of the components (ignored in case of an Array)
     * @param emptyCollection    <code>true</code> if an empty collection resolves the
     *                           dependency.
     */
    public CollectionComponentParameter(Class componentKeyType, Class componentValueType, boolean emptyCollection) {
        this.emptyCollection = emptyCollection;
        this.componentKeyType = componentKeyType;
        this.componentValueType = componentValueType;
    }

    /**
     * Resolve the parameter for the expected type. The method will return <code>null</code>
     * If the expected type is not one of the collection types {@link Array},
     * {@link Collection}or {@link Map}. An empty collection is only a valid resolution, if
     * the <code>emptyCollection</code> flag was set.
     *
     * @param container             {@inheritDoc}
     * @param adapter               {@inheritDoc}
     * @param expectedType          {@inheritDoc}
     * @param expectedParameterName {@inheritDoc}
     *
     * @return the instance of the collection type or <code>null</code>
     *
     * @throws PicoCompositionException {@inheritDoc}
     */
    @SuppressWarnings({ "unchecked" })
    public Object resolveInstance(PicoContainer container,
                                  ComponentAdapter adapter,
                                  Class expectedType,
                                  ParameterName expectedParameterName)
    {
        // type check is done in isResolvable
        Object result = null;
        final Class collectionType = getCollectionType(expectedType);
        if (collectionType != null) {
            final Map<Object, ComponentAdapter<?>> adapterMap =
                getMatchingComponentAdapters(container, adapter, componentKeyType, getValueType(expectedType));
            if (Array.class.isAssignableFrom(collectionType)) {
                result = getArrayInstance(container, expectedType, adapterMap);
            } else if (Map.class.isAssignableFrom(collectionType)) {
                result = getMapInstance(container, expectedType, adapterMap);
            } else if (Collection.class.isAssignableFrom(collectionType)) {
                result = getCollectionInstance(container, (Class<? extends Collection>)expectedType, adapterMap);
            } else {
                throw new PicoCompositionException(expectedType.getName() + " is not a collective type");
            }
        }
        return result;
    }

    /**
     * Check for a successful dependency resolution of the parameter for the expected type. The
     * dependency can only be satisfied if the expected type is one of the collection types
     * {@link Array},{@link Collection}or {@link Map}. An empty collection is only a valid
     * resolution, if the <code>emptyCollection</code> flag was set.
     *
     * @param container             {@inheritDoc}
     * @param adapter               {@inheritDoc}
     * @param expectedType          {@inheritDoc}
     * @param expectedParameterName {@inheritDoc}
     *
     * @return <code>true</code> if matching components were found or an empty collective type
     *         is allowed
     */
    public boolean isResolvable(PicoContainer container,
                                ComponentAdapter adapter,
                                Class expectedType,
                                ParameterName expectedParameterName)
    {
        final Class collectionType = getCollectionType(expectedType);
        final Class valueType = getValueType(expectedType);
        return collectionType != null && (emptyCollection || getMatchingComponentAdapters(container,
                                                                                          adapter,
                                                                                          componentKeyType,
                                                                                          valueType).size() > 0);
    }

    /**
     * Verify a successful dependency resolution of the parameter for the expected type. The
     * method will only return if the expected type is one of the collection types {@link Array},
     * {@link Collection}or {@link Map}. An empty collection is only a valid resolution, if
     * the <code>emptyCollection</code> flag was set.
     *
     * @param container             {@inheritDoc}
     * @param adapter               {@inheritDoc}
     * @param expectedType          {@inheritDoc}
     * @param expectedParameterName {@inheritDoc}
     *
     * @throws PicoCompositionException {@inheritDoc}
     */
    public void verify(PicoContainer container,
                       ComponentAdapter adapter,
                       Class expectedType,
                       ParameterName expectedParameterName)
    {
        final Class collectionType = getCollectionType(expectedType);
        if (collectionType != null) {
            final Class valueType = getValueType(expectedType);
            final Collection componentAdapters =
                getMatchingComponentAdapters(container, adapter, componentKeyType, valueType).values();
            if (componentAdapters.isEmpty()) {
                if (!emptyCollection) {
                    throw new PicoCompositionException(expectedType.getName()
                                                         + " not resolvable, no components of type "
                                                         + getValueType(expectedType).getName()
                                                         + " available");
                }
            } else {
                for (Object componentAdapter1 : componentAdapters) {
                    final ComponentAdapter componentAdapter = (ComponentAdapter)componentAdapter1;
                    componentAdapter.verify(container);
                }
            }
        } else {
            throw new PicoCompositionException(expectedType.getName() + " is not a collective type");
        }
    }

    /**
     * Visit the current {@link Parameter}.
     *
     * @see org.picocontainer.Parameter#accept(org.picocontainer.PicoVisitor)
     */
    public void accept(final PicoVisitor visitor) {
        visitor.visitParameter(this);
    }

    /**
     * Evaluate whether the given component adapter will be part of the collective type.
     *
     * @param adapter a <code>ComponentAdapter</code> value
     *
     * @return <code>true</code> if the adapter takes part
     */
    protected boolean evaluate(final ComponentAdapter adapter) {
        return adapter != null; // use parameter, prevent compiler warning
    }

    /**
     * Collect the matching ComponentAdapter instances.
     *
     * @param container container to use for dependency resolution
     * @param adapter   {@link ComponentAdapter} to exclude
     * @param keyType   the compatible type of the key
     * @param valueType the compatible type of the addComponent
     *
     * @return a {@link Map} with the ComponentAdapter instances and their component keys as map key.
     */
    @SuppressWarnings({ "unchecked" })
    protected Map<Object, ComponentAdapter<?>> getMatchingComponentAdapters(PicoContainer container,
                                                                            ComponentAdapter adapter,
                                                                            Class keyType,
                                                                            Class valueType)
    {
        final Map<Object, ComponentAdapter<?>> adapterMap = new LinkedHashMap<Object, ComponentAdapter<?>>();
        final PicoContainer parent = container.getParent();
        if (parent != null) {
            adapterMap.putAll(getMatchingComponentAdapters(parent, adapter, keyType, valueType));
        }
        final Collection<ComponentAdapter<?>> allAdapters = container.getComponentAdapters();
        for (ComponentAdapter componentAdapter : allAdapters) {
            adapterMap.remove(componentAdapter.getComponentKey());
        }
        final List<ComponentAdapter> adapterList = container.getComponentAdapters(valueType);
        for (ComponentAdapter componentAdapter : adapterList) {
            final Object key = componentAdapter.getComponentKey();
            if (adapter != null && key.equals(adapter.getComponentKey())) {
                continue;
            }
            if (keyType.isAssignableFrom(key.getClass()) && evaluate(componentAdapter)) {
                adapterMap.put(key, componentAdapter);
            }
        }
        return adapterMap;
    }

    private Class getCollectionType(final Class collectionType) {
        Class collectionClass = null;
        if (collectionType.isArray()) {
            collectionClass = Array.class;
        } else if (Map.class.isAssignableFrom(collectionType)) {
            collectionClass = Map.class;
        } else if (Collection.class.isAssignableFrom(collectionType)) {
            collectionClass = Collection.class;
        }
        return collectionClass;
    }

    private Class getValueType(final Class collectionType) {
        Class valueType = componentValueType;
        if (collectionType.isArray()) {
            valueType = collectionType.getComponentType();
        }
        return valueType;
    }

    private Object[] getArrayInstance(final PicoContainer container,
                                      final Class expectedType,
                                      final Map<Object, ComponentAdapter<?>> adapterList)
    {
        final Object[] result = (Object[])Array.newInstance(expectedType.getComponentType(), adapterList.size());
        int i = 0;
        for (ComponentAdapter componentAdapter : adapterList.values()) {
            result[i] = container.getComponent(componentAdapter.getComponentKey());
            i++;
        }
        return result;
    }

    @SuppressWarnings({ "unchecked" })
    private Collection getCollectionInstance(final PicoContainer container,
                                             final Class<? extends Collection> expectedType,
                                             final Map<Object, ComponentAdapter<?>> adapterList)
    {
        Class<? extends Collection> collectionType = expectedType;
        if (collectionType.isInterface()) {
            // The order of tests are significant. The least generic types last.
            if (List.class.isAssignableFrom(collectionType)) {
                collectionType = ArrayList.class;
//            } else if (BlockingQueue.class.isAssignableFrom(collectionType)) {
//                collectionType = ArrayBlockingQueue.class;
//            } else if (Queue.class.isAssignableFrom(collectionType)) {
//                collectionType = LinkedList.class;
            } else if (SortedSet.class.isAssignableFrom(collectionType)) {
                collectionType = TreeSet.class;
            } else if (Set.class.isAssignableFrom(collectionType)) {
                collectionType = HashSet.class;
            } else if (Collection.class.isAssignableFrom(collectionType)) {
                collectionType = ArrayList.class;
            }
        }
        try {
            Collection result = collectionType.newInstance();
            for (ComponentAdapter componentAdapter : adapterList.values()) {
                result.add(container.getComponent(componentAdapter.getComponentKey()));
            }
            return result;
        } catch (InstantiationException e) {
            ///CLOVER:OFF
            throw new PicoCompositionException(e);
            ///CLOVER:ON
        } catch (IllegalAccessException e) {
            ///CLOVER:OFF
            throw new PicoCompositionException(e);
            ///CLOVER:ON
        }
    }

    @SuppressWarnings({ "unchecked" })
    private Map getMapInstance(final PicoContainer container,
                               final Class<? extends Map> expectedType,
                               final Map<Object, ComponentAdapter<?>> adapterList)
    {
        Class<? extends Map> collectionType = expectedType;
        if (collectionType.isInterface()) {
            // The order of tests are significant. The least generic types last.
            if (SortedMap.class.isAssignableFrom(collectionType)) {
                collectionType = TreeMap.class;
//            } else if (ConcurrentMap.class.isAssignableFrom(collectionType)) {
//                collectionType = ConcurrentHashMap.class;
            } else if (Map.class.isAssignableFrom(collectionType)) {
                collectionType = HashMap.class;
            }
        }
        try {
            Map result = collectionType.newInstance();
            for (Map.Entry<Object, ComponentAdapter<?>> entry : adapterList.entrySet()) {
                final Object key = entry.getKey();
                result.put(key, container.getComponent(key));
            }
            return result;
        } catch (InstantiationException e) {
            ///CLOVER:OFF
            throw new PicoCompositionException(e);
            ///CLOVER:ON
        } catch (IllegalAccessException e) {
            ///CLOVER:OFF
            throw new PicoCompositionException(e);
            ///CLOVER:ON
        }
    }
}
