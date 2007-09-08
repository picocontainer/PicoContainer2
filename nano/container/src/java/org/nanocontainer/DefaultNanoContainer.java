/*****************************************************************************
 * Copyright (C) NanoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the LICENSE.txt file.                                                     *
 *                                                                           *
 * Original code by Paul Hammant                                             *
 *****************************************************************************/

package org.nanocontainer;

import org.picocontainer.ComponentAdapter;
import org.picocontainer.ComponentFactory;
import org.picocontainer.ComponentMonitor;
import org.picocontainer.ComponentMonitorStrategy;
import org.picocontainer.LifecycleStrategy;
import org.picocontainer.MutablePicoContainer;
import org.picocontainer.Parameter;
import org.picocontainer.PicoClassNotFoundException;
import org.picocontainer.PicoContainer;
import org.picocontainer.PicoException;
import org.picocontainer.security.CustomPermissionsURLClassLoader;
import org.picocontainer.DefaultPicoContainer;
import org.picocontainer.PicoCompositionException;
import org.picocontainer.ParameterName;
import org.picocontainer.PicoVisitor;
import org.picocontainer.behaviors.Caching;
import org.picocontainer.containers.AbstractDelegatingMutablePicoContainer;

import java.io.Serializable;
import java.net.URL;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.security.Permissions;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Enumeration;

/**
 * This is a MutablePicoContainer that also supports soft composition. i.e. assembly by class name rather that class
 * reference.
 * <p>
 * In terms of implementation it adopts the behaviour of DefaultPicoContainer and DefaultNanoContainer.</p>
 *
 * @author Paul Hammant
 * @author Mauro Talevi
 * @author Michael Rimov
 * @version $Revision$
 */
public class DefaultNanoContainer extends AbstractDelegatingMutablePicoContainer implements NanoContainer, Serializable,
                                                                                            ComponentMonitorStrategy {

    private static final transient Map<String, String> primitiveNameToBoxedName = new HashMap<String, String>();

    static {
        primitiveNameToBoxedName.put("int", Integer.class.getName());
        primitiveNameToBoxedName.put("byte", Byte.class.getName());
        primitiveNameToBoxedName.put("short", Short.class.getName());
        primitiveNameToBoxedName.put("long", Long.class.getName());
        primitiveNameToBoxedName.put("float", Float.class.getName());
        primitiveNameToBoxedName.put("double", Double.class.getName());
        primitiveNameToBoxedName.put("boolean", Boolean.class.getName());
    }


    private final transient List<ClassPathElement> classPathElements = new ArrayList<ClassPathElement>();
    private final transient ClassLoader parentClassLoader;

    private transient ClassLoader componentClassLoader;
    private transient boolean componentClassLoaderLocked;


    protected final Map<String, PicoContainer> namedChildContainers = new HashMap<String, PicoContainer>();


    public DefaultNanoContainer(ClassLoader classLoader, ComponentFactory componentFactory, PicoContainer parent) {
        super(new DefaultPicoContainer(componentFactory, parent));
        parentClassLoader = classLoader;
    }

    public DefaultNanoContainer(ClassLoader classLoader, MutablePicoContainer delegate) {
        super(delegate);
        parentClassLoader = classLoader;

    }

    public DefaultNanoContainer(ClassLoader classLoader, PicoContainer parent, ComponentMonitor componentMonitor) {
        super(new DefaultPicoContainer(new Caching(), parent));
        parentClassLoader = classLoader;
        ((ComponentMonitorStrategy)getDelegate()).changeMonitor(componentMonitor);
    }

    public DefaultNanoContainer(ComponentFactory componentFactory) {
        super(new DefaultPicoContainer(componentFactory, null));
        parentClassLoader = DefaultNanoContainer.class.getClassLoader();
    }

    public DefaultNanoContainer(PicoContainer parent) {
        super(new DefaultPicoContainer(parent));
        parentClassLoader = DefaultNanoContainer.class.getClassLoader();
    }

    public DefaultNanoContainer(MutablePicoContainer pico) {
        super(pico);
        parentClassLoader = DefaultNanoContainer.class.getClassLoader();
    }

    public DefaultNanoContainer(ClassLoader classLoader) {
        super(new DefaultPicoContainer());
        parentClassLoader = classLoader;
    }

    public DefaultNanoContainer() {
        super(new DefaultPicoContainer());
        parentClassLoader = DefaultNanoContainer.class.getClassLoader();
    }


    /**
     * Constructor that provides the same control over the nanocontainer lifecycle strategies
     * as {@link DefaultPicoContainer ( org.picocontainer.ComponentFactory , org.picocontainer.LifecycleStrategy , PicoContainer)}.
     *
     * @param componentFactory componentFactory
     * @param lifecycleStrategy       LifecycleStrategy
     * @param parent                  PicoContainer may be null if there is no parent.
     * @param cl                      the Classloader to use.  May be null, in which case DefaultNanoPicoContainer.class.getClassLoader()
     *                                will be called instead.
     */
    public DefaultNanoContainer(ComponentFactory componentFactory,
                                LifecycleStrategy lifecycleStrategy, PicoContainer parent, ClassLoader cl)
    {

        super(new DefaultPicoContainer(componentFactory, lifecycleStrategy, parent));
        parentClassLoader = (cl != null) ? cl : DefaultNanoContainer.class.getClassLoader();
    }


    protected DefaultNanoContainer createChildContainer() {
        MutablePicoContainer child = getDelegate().makeChildContainer();
        DefaultNanoContainer container = new DefaultNanoContainer(getComponentClassLoader(), child);
        container.changeMonitor(currentMonitor());
        return container;
    }

    public void changeMonitor(ComponentMonitor monitor) {
        ((ComponentMonitorStrategy)getDelegate()).changeMonitor(monitor);
    }

    public ComponentMonitor currentMonitor() {
        return ((ComponentMonitorStrategy)getDelegate()).currentMonitor();
    }

    public final Object getComponent(Object componentKeyOrType) throws PicoException {

        if (componentKeyOrType instanceof ClassName) {
            componentKeyOrType = loadClass(((ClassName)componentKeyOrType).className);
        }

        Object instance = getDelegate().getComponent(componentKeyOrType);

        if (instance != null) {
            return instance;
        }

        ComponentAdapter componentAdapter = null;
        if (componentKeyOrType.toString().startsWith("*")) {
            String candidateClassName = componentKeyOrType.toString().substring(1);
            Collection<ComponentAdapter<?>> cas = getComponentAdapters();
            for (ComponentAdapter ca : cas) {
                Object key = ca.getComponentKey();
                if (key instanceof Class && candidateClassName.equals(((Class)key).getName())) {
                    componentAdapter = ca;
                    break;
                }
            }
        }
        if (componentAdapter != null) {
            return componentAdapter.getComponentInstance(this);
        } else {
            return getComponentInstanceFromChildren(componentKeyOrType);
        }
    }

    private Object getComponentInstanceFromChildren(Object componentKey) {
        String componentKeyPath = componentKey.toString();
        int ix = componentKeyPath.indexOf('/');
        if (ix != -1) {
            String firstElement = componentKeyPath.substring(0, ix);
            String remainder = componentKeyPath.substring(ix + 1, componentKeyPath.length());
            Object o = getNamedContainers().get(firstElement);
            if (o != null) {
                MutablePicoContainer child = (MutablePicoContainer)o;
                return child.getComponent(remainder);
            }
        }
        return null;
    }

    public final MutablePicoContainer makeChildContainer() {
        return makeChildContainer("containers" + namedChildContainers.size());
    }

    /**
     * Makes a child container with the same basic characteristics of <tt>this</tt>
     * object (ComponentFactory, PicoContainer type, Behavior, etc)
     *
     * @param name the name of the child container
     *
     * @return The child MutablePicoContainer
     */
    public MutablePicoContainer makeChildContainer(String name) {
        DefaultNanoContainer child = createChildContainer();
        MutablePicoContainer parentDelegate = getDelegate();
        parentDelegate.removeChildContainer(child.getDelegate());
        parentDelegate.addChildContainer(child);
        namedChildContainers.put(name, child);
        return child;
    }

    public boolean removeChildContainer(PicoContainer child) {
        boolean result = getDelegate().removeChildContainer(child);
        Iterator<Map.Entry<String, PicoContainer>> children = namedChildContainers.entrySet().iterator();
        while (children.hasNext()) {
            Map.Entry<String, PicoContainer> e = children.next();
            PicoContainer pc = e.getValue();
            if (pc == child) {
                children.remove();
            }
        }
        return result;
    }

    protected final Map getNamedContainers() {
        return namedChildContainers;
    }


    public ClassPathElement addClassLoaderURL(URL url) {
        if (componentClassLoaderLocked) {
            throw new IllegalStateException("ClassLoader URLs cannot be added once this instance is locked");
        }

        ClassPathElement classPathElement = new ClassPathElement(url);
        classPathElements.add(classPathElement);
        return classPathElement;
    }

    public MutablePicoContainer addComponent(Object implOrInstance) {
        if (implOrInstance instanceof ClassName) {
            String className = ((ClassName)implOrInstance).className;
            super.addComponent(loadClass(className));
        } else {
            super.addComponent(implOrInstance);
        }
        return this;
    }

    public MutablePicoContainer addComponent(Object key,
                                             Object componentImplementationOrInstance,
                                             Parameter... parameters) {
        super.addComponent(classNameToClassIfApplicable(key), classNameToClassIfApplicable(componentImplementationOrInstance), parameters);
        return this;
    }

    private Object classNameToClassIfApplicable(Object key) {
        if (key instanceof ClassName) {
            key = loadClass(((ClassName)key).getClassName());
        }
        return key;
    }

    public MutablePicoContainer addAdapter(ComponentAdapter componentAdapter) throws PicoCompositionException {
        super.addAdapter(componentAdapter);
        return this;
    }

    public ClassLoader getComponentClassLoader() {
        if (componentClassLoader == null) {
            componentClassLoaderLocked = true;
            componentClassLoader = AccessController.doPrivileged(new PrivilegedAction<ClassLoader>() {
                public ClassLoader run() {
                    return new CustomPermissionsURLClassLoader(getURLs(classPathElements),
                                                               makePermissions(),
                                                               parentClassLoader);
                }
            });
        }
        return componentClassLoader;
    }

    public MutablePicoContainer addChildContainer(PicoContainer child) {
        getDelegate().addChildContainer(child);
        namedChildContainers.put("containers" + namedChildContainers.size(), child);
        return this;
    }

    public void addChildContainer(String name, PicoContainer child) {

        super.addChildContainer(child);

        namedChildContainers.put(name, child);
    }

    private Class loadClass(final String className) {
        ClassLoader classLoader = getComponentClassLoader();
        String cn = getClassName(className);
        try {
            return classLoader.loadClass(cn);
        } catch (ClassNotFoundException e) {
            throw new PicoClassNotFoundException(cn, e);
        }
    }

    private Map<URL, Permissions> makePermissions() {
        Map<URL, Permissions> permissionsMap = new HashMap<URL, Permissions>();
        for (ClassPathElement cpe : classPathElements) {
            Permissions permissionCollection = cpe.getPermissionCollection();
            permissionsMap.put(cpe.getUrl(), permissionCollection);
        }
        return permissionsMap;
    }

    private URL[] getURLs(List<ClassPathElement> classPathElemelements) {
        final URL[] urls = new URL[classPathElemelements.size()];
        for (int i = 0; i < urls.length; i++) {
            urls[i] = (classPathElemelements.get(i)).getUrl();
        }
        return urls;
    }

    private static String getClassName(String primitiveOrClass) {
        String fromMap = primitiveNameToBoxedName.get(primitiveOrClass);
        return fromMap != null ? fromMap : primitiveOrClass;
    }


    public ComponentAdapter<?> getComponentAdapter(Object componentKey) {
        Object componentKey2 = componentKey;
        if (componentKey instanceof ClassName) {
            componentKey2 = loadClass(((ClassName)componentKey).className);
        }
        return super.getComponentAdapter(componentKey2);
    }


    public MutablePicoContainer change(Properties... properties) {
        super.change(properties);
        return this;
    }

    public MutablePicoContainer as(Properties... properties) {
        return new AsPropertiesNanoContainer(properties);
    }

    private class AsPropertiesNanoContainer implements NanoContainer {
        private MutablePicoContainer delegate;

        public AsPropertiesNanoContainer(Properties... props) {
            delegate = DefaultNanoContainer.this.getDelegate().as(props);
        }

        public ClassPathElement addClassLoaderURL(URL url) {
            return DefaultNanoContainer.this.addClassLoaderURL(url);
        }

        public ClassLoader getComponentClassLoader() {
            return DefaultNanoContainer.this.getComponentClassLoader();
        }

        public MutablePicoContainer makeChildContainer(String name) {
            return DefaultNanoContainer.this.makeChildContainer(name);
        }

        public void addChildContainer(String name, PicoContainer child) {
            DefaultNanoContainer.this.addChildContainer(child);

        }

        public MutablePicoContainer addComponent(Object componentKey,
                                                 Object componentImplementationOrInstance,
                                                 Parameter... parameters)
        {
            delegate.addComponent(classNameToClassIfApplicable(componentKey), classNameToClassIfApplicable(componentImplementationOrInstance), parameters);
            return DefaultNanoContainer.this;
        }

        public MutablePicoContainer addComponent(Object implOrInstance) {
            delegate.addComponent(classNameToClassIfApplicable(implOrInstance));
            return DefaultNanoContainer.this;
        }

        public MutablePicoContainer addConfig(String name, Object val) {
            delegate.addConfig(name, val);
            return DefaultNanoContainer.this;
        }

        public MutablePicoContainer addAdapter(ComponentAdapter componentAdapter) {
            delegate.addAdapter(componentAdapter);
            return DefaultNanoContainer.this;
        }

        public ComponentAdapter removeComponent(Object componentKey) {
            return delegate.removeComponent(componentKey);
        }

        public ComponentAdapter removeComponentByInstance(Object componentInstance) {
            return delegate.removeComponentByInstance(componentInstance);
        }

        public MutablePicoContainer makeChildContainer() {
            return DefaultNanoContainer.this.makeChildContainer();
        }

        public MutablePicoContainer addChildContainer(PicoContainer child) {
            return DefaultNanoContainer.this.addChildContainer(child);
        }

        public boolean removeChildContainer(PicoContainer child) {
            return DefaultNanoContainer.this.removeChildContainer(child);
        }

        public MutablePicoContainer change(Properties... properties) {
            return DefaultNanoContainer.this.change(properties);
        }

        public MutablePicoContainer as(Properties... properties) {
            return new AsPropertiesNanoContainer(properties);
        }

        public Object getComponent(Object componentKeyOrType) {
            return DefaultNanoContainer.this.getComponent(componentKeyOrType);
        }

        public <T> T getComponent(Class<T> componentType) {
            return DefaultNanoContainer.this.getComponent(componentType);
        }

        public List getComponents() {
            return DefaultNanoContainer.this.getComponents();
        }

        public PicoContainer getParent() {
            return DefaultNanoContainer.this.getParent();
        }

        public ComponentAdapter<?> getComponentAdapter(Object componentKey) {
            return DefaultNanoContainer.this.getComponentAdapter(componentKey);
        }

        public <T> ComponentAdapter<T> getComponentAdapter(Class<T> componentType, ParameterName componentParameterName) {
            return DefaultNanoContainer.this.getComponentAdapter(componentType, componentParameterName);
        }

        public Collection<ComponentAdapter<?>> getComponentAdapters() {
            return DefaultNanoContainer.this.getComponentAdapters();
        }

        public <T> List<ComponentAdapter<T>> getComponentAdapters(Class<T> componentType) {
            return DefaultNanoContainer.this.getComponentAdapters(componentType);
        }

        public <T> List<T> getComponents(Class<T> componentType) {
            return DefaultNanoContainer.this.getComponents();
        }

        public void accept(PicoVisitor visitor) {
            DefaultNanoContainer.this.accept(visitor);
        }

        public void start() {

        }

        public void stop() {

        }

        public void dispose() {

        }
    }


}