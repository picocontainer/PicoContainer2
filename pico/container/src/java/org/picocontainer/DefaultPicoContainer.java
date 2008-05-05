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

import org.picocontainer.injectors.FactoryInjector;
import org.picocontainer.adapters.InstanceAdapter;
import org.picocontainer.behaviors.AbstractBehaviorFactory;
import org.picocontainer.behaviors.AdaptingBehavior;
import org.picocontainer.behaviors.Cached;
import org.picocontainer.behaviors.Caching;
import org.picocontainer.behaviors.HiddenImplementation;
import org.picocontainer.containers.*;
import org.picocontainer.injectors.AbstractInjector;
import org.picocontainer.injectors.AdaptingInjection;
import org.picocontainer.lifecycle.LifecycleState;
import static org.picocontainer.lifecycle.LifecycleState.*;
import org.picocontainer.lifecycle.StartableLifecycleStrategy;
import org.picocontainer.monitors.NullComponentMonitor;

import java.io.Serializable;
import java.lang.annotation.Annotation;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Properties;
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
 * {@link HiddenImplementation}.
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
 */
public class DefaultPicoContainer implements MutablePicoContainer, ComponentMonitorStrategy, Serializable  {

    /**
	 * Serialization UUID.
	 */
	private static final long serialVersionUID = -8987815732600681148L;


	/**
	 * Component factory instance.
	 */
	protected final ComponentFactory componentFactory;
    
	/**
	 * Parent picocontainer
	 */
    private PicoContainer parent;
    
    /**
     * All picocontainer children.
     */
    private final Set<PicoContainer> children = new HashSet<PicoContainer>();

    /**
     * Current state of the container.
     */
    private LifecycleState lifecycleState = CONSTRUCTED;
    
    /** 
     * Keeps track of child containers started status.
     */
    private final Set<WeakReference<PicoContainer>> childrenStarted = new HashSet<WeakReference<PicoContainer>>();

    /**
     * Lifecycle strategy instance.
     */
    protected final LifecycleStrategy lifecycleStrategy;
    
    private final Properties componentProperties = new Properties();
    
    /**
     * Component monitor instance.  Receives event callbacks.
     */
    protected ComponentMonitor componentMonitor;

    /** List collecting the CAs which have been successfully started */
    private final List<WeakReference<ComponentAdapter<?>>> startedComponentAdapters = new ArrayList<WeakReference<ComponentAdapter<?>>>();


    /**
     * Map used for looking up component adapters by their key.
     */
	private final Map<Object, ComponentAdapter<?>> componentKeyToAdapterCache = new HashMap<Object, ComponentAdapter<?> >();


	private final List<ComponentAdapter<?>> componentAdapters = new ArrayList<ComponentAdapter<?>>();


	protected final List<ComponentAdapter<?>> orderedComponentAdapters = new ArrayList<ComponentAdapter<?>>();

    /**
     * Creates a new container with a custom ComponentFactory and a parent container.
     * <p/>
     * <em>
     * Important note about caching: If you intend the components to be cached, you should pass
     * in a factory that creates {@link Cached} instances, such as for example
     * {@link Caching}. Caching can delegate to
     * other ComponentAdapterFactories.
     * </em>
     *
     * @param componentFactory the factory to use for creation of ComponentAdapters.
     * @param parent                  the parent container (used for component dependency lookups).
     */
    public DefaultPicoContainer(final ComponentFactory componentFactory, final PicoContainer parent) {
        this(componentFactory, new StartableLifecycleStrategy(new NullComponentMonitor()), parent, new NullComponentMonitor());
    }

    /**
     * Creates a new container with a custom ComponentFactory, LifecycleStrategy for instance registration,
     * and a parent container.
     * <p/>
     * <em>
     * Important note about caching: If you intend the components to be cached, you should pass
     * in a factory that creates {@link Cached} instances, such as for example
     * {@link Caching}. Caching can delegate to
     * other ComponentAdapterFactories.
     * </em>
     *
     * @param componentFactory the factory to use for creation of ComponentAdapters.
     * @param lifecycleStrategy
     *                                the lifecycle strategy chosen for registered
     *                                instance (not implementations!)
     * @param parent                  the parent container (used for component dependency lookups).
     */
    public DefaultPicoContainer(final ComponentFactory componentFactory,
                                final LifecycleStrategy lifecycleStrategy,
                                final PicoContainer parent) {
        this(componentFactory, lifecycleStrategy, parent, new NullComponentMonitor() );
    }

    public DefaultPicoContainer(final ComponentFactory componentFactory,
                                final LifecycleStrategy lifecycleStrategy,
                                final PicoContainer parent, final ComponentMonitor componentMonitor) {
        if (componentFactory == null) {
			throw new NullPointerException("componentFactory");
		}
        if (lifecycleStrategy == null) {
			throw new NullPointerException("lifecycleStrategy");
		}
        this.componentFactory = componentFactory;
        this.lifecycleStrategy = lifecycleStrategy;
        this.parent = parent;
        if (parent != null && !(parent instanceof EmptyPicoContainer)) {
            this.parent = new ImmutablePicoContainer(parent);
        }
        this.componentMonitor = componentMonitor;
    }

    /**
     * Creates a new container with the AdaptingInjection using a
     * custom ComponentMonitor
     *
     * @param monitor the ComponentMonitor to use
     * @param parent  the parent container (used for component dependency lookups).
     */
    public DefaultPicoContainer(final ComponentMonitor monitor, final PicoContainer parent) {
        this(new AdaptingBehavior(), new StartableLifecycleStrategy(monitor), parent, monitor);
    }

    /**
     * Creates a new container with the AdaptingInjection using a
     * custom ComponentMonitor and lifecycle strategy
     *
     * @param monitor           the ComponentMonitor to use
     * @param lifecycleStrategy the lifecycle strategy to use.
     * @param parent            the parent container (used for component dependency lookups).
     */
    public DefaultPicoContainer(final ComponentMonitor monitor, final LifecycleStrategy lifecycleStrategy, final PicoContainer parent) {
        this(new AdaptingBehavior(), lifecycleStrategy, parent, monitor);
    }

    /**
     * Creates a new container with the AdaptingInjection using a
     * custom lifecycle strategy
     *
     * @param lifecycleStrategy the lifecycle strategy to use.
     * @param parent            the parent container (used for component dependency lookups).
     */
    public DefaultPicoContainer(final LifecycleStrategy lifecycleStrategy, final PicoContainer parent) {
        this(new NullComponentMonitor(), lifecycleStrategy, parent);
    }


    /**
     * Creates a new container with a custom ComponentFactory and no parent container.
     *
     * @param componentFactory the ComponentFactory to use.
     */
    public DefaultPicoContainer(final ComponentFactory componentFactory) {
        this(componentFactory, null);
    }

    /**
     * Creates a new container with the AdaptingInjection using a
     * custom ComponentMonitor
     *
     * @param monitor the ComponentMonitor to use
     */
    public DefaultPicoContainer(final ComponentMonitor monitor) {
        this(monitor, new StartableLifecycleStrategy(monitor), null);
    }

    /**
     * Creates a new container with a (caching) {@link AdaptingInjection}
     * and a parent container.
     *
     * @param parent the parent container (used for component dependency lookups).
     */
    public DefaultPicoContainer(final PicoContainer parent) {
        this(new AdaptingBehavior(), parent);
    }

    /** Creates a new container with a {@link AdaptingBehavior} and no parent container. */
    public DefaultPicoContainer() {
        this(new AdaptingBehavior(), null);
    }

    /** {@inheritDoc} **/
    public Collection<ComponentAdapter<?>> getComponentAdapters() {
        return Collections.unmodifiableList(getModifiableComponentAdapterList());
    }

    /** {@inheritDoc} **/
    public final ComponentAdapter<?> getComponentAdapter(final Object componentKey) {
        ComponentAdapter<?> adapter = getComponentKeyToAdapterCache().get(componentKey);
        if (adapter == null && parent != null) {
            adapter = getParent().getComponentAdapter(componentKey);
        }
        return adapter;
    }

    /** {@inheritDoc} **/
    public <T> ComponentAdapter<T> getComponentAdapter(final Class<T> componentType, final NameBinding componentNameBinding) {
        return getComponentAdapter(componentType, componentNameBinding, null);
    }

    /** {@inheritDoc} **/
    private <T> ComponentAdapter<T> getComponentAdapter(final Class<T> componentType, final NameBinding componentNameBinding, final Class<? extends Annotation> binding) {
        // See http://jira.codehaus.org/secure/ViewIssue.jspa?key=PICO-115
        ComponentAdapter<?> adapterByKey = getComponentAdapter(componentType);
        if (adapterByKey != null) {
            return typeComponentAdapter(adapterByKey);
        }

        List<ComponentAdapter<T>> found = binding == null ? getComponentAdapters(componentType) : getComponentAdapters(componentType, binding);

        if (found.size() == 1) {
            return found.get(0);
        } else if (found.isEmpty()) {
            if (parent != null) {
                return getParent().getComponentAdapter(componentType, componentNameBinding);
            } else {
                return null;
            }
        } else {
            if (componentNameBinding != null) {
                String parameterName = componentNameBinding.getName();
                if (parameterName != null) {
                    ComponentAdapter<?> ca = getComponentAdapter(parameterName);
                    if (ca != null && componentType.isAssignableFrom(ca.getComponentImplementation())) {
                        return typeComponentAdapter(ca);
                    }
                }
            }
            Class<?>[] foundClasses = new Class[found.size()];
            for (int i = 0; i < foundClasses.length; i++) {
                foundClasses[i] = found.get(i).getComponentImplementation();
            }

            throw new AbstractInjector.AmbiguousComponentResolutionException(componentType, foundClasses);
        }
    }

    /** {@inheritDoc} **/
    public <T> ComponentAdapter<T> getComponentAdapter(final Class<T> componentType, final Class<? extends Annotation> binding) {
        return getComponentAdapter(componentType, null, binding);
    }

    /** {@inheritDoc} **/
    public <T> List<ComponentAdapter<T>> getComponentAdapters(final Class<T> componentType) {
        return getComponentAdapters(componentType,  null);
    }

    /** {@inheritDoc} **/
    public <T> List<ComponentAdapter<T>> getComponentAdapters(final Class<T> componentType, final Class<? extends Annotation> binding) {
        if (componentType == null) {
            return Collections.emptyList();
        }
        List<ComponentAdapter<T>> found = new ArrayList<ComponentAdapter<T>>();
        for (ComponentAdapter<?> componentAdapter : getComponentAdapters()) {
            Object k = componentAdapter.getComponentKey();

            if (componentType.isAssignableFrom(componentAdapter.getComponentImplementation()) &&
                (!(k instanceof BindKey) || (k instanceof BindKey && (((BindKey<?>)k).getAnnotation() == null || binding == null ||
                                                                      ((BindKey<?>)k).getAnnotation() == binding)))) {
                found.add((ComponentAdapter<T>)typeComponentAdapter(componentAdapter));
            }
        }
        return found;
    }

    protected MutablePicoContainer addAdapterInternal(ComponentAdapter<?> componentAdapter) {
        Object componentKey = componentAdapter.getComponentKey();
        if (getComponentKeyToAdapterCache().containsKey(componentKey)) {
            throw new PicoCompositionException("Duplicate Keys not allowed. Duplicate for '" + componentKey + "'");
        }
        getModifiableComponentAdapterList().add(componentAdapter);
        getComponentKeyToAdapterCache().put(componentKey, componentAdapter);
        return this;
    }

    /**
     * {@inheritDoc}
     * This method can be used to override the ComponentAdapter created by the {@link ComponentFactory}
     * passed to the constructor of this container.
     */
    public MutablePicoContainer addAdapter(final ComponentAdapter<?> componentAdapter) {
        return addAdapter(componentAdapter,  this.componentProperties);
    }

    /** {@inheritDoc} **/
    public MutablePicoContainer addAdapter(final ComponentAdapter<?> componentAdapter, final Properties properties) {
        Properties tmpProperties = (Properties)properties.clone();
        if (AbstractBehaviorFactory.removePropertiesIfPresent(tmpProperties, Characteristics.NONE) == false && componentFactory instanceof BehaviorFactory) {
            MutablePicoContainer container = addAdapterInternal(((BehaviorFactory)componentFactory).addComponentAdapter(
                componentMonitor,
                lifecycleStrategy,
                tmpProperties,
                componentAdapter));
            throwIfPropertiesLeft(tmpProperties);
            return container;
        } else {
            return addAdapterInternal(componentAdapter);
        }

    }


    /** {@inheritDoc} **/
    public <T> ComponentAdapter<T> removeComponent(final Object componentKey) {
        if (lifecycleState == STARTED) {
            throw new PicoCompositionException("Cannot remove components after the container has started");
        }
        
        if (lifecycleState == DISPOSED) {
            throw new PicoCompositionException("Cannot remove components after the container has been disposed");        	
        }
        
        ComponentAdapter<T> adapter = (ComponentAdapter<T>) getComponentKeyToAdapterCache().remove(componentKey);
        getModifiableComponentAdapterList().remove(adapter);
        getOrderedComponentAdapters().remove(adapter);    	
        return adapter;
    }

    /**
     * {@inheritDoc}
     * The returned ComponentAdapter will be an {@link org.picocontainer.adapters.InstanceAdapter}.
     */
    public MutablePicoContainer addComponent(final Object implOrInstance) {
        return addComponent(implOrInstance, this.componentProperties);
    }

    private MutablePicoContainer addComponent(final Object implOrInstance, final Properties props) {
        Class<?> clazz;
        if (implOrInstance instanceof String) {
            return addComponent(implOrInstance, implOrInstance);
        }
        if (implOrInstance instanceof Class) {
            clazz = (Class<?>)implOrInstance;
        } else {
            clazz = implOrInstance.getClass();
        }
        return addComponent(clazz, implOrInstance, props);
    }


    public MutablePicoContainer addConfig(final String name, final Object val) {
        return addAdapterInternal(new InstanceAdapter<Object>(name, val, lifecycleStrategy, componentMonitor));
    }


    /**
     * {@inheritDoc}
     * The returned ComponentAdapter will be instantiated by the {@link ComponentFactory}
     * passed to the container's constructor.
     */
    public MutablePicoContainer addComponent(final Object componentKey,
                                             final Object componentImplementationOrInstance,
                                             final Parameter... parameters) {
        return this.addComponent(componentKey, componentImplementationOrInstance, this.componentProperties, parameters);
    }

    private MutablePicoContainer addComponent(final Object componentKey,
                                             final Object componentImplementationOrInstance,
                                             final Properties properties,
                                             Parameter... parameters) {
        if (parameters != null && parameters.length == 0 && parameters != Parameter.ZERO) {
            parameters = null; // backwards compatibility!  solve this better later - Paul
        }
        if (componentImplementationOrInstance instanceof Class) {
            Properties tmpProperties = (Properties) properties.clone();
            ComponentAdapter<?> componentAdapter = componentFactory.createComponentAdapter(componentMonitor,
                                                                                               lifecycleStrategy,
                                                                                               tmpProperties,
                                                                                               componentKey,
                                                                                               (Class<?>)componentImplementationOrInstance,
                                                                                               parameters);
            AbstractBehaviorFactory.removePropertiesIfPresent(tmpProperties, Characteristics.USE_NAMES);
            throwIfPropertiesLeft(tmpProperties);
            return addAdapterInternal(componentAdapter);
        } else {
            ComponentAdapter<?> componentAdapter =
                new InstanceAdapter<Object>(componentKey, componentImplementationOrInstance, lifecycleStrategy, componentMonitor);
            return addAdapter(componentAdapter, properties);
        }
    }

    private void throwIfPropertiesLeft(final Properties tmpProperties) {
        if(tmpProperties.size() > 0) {
            throw new PicoCompositionException("Unprocessed Characteristics:" + tmpProperties +", refer http://picocontainer.org/unprocessed-properties-help.html");
        }
    }

    private void addOrderedComponentAdapter(final ComponentAdapter<?> componentAdapter) {
        if (!getOrderedComponentAdapters().contains(componentAdapter)) {
            getOrderedComponentAdapters().add(componentAdapter);
        }
    }

    public List<Object> getComponents() throws PicoException {
        return getComponents(Object.class);
    }

    public <T> List<T> getComponents(final Class<T> componentType) {
        if (componentType == null) {
            return Collections.emptyList();
        }

        Map<ComponentAdapter<T>, T> adapterToInstanceMap = new HashMap<ComponentAdapter<T>, T>();
        for (ComponentAdapter<?> componentAdapter : getModifiableComponentAdapterList()) {
            if (componentType.isAssignableFrom(componentAdapter.getComponentImplementation())) {
                ComponentAdapter<T> typedComponentAdapter = typeComponentAdapter(componentAdapter);
                T componentInstance = getLocalInstance(typedComponentAdapter);

                adapterToInstanceMap.put(typedComponentAdapter, componentInstance);
            }
        }
        List<T> result = new ArrayList<T>();
        for (ComponentAdapter<?> componentAdapter : getOrderedComponentAdapters()) {
            final T componentInstance = adapterToInstanceMap.get(componentAdapter);
            if (componentInstance != null) {
                // may be null in the case of the "implicit" addAdapter
                // representing "this".
                result.add(componentInstance);
            }
        }
        return result;
    }

    private <T> T getLocalInstance(final ComponentAdapter<T> typedComponentAdapter) {
        T componentInstance = typedComponentAdapter.getComponentInstance(this);

        // This is to ensure all are added. (Indirect dependencies will be added
        // from InstantiatingComponentAdapter).
        addOrderedComponentAdapter(typedComponentAdapter);

        return componentInstance;
    }

    @SuppressWarnings({ "unchecked" })
    private static <T> ComponentAdapter<T> typeComponentAdapter(final ComponentAdapter<?> componentAdapter) {
        return (ComponentAdapter<T>)componentAdapter;
    }

    public Object getComponent(final Object componentKeyOrType) {
        return getComponent(componentKeyOrType, null);
    }

    public Object getComponent(final Object componentKeyOrType, final Class<? extends Annotation> annotation) {
        Object retVal;
        if (annotation != null) {
            final ComponentAdapter<?> componentAdapter = getComponentAdapter((Class<?>)componentKeyOrType, annotation);
            retVal = componentAdapter == null ? null : getInstance(componentAdapter, null);
        } else if (componentKeyOrType instanceof Class) {
            final ComponentAdapter<?> componentAdapter = getComponentAdapter((Class<?>)componentKeyOrType, (NameBinding) null);
            retVal = componentAdapter == null ? null : getInstance(componentAdapter, (Class<?>)componentKeyOrType);
        } else {
            ComponentAdapter<?> componentAdapter = getComponentAdapter(componentKeyOrType);
            retVal = componentAdapter == null ? null : getInstance(componentAdapter, null);
        }
        if (retVal == null) {
            retVal = componentMonitor.noComponentFound(this, componentKeyOrType);
        }
        return retVal;
    }

    public <T> T getComponent(final Class<T> componentType) {
        Object o = getComponent((Object)componentType, null);
        return componentType.cast(o);
    }

    public <T> T getComponent(final Class<T> componentType, final Class<? extends Annotation> binding) {
         Object o = getComponent((Object)componentType, binding);
        return componentType.cast(o);
    }


    private Object getInstance(final ComponentAdapter<?> componentAdapter, Class componentKey) {
        // check whether this is our adapter
        // we need to check this to ensure up-down dependencies cannot be followed
        final boolean isLocal = getModifiableComponentAdapterList().contains(componentAdapter);

        if (isLocal) {
            Object instance;
            try {
                if (componentAdapter instanceof FactoryInjector) {
                    instance = ((FactoryInjector) componentAdapter).getComponentInstance(this, componentKey);
                } else {
                    instance = componentAdapter.getComponentInstance(this);
                }
            } catch (AbstractInjector.CyclicDependencyException e) {
                if (parent != null) {
                    instance = getParent().getComponent(componentAdapter.getComponentKey());
                    if (instance != null) {
                        return instance;
                    }
                }
                throw e;
            }
            addOrderedComponentAdapter(componentAdapter);

            return instance;
        } else if (parent != null) {
            return getParent().getComponent(componentAdapter.getComponentKey());
        }

        return null;
    }


    /** {@inheritDoc} **/
    public PicoContainer getParent() {
        return parent;
    }

    /** {@inheritDoc} **/
    public <T> ComponentAdapter<T> removeComponentByInstance(final T componentInstance) {
        for (ComponentAdapter<?> componentAdapter : getModifiableComponentAdapterList()) {
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
     * if it is an instance of {@link Behavior lifecycle manager}.
     * The actual {@link LifecycleStrategy lifecycle strategy} supported
     * depends on the concrete implementation of the adapter.
     *
     * @see Behavior
     * @see LifecycleStrategy
     * @see #makeChildContainer()
     * @see #addChildContainer(PicoContainer)
     * @see #removeChildContainer(PicoContainer)
     */
    public void start() {
    	
    	if (!lifecycleState.isStartAllowed()) {
			throw new IllegalStateException("Cannot start.  Current container state was: " + lifecycleState);    		
    	}

    	lifecycleState = STARTED;

        startAdapters();
        childrenStarted.clear();
        for (PicoContainer child : children) {
            childrenStarted.add(new WeakReference<PicoContainer>(child));
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
     * if it is an instance of {@link Behavior lifecycle manager}.
     * The actual {@link LifecycleStrategy lifecycle strategy} supported
     * depends on the concrete implementation of the adapter.
     *
     * @see Behavior
     * @see LifecycleStrategy
     * @see #makeChildContainer()
     * @see #addChildContainer(PicoContainer)
     * @see #removeChildContainer(PicoContainer)
     */
    public void stop() {
    	if (!lifecycleState.isStopAllowed()) {
			throw new IllegalStateException("Cannot stop.  Current container state was: " + lifecycleState);    		
    	}

    	for (PicoContainer child : children) {
            if (childStarted(child)) {
                if (child instanceof Startable) {
                    ((Startable)child).stop();
                }
            }
        }
        stopAdapters();
        lifecycleState = STOPPED;
    }

    /**
     * Checks the status of the child container to see if it's been started
     * to prevent IllegalStateException upon stop
     *
     * @param child the child PicoContainer
     *
     * @return A boolean, <code>true</code> if the container is started
     */
    private boolean childStarted(final PicoContainer child) {
    	for (WeakReference<PicoContainer> eachChild : childrenStarted) {
    		PicoContainer ref = eachChild.get();
    		if (ref == null) {
    			continue;
    		}
    		
    		if (child.equals(ref)) {
    			return true;
    		}
    	}
    	return false;
    }

    /**
     * Dispose the components of this PicoContainer and all its logical child containers.
     * The lifecycle operation is delegated to the component adapter,
     * if it is an instance of {@link Behavior lifecycle manager}.
     * The actual {@link LifecycleStrategy lifecycle strategy} supported
     * depends on the concrete implementation of the adapter.
     *
     * @see Behavior
     * @see LifecycleStrategy
     * @see #makeChildContainer()
     * @see #addChildContainer(PicoContainer)
     * @see #removeChildContainer(PicoContainer)
     */
    public void dispose() {
    	if (lifecycleState.isStarted()) {
    		stop();
    	}
    	
    	if (!lifecycleState.isDisposedAllowed()) {
			throw new IllegalStateException("Cannot dispose.  Current lifecycle state is: " + lifecycleState);    		
    	}

        for (PicoContainer child : children) {
            if (child instanceof MutablePicoContainer) {
                ((Disposable)child).dispose();
            }
        }
        disposeAdapters();
        lifecycleState = DISPOSED;
    }

    public MutablePicoContainer makeChildContainer() {
        DefaultPicoContainer pc = new DefaultPicoContainer(componentFactory, lifecycleStrategy, this);
        addChildContainer(pc);
        return pc;
    }
    
    /**
     * Checks for identical references in the child container.  It doesn't
     * traverse an entire hierarchy, namely it simply checks for child containers
     * that are equal to the current container.
     * @param child
     */
    private void checkCircularChildDependencies(PicoContainer child) {
    	final String MESSAGE = "Cannot have circular dependency between parent " 
			+ this + " and child: " + child;
    	if (child == this) {
    		throw new IllegalArgumentException(MESSAGE);
    	}
    	
    	//Todo: Circular Import Dependency on AbstractDelegatingPicoContainer
    	if (child instanceof AbstractDelegatingPicoContainer) {
    		AbstractDelegatingPicoContainer delegateChild = (AbstractDelegatingPicoContainer) child;
    		while(delegateChild != null) {
    			PicoContainer delegateInstance = delegateChild.getDelegate();
    			if (this == delegateInstance) {
					throw new IllegalArgumentException(MESSAGE);
    			}
    			if (delegateInstance instanceof AbstractDelegatingPicoContainer) {
    				delegateChild = (AbstractDelegatingPicoContainer) delegateInstance;
    			} else {
    				delegateChild = null;
    			}
    			
    		}
    	}
    	
    }

    public MutablePicoContainer addChildContainer(final PicoContainer child) {
    	checkCircularChildDependencies(child);
    	if (children.add(child)) {
            // @todo Should only be added if child container has also be started
            if (lifecycleState == STARTED) {
                childrenStarted.add(new WeakReference<PicoContainer>(child));
            }
        }
        return this;
    }

    public boolean removeChildContainer(final PicoContainer child) {
        final boolean result = children.remove(child);
        WeakReference<PicoContainer> foundRef = null;
        for (WeakReference<PicoContainer> eachChild : childrenStarted) {
        	PicoContainer ref = eachChild.get();
        	if (ref.equals(child)) {
        		foundRef = eachChild;
        		break;
        	}
        }
        
        if (foundRef != null) {
        	childrenStarted.remove(foundRef);
        }
        
        return result;
    }

    public MutablePicoContainer change(final Properties... properties) {
        for (Properties c : properties) {
            Enumeration<String> e = (Enumeration<String>) c.propertyNames();
            while (e.hasMoreElements()) {
                String s = e.nextElement();
                componentProperties.setProperty(s,c.getProperty(s));
            }
        }
        return this;
    }

    public MutablePicoContainer as(final Properties... properties) {
        return new AsPropertiesPicoContainer(properties);
    }

    public void accept(final PicoVisitor visitor) {
        visitor.visitContainer(this);
        final List<ComponentAdapter<?>> componentAdapters = new ArrayList<ComponentAdapter<?>>(getComponentAdapters());
        for (ComponentAdapter<?> componentAdapter : componentAdapters) {
            componentAdapter.accept(visitor);
        }
        final List<PicoContainer> allChildren = new ArrayList<PicoContainer>(children);
        for (PicoContainer child : allChildren) {
            child.accept(visitor);
        }
    }

    /**
     * Changes monitor in the ComponentFactory, the component adapters
     * and the child containers, if these support a ComponentMonitorStrategy.
     * {@inheritDoc}
     */
    public void changeMonitor(final ComponentMonitor monitor) {
        this.componentMonitor = monitor;
        if (lifecycleStrategy instanceof ComponentMonitorStrategy) {
            ((ComponentMonitorStrategy)lifecycleStrategy).changeMonitor(monitor);
        }
        for (ComponentAdapter<?> adapter : getModifiableComponentAdapterList()) {
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
     * Returns the first current monitor found in the ComponentFactory, the component adapters
     * and the child containers, if these support a ComponentMonitorStrategy.
     * {@inheritDoc}
     *
     * @throws PicoCompositionException if no component monitor is found in container or its children
     */
    public ComponentMonitor currentMonitor() {
        return componentMonitor;
    }

    /**
     * {@inheritDoc}
     * Loops over all component adapters and invokes
     * start(PicoContainer) method on the ones which are LifecycleManagers
     */
    private void startAdapters() {
        Collection<ComponentAdapter<?>> adapters = getComponentAdapters();
        for (ComponentAdapter<?> adapter : adapters) {
            if (adapter instanceof Behavior) {
                Behavior<?> behaviorAdapter = (Behavior<?>)adapter;
                if (behaviorAdapter.componentHasLifecycle()) {
                    // create an instance, it will be added to the ordered CA list
                    adapter.getComponentInstance(DefaultPicoContainer.this);
                    addOrderedComponentAdapter(adapter);
                }
            }
        }
        adapters = getOrderedComponentAdapters();
        // clear list of started CAs
        startedComponentAdapters.clear();
        // clone the adapters
        List<ComponentAdapter<?>> adaptersClone = new ArrayList<ComponentAdapter<?>>(adapters);
        for (final ComponentAdapter<?> adapter : adaptersClone) {
            if (adapter instanceof Behavior) {
                Behavior<?> manager = (Behavior<?>)adapter;
                manager.start(DefaultPicoContainer.this);
                startedComponentAdapters.add(new WeakReference<ComponentAdapter<?>>(adapter));
            }
        }
    }

    /**
     * {@inheritDoc}
     * Loops over started component adapters (in inverse order) and invokes
     * stop(PicoContainer) method on the ones which are LifecycleManagers
     */
    private void stopAdapters() {
        for (int i = startedComponentAdapters.size() - 1; 0 <= i; i--) {
            ComponentAdapter<?> adapter = startedComponentAdapters.get(i).get();
            if (adapter == null) {
            	//Weak reference -- may be null
            	continue;
            }
            if (adapter instanceof Behavior) {
                Behavior<?> manager = (Behavior<?>)adapter;
                manager.stop(DefaultPicoContainer.this);
            }
        }
    }

    /**
     * {@inheritDoc}
     * Loops over all component adapters (in inverse order) and invokes
     * dispose(PicoContainer) method on the ones which are LifecycleManagers
     */
    private void disposeAdapters() {
        for (int i = getOrderedComponentAdapters().size() - 1; 0 <= i; i--) {
            ComponentAdapter<?> adapter = getOrderedComponentAdapters().get(i);
            if (adapter instanceof Behavior) {
                Behavior<?>manager = (Behavior<?>)adapter;
                manager.dispose(DefaultPicoContainer.this);
            }
        }
    }



	/**
	 * @return the orderedComponentAdapters
	 */
	protected List<ComponentAdapter<?>> getOrderedComponentAdapters() {
		return orderedComponentAdapters;
	}



	/**
	 * @return the componentKeyToAdapterCache
	 */
	protected Map<Object, ComponentAdapter<?>> getComponentKeyToAdapterCache() {
		return componentKeyToAdapterCache;
	}

	/**
	 * @return the componentAdapters
	 */
	protected List<ComponentAdapter<?>> getModifiableComponentAdapterList() {
		return componentAdapters;
	}

    private class AsPropertiesPicoContainer extends AbstractDelegatingMutablePicoContainer {
        /**
		 * Serialization UUID.
		 */
		private static final long serialVersionUID = -4846748925372564136L;
		
		private final Properties properties;

        public AsPropertiesPicoContainer(final Properties... props) {
            super(DefaultPicoContainer.this);
            properties = (Properties) componentProperties.clone();
            for (Properties c : props) {
                Enumeration<?> e = c.propertyNames();
                while (e.hasMoreElements()) {
                    String s = (String)e.nextElement();
                    properties.setProperty(s,c.getProperty(s));
                }
            }
        }

        @Override
		public MutablePicoContainer makeChildContainer() {
            return getDelegate().makeChildContainer();
        }

        @Override
		public MutablePicoContainer addComponent(final Object componentKey,
                                                 final Object componentImplementationOrInstance,
                                                 final Parameter... parameters) throws PicoCompositionException {
            return DefaultPicoContainer.this.addComponent(componentKey,
                                      componentImplementationOrInstance,
                                      properties,
                                      parameters);
        }

        @Override
		public MutablePicoContainer addComponent(final Object implOrInstance) throws PicoCompositionException {
            return DefaultPicoContainer.this.addComponent(implOrInstance, properties);
        }

        @Override
		public MutablePicoContainer addAdapter(final ComponentAdapter<?> componentAdapter) throws PicoCompositionException {
            return DefaultPicoContainer.this.addAdapter(componentAdapter, properties);
        }
    }

}
