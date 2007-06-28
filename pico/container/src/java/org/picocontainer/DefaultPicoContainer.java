/*****************************************************************************
 * Copyright (C) PicoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the LICENSE.txt file.                                                     *
 *                                                                           *
 * Original code by                                                          *
 *****************************************************************************/
package org.picocontainer;

import org.picocontainer.adapters.InstanceAdapter;
import org.picocontainer.behaviors.CachingBehavior;
import org.picocontainer.behaviors.CachingBehaviorFactory;
import org.picocontainer.behaviors.ImplementationHidingBehavior;
import org.picocontainer.behaviors.AdaptiveBehaviorFactory;
import org.picocontainer.containers.AbstractDelegatingMutablePicoContainer;
import org.picocontainer.containers.EmptyPicoContainer;
import org.picocontainer.containers.ImmutablePicoContainer;
import org.picocontainer.injectors.AbstractInjector;
import org.picocontainer.injectors.AdaptiveInjectionFactory;
import org.picocontainer.lifecycle.StartableLifecycleStrategy;
import org.picocontainer.monitors.NullComponentMonitor;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * <p/>
 * The Standard {@link PicoContainer}/{@link MutablePicoContainer} implementation.
 * Constructing a container c with a parent p container will cause c to look up components
 * in p if they cannot be found inside c itself.
 * </p>
 * <p/>
 * Using {@link Class} objects as keys to the various registerXXX() methods makes
 * a subtle semantic difference:
 * </p>
 * <p/>
 * If there are more than one registered components of the same type and one of them are
 * registered with a {@link java.lang.Class} key of the corresponding type, this addComponent
 * will take precedence over other components during type resolution.
 * </p>
 * <p/>
 * Another place where keys that are classes make a subtle difference is in
 * {@link ImplementationHidingBehavior}.
 * </p>
 * <p/>
 * This implementation of {@link MutablePicoContainer} also supports
 * {@link ComponentMonitorStrategy}.
 * </p>
 *
 * @author Paul Hammant
 * @author Aslak Helles&oslash;y
 * @author Jon Tirs&eacute;n
 * @author Thomas Heller
 * @author Mauro Talevi
 * @version $Revision: 1.8 $
 */
public class DefaultPicoContainer implements MutablePicoContainer, ComponentMonitorStrategy, Serializable {
    private final Map<Object, ComponentAdapter> componentKeyToAdapterCache = new HashMap<Object, ComponentAdapter>();
    private ComponentFactory componentFactory;
    private PicoContainer parent;
    private final Set<PicoContainer> children = new HashSet<PicoContainer>();

    private final List<ComponentAdapter<?>> componentAdapters = new ArrayList<ComponentAdapter<?>>();
    // Keeps track of instantiation order.
    private final List<ComponentAdapter<?>> orderedComponentAdapters = new ArrayList<ComponentAdapter<?>>();

    // Keeps track of the container started status
    private boolean started = false;
    // Keeps track of the container disposed status
    private boolean disposed = false;
    // Keeps track of child containers started status
    private final Set<Integer> childrenStarted = new HashSet<Integer>();

    private final LifecycleManager lifecycleManager = new OrderedComponentAdapterLifecycleManager();
    private LifecycleStrategy lifecycleStrategy;
    private final ComponentCharacteristics componentCharacteristics = new ComponentCharacteristics();
    private ComponentMonitor componentMonitor;

    /**
     * Creates a new container with a custom ComponentAdapterFactory and a parent container.
     * <p/>
     * <em>
     * Important note about caching: If you intend the components to be cached, you should pass
     * in a factory that creates {@link CachingBehavior} instances, such as for example
     * {@link CachingBehaviorFactory}. CachingBehaviorFactory can delegate to
     * other ComponentAdapterFactories.
     * </em>
     *
     * @param componentFactory the factory to use for creation of ComponentAdapters.
     * @param parent                  the parent container (used for component dependency lookups).
     */
    public DefaultPicoContainer(ComponentFactory componentFactory, PicoContainer parent) {
        this(componentFactory, new StartableLifecycleStrategy(NullComponentMonitor.getInstance()), parent, NullComponentMonitor.getInstance());
    }

    /**
     * Creates a new container with a custom ComponentAdapterFactory, LifecycleStrategy for instance registration,
     * and a parent container.
     * <p/>
     * <em>
     * Important note about caching: If you intend the components to be cached, you should pass
     * in a factory that creates {@link CachingBehavior} instances, such as for example
     * {@link CachingBehaviorFactory}. CachingBehaviorFactory can delegate to
     * other ComponentAdapterFactories.
     * </em>
     *
     * @param componentFactory the factory to use for creation of ComponentAdapters.
     * @param lifecycleStrategy
     *                                the lifecylce strategy chosen for regiered
     *                                instance (not implementations!)
     * @param parent                  the parent container (used for component dependency lookups).
     */
    public DefaultPicoContainer(ComponentFactory componentFactory,
                                LifecycleStrategy lifecycleStrategy,
                                PicoContainer parent) {
        this(componentFactory, lifecycleStrategy, parent, NullComponentMonitor.getInstance() );
    }

    public DefaultPicoContainer(ComponentFactory componentFactory,
                                LifecycleStrategy lifecycleStrategy,
                                PicoContainer parent, ComponentMonitor componentMonitor) {
        if (componentFactory == null) throw new NullPointerException("componentFactory");
        if (lifecycleStrategy == null) throw new NullPointerException("lifecycleStrategy");
        this.componentFactory = componentFactory;
        this.lifecycleStrategy = lifecycleStrategy;
        this.parent = parent;
        if (parent != null && !(parent instanceof EmptyPicoContainer)) {
            this.parent = new ImmutablePicoContainer(parent);
        }
        this.componentMonitor = componentMonitor;
    }


    /**
     * Creates a new container with the AdaptiveInjectionFactory using a
     * custom ComponentMonitor
     *
     * @param monitor the ComponentMonitor to use
     * @param parent  the parent container (used for component dependency lookups).
     */
    public DefaultPicoContainer(ComponentMonitor monitor, PicoContainer parent) {
        this(new AdaptiveBehaviorFactory(), new StartableLifecycleStrategy(monitor), parent, monitor);
    }

    /**
     * Creates a new container with the AdaptiveInjectionFactory using a
     * custom ComponentMonitor and lifecycle strategy
     *
     * @param monitor           the ComponentMonitor to use
     * @param lifecycleStrategy the lifecycle strategy to use.
     * @param parent            the parent container (used for component dependency lookups).
     */
    public DefaultPicoContainer(ComponentMonitor monitor, LifecycleStrategy lifecycleStrategy, PicoContainer parent) {
        this(new AdaptiveBehaviorFactory(), lifecycleStrategy, parent, monitor);
    }

    /**
     * Creates a new container with the AdaptiveInjectionFactory using a
     * custom lifecycle strategy
     *
     * @param lifecycleStrategy the lifecycle strategy to use.
     * @param parent            the parent container (used for component dependency lookups).
     */
    public DefaultPicoContainer(LifecycleStrategy lifecycleStrategy, PicoContainer parent) {
        this(NullComponentMonitor.getInstance(), lifecycleStrategy, parent);
    }


    /**
     * Creates a new container with a custom ComponentAdapterFactory and no parent container.
     *
     * @param componentFactory the ComponentAdapterFactory to use.
     */
    public DefaultPicoContainer(ComponentFactory componentFactory) {
        this(componentFactory, null);
    }

    /**
     * Creates a new container with the AdaptiveInjectionFactory using a
     * custom ComponentMonitor
     *
     * @param monitor the ComponentMonitor to use
     */
    public DefaultPicoContainer(ComponentMonitor monitor) {
        this(monitor, new StartableLifecycleStrategy(monitor), null);
    }

    /**
     * Creates a new container with a (caching) {@link AdaptiveInjectionFactory}
     * and a parent container.
     *
     * @param parent the parent container (used for component dependency lookups).
     */
    public DefaultPicoContainer(PicoContainer parent) {
        this(new AdaptiveBehaviorFactory(), parent);
    }

    /** Creates a new container with a {@link AdaptiveBehaviorFactory} and no parent container. */
    public DefaultPicoContainer() {
        this(new AdaptiveBehaviorFactory(), null);
    }

    public Collection<ComponentAdapter<?>> getComponentAdapters() {
        return Collections.unmodifiableList(componentAdapters);
    }

    public final ComponentAdapter<?> getComponentAdapter(Object componentKey) {
        ComponentAdapter adapter = componentKeyToAdapterCache.get(componentKey);
        if (adapter == null && parent != null) {
            adapter = parent.getComponentAdapter(componentKey);
        }
        return adapter;
    }

    public <T> ComponentAdapter<T> getComponentAdapter(Class<T> componentType) {
        // See http://jira.codehaus.org/secure/ViewIssue.jspa?key=PICO-115
        ComponentAdapter<?> adapterByKey = getComponentAdapter((Object)componentType);
        if (adapterByKey != null) {
            return typeComponentAdapter(adapterByKey);
        }

        List<ComponentAdapter<T>> found = getComponentAdapters(componentType);

        if (found.size() == 1) {
            return found.get(0);
        } else if (found.isEmpty()) {
            if (parent != null) {
                return parent.getComponentAdapter(componentType);
            } else {
                return null;
            }
        } else {
            Class[] foundClasses = new Class[found.size()];
            for (int i = 0; i < foundClasses.length; i++) {
                foundClasses[i] = found.get(i).getComponentImplementation();
            }

            throw new AbstractInjector.AmbiguousComponentResolutionException(componentType, foundClasses);
        }
    }

    public <T> List<ComponentAdapter<T>> getComponentAdapters(Class<T> componentType) {
        if (componentType == null) {
            return Collections.emptyList();
        }
        List<ComponentAdapter<T>> found = new ArrayList<ComponentAdapter<T>>();
        for (ComponentAdapter<?> componentAdapter : getComponentAdapters()) {
            if (componentType.isAssignableFrom(componentAdapter.getComponentImplementation())) {
                ComponentAdapter<T> typedComponentAdapter = typeComponentAdapter(componentAdapter);
                found.add(typedComponentAdapter);
            }
        }
        return found;
    }

    /**
     * {@inheritDoc}
     * This method can be used to override the ComponentAdapter created by the {@link ComponentFactory}
     * passed to the constructor of this container.
     */
    public MutablePicoContainer addAdapter(ComponentAdapter componentAdapter) {
        Object componentKey = componentAdapter.getComponentKey();
        if (componentKeyToAdapterCache.containsKey(componentKey)) {
            throw new PicoCompositionException("Duplicate Keys not allowed. Duplicate for '" + componentKey + "'");
        }
        componentAdapters.add(componentAdapter);
        componentKeyToAdapterCache.put(componentKey, componentAdapter);
        return this;
    }

    public ComponentAdapter removeComponent(Object componentKey) {
        ComponentAdapter adapter = componentKeyToAdapterCache.remove(componentKey);
        componentAdapters.remove(adapter);
        orderedComponentAdapters.remove(adapter);
        return adapter;
    }

    /**
     * {@inheritDoc}
     * The returned ComponentAdapter will be an {@link org.picocontainer.adapters.InstanceAdapter}.
     */
    public MutablePicoContainer addComponent(Object implOrInstance) {
        Class clazz;
        if (implOrInstance instanceof String) {
            addComponent((String) implOrInstance, implOrInstance);
        }
        if (implOrInstance instanceof CharacterizedObject) {
            CharacterizedObject co = (CharacterizedObject)implOrInstance;
            if (co.implOrInst instanceof Class) {
                clazz = (Class)co.implOrInst;
            } else {
                clazz = co.getClass();
            }
        } else if (implOrInstance instanceof Class) {
            clazz = (Class)implOrInstance;
        } else {
            clazz = implOrInstance.getClass();
        }
        return addComponent(clazz, implOrInstance);
    }

    /**
     * {@inheritDoc}
     * The returned ComponentAdapter will be instantiated by the {@link ComponentFactory}
     * passed to the container's constructor.
     */
    public MutablePicoContainer addComponent(Object componentKey,
                                             Object componentImplementationOrInstance,
                                             Parameter... parameters)
    {
        ComponentCharacteristics characteristics = this.componentCharacteristics;
        if (componentImplementationOrInstance instanceof CharacterizedObject) {
            characteristics = ((CharacterizedObject)componentImplementationOrInstance).characteristics;
            componentImplementationOrInstance = ((CharacterizedObject)componentImplementationOrInstance).implOrInst;
        }
        if (parameters != null && parameters.length == 0 && parameters != Parameter.ZERO) {
            parameters = null; // backwards compatibility!  solve this better later - Paul
        }
        if (componentImplementationOrInstance instanceof Class) {
            ComponentCharacteristics tmpComponentCharacteristics = characteristics.clone();
            ComponentAdapter componentAdapter = componentFactory.createComponentAdapter(componentMonitor,
                                                                                               lifecycleStrategy,
                                                                                               tmpComponentCharacteristics,
                                                                                               componentKey,
                                                                                               (Class)componentImplementationOrInstance,
                                                                                               parameters);
            if(tmpComponentCharacteristics.hasUnProcessedEntries()) {
                throw new PicoCompositionException("Unprocessed Characteristics:" + tmpComponentCharacteristics);
            }
            return addAdapter(componentAdapter);
        } else {
            ComponentAdapter componentAdapter =
                new InstanceAdapter(componentKey, componentImplementationOrInstance, lifecycleStrategy, componentMonitor);
            return addAdapter(componentAdapter);
        }
    }

    protected ComponentCharacteristics getComponentCharacteristic() {
        return componentCharacteristics;
    }

    private void addOrderedComponentAdapter(ComponentAdapter componentAdapter) {
        if (!orderedComponentAdapters.contains(componentAdapter)) {
            orderedComponentAdapters.add(componentAdapter);
        }
    }

    public List getComponents() throws PicoException {
        return getComponents(Object.class);
    }

    public <T> List<T> getComponents(Class<T> componentType) {
        if (componentType == null) {
            return Collections.emptyList();
        }

        Map<ComponentAdapter<T>, T> adapterToInstanceMap = new HashMap<ComponentAdapter<T>, T>();
        for (ComponentAdapter<?> componentAdapter : componentAdapters) {
            if (componentType.isAssignableFrom(componentAdapter.getComponentImplementation())) {
                ComponentAdapter<T> typedComponentAdapter = typeComponentAdapter(componentAdapter);
                T componentInstance = getLocalInstance(typedComponentAdapter);

                adapterToInstanceMap.put(typedComponentAdapter, componentInstance);
            }
        }
        List<T> result = new ArrayList<T>();
        for (ComponentAdapter componentAdapter : orderedComponentAdapters) {
            final T componentInstance = adapterToInstanceMap.get(componentAdapter);
            if (componentInstance != null) {
                // may be null in the case of the "implicit" addAdapter
                // representing "this".
                result.add(componentInstance);
            }
        }
        return result;
    }

    private <T> T getLocalInstance(ComponentAdapter<T> typedComponentAdapter) {
        T componentInstance = typedComponentAdapter.getComponentInstance(this);

        // This is to ensure all are added. (Indirect dependencies will be added
        // from InstantiatingComponentAdapter).
        addOrderedComponentAdapter(typedComponentAdapter);

        return componentInstance;
    }

    @SuppressWarnings({ "unchecked" })
    private static <T> ComponentAdapter<T> typeComponentAdapter(ComponentAdapter<?> componentAdapter) {
        return (ComponentAdapter<T>)componentAdapter;
    }

    public Object getComponent(Object componentKeyOrType) {
        Object retVal;
        if (componentKeyOrType instanceof Class) {
            final ComponentAdapter<?> componentAdapter = getComponentAdapter((Class<?>)componentKeyOrType);
            retVal = componentAdapter == null ? null : getInstance(componentAdapter);
        } else {
            ComponentAdapter<?> componentAdapter = getComponentAdapter(componentKeyOrType);
            retVal = componentAdapter == null ? null : getInstance(componentAdapter);
        }
        if (retVal == null) {
            componentMonitor.noComponent(this, componentKeyOrType);
        }
        return retVal;
    }

    public <T> T getComponent(Class<T> componentType) {
        return componentType.cast(getComponent((Object)componentType));
    }

    private Object getInstance(ComponentAdapter componentAdapter) {
        // check wether this is our adapter
        // we need to check this to ensure up-down dependencies cannot be followed
        final boolean isLocal = componentAdapters.contains(componentAdapter);

        if (isLocal) {
            Object instance;
            try {
                instance = componentAdapter.getComponentInstance(this);
            } catch (AbstractInjector.CyclicDependencyException e) {
                if (parent != null) {
                    instance = parent.getComponent(componentAdapter.getComponentKey());
                    if (instance != null) {
                        return instance;
                    }
                }
                throw e;
            }
            addOrderedComponentAdapter(componentAdapter);

            return instance;
        } else if (parent != null) {
            return parent.getComponent(componentAdapter.getComponentKey());
        }

        return null;
    }


    public PicoContainer getParent() {
        return parent;
    }

    public ComponentAdapter removeComponentByInstance(Object componentInstance) {
        for (ComponentAdapter<?> componentAdapter : componentAdapters) {
            if (getLocalInstance(componentAdapter).equals(componentInstance)) {
                return removeComponent(componentAdapter.getComponentKey());
            }
        }
        return null;
    }

    /**
     * Start the components of this PicoContainer and all its logical child containers.
     * The starting of the child container is only attempted if the parent
     * container start successfully.  The child container for which start is attempted
     * is tracked so that upon stop, only those need to be stopped.
     * The lifecycle operation is delegated to the component adapter,
     * if it is an instance of {@link LifecycleManager lifecycle manager}.
     * The actual {@link LifecycleStrategy lifecycle strategy} supported
     * depends on the concrete implementation of the adapter.
     *
     * @see LifecycleManager
     * @see LifecycleStrategy
     * @see #makeChildContainer()
     * @see #addChildContainer(PicoContainer)
     * @see #removeChildContainer(PicoContainer)
     */
    public void start() {
        if (disposed) throw new IllegalStateException("Already disposed");
        if (started) throw new IllegalStateException("Already started");
        started = true;
        this.lifecycleManager.start(this);
        childrenStarted.clear();
        for (PicoContainer child : children) {
            childrenStarted.add(child.hashCode());
            if (child instanceof Startable) {
                ((Startable)child).start();
            }
        }
    }

    /**
     * Stop the components of this PicoContainer and all its logical child containers.
     * The stopping of the child containers is only attempted for those that have been
     * started, possibly not successfully.
     * The lifecycle operation is delegated to the component adapter,
     * if it is an instance of {@link LifecycleManager lifecycle manager}.
     * The actual {@link LifecycleStrategy lifecycle strategy} supported
     * depends on the concrete implementation of the adapter.
     *
     * @see LifecycleManager
     * @see LifecycleStrategy
     * @see #makeChildContainer()
     * @see #addChildContainer(PicoContainer)
     * @see #removeChildContainer(PicoContainer)
     */
    public void stop() {
        if (disposed) throw new IllegalStateException("Already disposed");
        if (!started) throw new IllegalStateException("Not started");
        for (PicoContainer child : children) {
            if (childStarted(child)) {
                if (child instanceof Startable) {
                    ((Startable)child).stop();
                }
            }
        }
        this.lifecycleManager.stop(this);
        started = false;
    }

    /**
     * Checks the status of the child container to see if it's been started
     * to prevent IllegalStateException upon stop
     *
     * @param child the child PicoContainer
     *
     * @return A boolean, <code>true</code> if the container is started
     */
    private boolean childStarted(PicoContainer child) {
        return childrenStarted.contains(new Integer(child.hashCode()));
    }

    /**
     * Dispose the components of this PicoContainer and all its logical child containers.
     * The lifecycle operation is delegated to the component adapter,
     * if it is an instance of {@link LifecycleManager lifecycle manager}.
     * The actual {@link LifecycleStrategy lifecycle strategy} supported
     * depends on the concrete implementation of the adapter.
     *
     * @see LifecycleManager
     * @see LifecycleStrategy
     * @see #makeChildContainer()
     * @see #addChildContainer(PicoContainer)
     * @see #removeChildContainer(PicoContainer)
     */
    public void dispose() {
        if (disposed) throw new IllegalStateException("Already disposed");
        for (PicoContainer child : children) {
            if (child instanceof MutablePicoContainer) {
                ((Disposable)child).dispose();
            }
        }
        this.lifecycleManager.dispose(this);
        disposed = true;
    }

    public MutablePicoContainer makeChildContainer() {
        DefaultPicoContainer pc = new DefaultPicoContainer(componentFactory, lifecycleStrategy, this);
        addChildContainer(pc);
        return pc;
    }

    public MutablePicoContainer addChildContainer(PicoContainer child) {
        if (children.add(child)) {
            // @todo Should only be added if child container has also be started
            if (started) {
                childrenStarted.add(child.hashCode());
            }
        }
        return this;
    }

    public boolean removeChildContainer(PicoContainer child) {
        final boolean result = children.remove(child);
        childrenStarted.remove(new Integer(child.hashCode()));
        return result;
    }

    public MutablePicoContainer change(ComponentCharacteristics... characteristics) {
        for (ComponentCharacteristics c : characteristics) {
            c.mergeInto(this.componentCharacteristics);
        }
        return this;
    }

    public MutablePicoContainer as(ComponentCharacteristics... characteristics) {
        return new TemporaryCharacterizedPicoContainer(characteristics);
    }

    public void accept(PicoVisitor visitor) {
        visitor.visitContainer(this);
        final List<ComponentAdapter> componentAdapters = new ArrayList<ComponentAdapter>(getComponentAdapters());
        for (ComponentAdapter componentAdapter : componentAdapters) {
            componentAdapter.accept(visitor);
        }
        final List<PicoContainer> allChildren = new ArrayList<PicoContainer>(children);
        for (PicoContainer child : allChildren) {
            child.accept(visitor);
        }
    }

    /**
     * Changes monitor in the ComponentAdapterFactory, the component adapters
     * and the child containers, if these support a ComponentMonitorStrategy.
     * {@inheritDoc}
     */
    public void changeMonitor(ComponentMonitor monitor) {
        this.componentMonitor = monitor;
        if (lifecycleStrategy instanceof ComponentMonitorStrategy) {
            ((ComponentMonitorStrategy)lifecycleStrategy).changeMonitor(monitor);
        }
        for (ComponentAdapter adapter : componentAdapters) {
            if (adapter instanceof ComponentMonitorStrategy) {
                ((ComponentMonitorStrategy)adapter).changeMonitor(monitor);
            }
        }
        for (PicoContainer child : children) {
            if (child instanceof ComponentMonitorStrategy) {
                ((ComponentMonitorStrategy)child).changeMonitor(monitor);
            }
        }
    }

    /**
     * Returns the first current monitor found in the ComponentAdapterFactory, the component adapters
     * and the child containers, if these support a ComponentMonitorStrategy.
     * {@inheritDoc}
     *
     * @throws PicoCompositionException if no component monitor is found in container or its children
     */
    public ComponentMonitor currentMonitor() {
        return componentMonitor;
    }

    /**
     * <p>
     * Implementation of lifecycle manager which delegates to the container's component adapters.
     * The component adapters will be ordered by dependency as registered in the container.
     * This LifecycleManager will delegate calls on the lifecycle methods to the component adapters
     * if these are themselves LifecycleManagers.
     * </p>
     *
     * @author Mauro Talevi
     * @since 1.2
     */
    private final class OrderedComponentAdapterLifecycleManager implements LifecycleManager, Serializable {

        /** List collecting the CAs which have been successfully started */
        private final List<ComponentAdapter> startedComponentAdapters = new ArrayList<ComponentAdapter>();

        /**
         * {@inheritDoc}
         * Loops over all component adapters and invokes
         * start(PicoContainer) method on the ones which are LifecycleManagers
         */
        public void start(PicoContainer node) {
            Collection<ComponentAdapter<?>> adapters = getComponentAdapters();
            for (ComponentAdapter adapter : adapters) {
                if (adapter instanceof LifecycleManager) {
                    LifecycleManager manager = (LifecycleManager)adapter;
                    if (manager.hasLifecycle()) {
                        // create an instance, it will be added to the ordered CA list
                        adapter.getComponentInstance(node);
                        addOrderedComponentAdapter(adapter);
                    }
                }
            }
            adapters = orderedComponentAdapters;
            // clear list of started CAs
            startedComponentAdapters.clear();
            for (final ComponentAdapter adapter : adapters) {
                if (adapter instanceof LifecycleManager) {
                    LifecycleManager manager = (LifecycleManager)adapter;
                    manager.start(node);
                    startedComponentAdapters.add(adapter);
                }
            }
        }

        /**
         * {@inheritDoc}
         * Loops over started component adapters (in inverse order) and invokes
         * stop(PicoContainer) method on the ones which are LifecycleManagers
         */
        public void stop(PicoContainer node) {
            List<ComponentAdapter> adapters = startedComponentAdapters;
            for (int i = adapters.size() - 1; 0 <= i; i--) {
                ComponentAdapter adapter = adapters.get(i);
                if (adapter instanceof LifecycleManager) {
                    LifecycleManager manager = (LifecycleManager)adapter;
                    manager.stop(node);
                }
            }
        }

        /**
         * {@inheritDoc}
         * Loops over all component adapters (in inverse order) and invokes
         * dispose(PicoContainer) method on the ones which are LifecycleManagers
         */
        public void dispose(PicoContainer node) {
            List<ComponentAdapter<?>> adapters = orderedComponentAdapters;
            for (int i = adapters.size() - 1; 0 <= i; i--) {
                ComponentAdapter adapter = adapters.get(i);
                if (adapter instanceof LifecycleManager) {
                    LifecycleManager manager = (LifecycleManager)adapter;
                    manager.dispose(node);
                }
            }
        }

        public boolean hasLifecycle() {
            throw new UnsupportedOperationException("Should not have been called");
        }

    }

    private class TemporaryCharacterizedPicoContainer extends AbstractDelegatingMutablePicoContainer {
        private final ComponentCharacteristics[] characteristics;

        public TemporaryCharacterizedPicoContainer(ComponentCharacteristics... characteristics) {
            super(DefaultPicoContainer.this);
            this.characteristics = characteristics;
        }

        public MutablePicoContainer makeChildContainer() {
            return getDelegate().makeChildContainer();
        }

        public MutablePicoContainer addComponent(Object componentKey,
                                                 Object componentImplementationOrInstance,
                                                 Parameter... parameters) throws PicoCompositionException
        {
            return super.addComponent(componentKey,
                                      makeCharacterizedImplOrInstance(componentImplementationOrInstance),
                                      parameters);
        }

        public MutablePicoContainer addComponent(Object implOrInstance) throws PicoCompositionException {
            return super.addComponent(makeCharacterizedImplOrInstance(implOrInstance));
        }


        private CharacterizedObject makeCharacterizedImplOrInstance(Object componentImplementationOrInstance) {
            ComponentCharacteristics tempCharacteristics = (ComponentCharacteristics)componentCharacteristics.clone();
            for (ComponentCharacteristics c : characteristics) {
                c.mergeInto(tempCharacteristics);
            }
            return new CharacterizedObject(tempCharacteristics, componentImplementationOrInstance);
        }

    }

    private static class CharacterizedObject {
        private final ComponentCharacteristics characteristics;
        private final Object implOrInst;

        public CharacterizedObject(ComponentCharacteristics tempCharacteristics,
                                   Object componentImplementationOrInstance)
        {
            characteristics = tempCharacteristics;
            implOrInst = componentImplementationOrInstance;
        }
    }


}
