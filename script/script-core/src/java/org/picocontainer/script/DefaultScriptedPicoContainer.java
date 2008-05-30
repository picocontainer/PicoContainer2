/*******************************************************************************
 * Copyright (C) PicoContainer Organization. All rights reserved. 
 * ---------------------------------------------------------------------------
 * The software in this package is published under the terms of the BSD style
 * license a copy of which has been included with this distribution in the
 * LICENSE.txt file. 
 ******************************************************************************/
package org.picocontainer.script;

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
import org.picocontainer.NameBinding;
import org.picocontainer.PicoVisitor;
import org.picocontainer.behaviors.Caching;
import org.picocontainer.containers.AbstractDelegatingMutablePicoContainer;

import java.io.Serializable;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
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

/**
 * Default implementation of ScriptedPicoContainer.
 * 
 * @author Paul Hammant
 * @author Mauro Talevi
 * @author Michael Rimov
 */
public class DefaultScriptedPicoContainer extends AbstractDelegatingMutablePicoContainer implements
        ScriptedPicoContainer, Serializable, ComponentMonitorStrategy {

    /**
     * Serialization UUID.
     */
    private static final long serialVersionUID = -1781615587796107858L;

    /**
     * Conversion Map to allow for primitives to be boxed to Object types.
     */
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

    public DefaultScriptedPicoContainer(ClassLoader classLoader, ComponentFactory componentFactory, PicoContainer parent) {
        super(new DefaultPicoContainer(componentFactory, parent));
        parentClassLoader = classLoader;
    }

    public DefaultScriptedPicoContainer(ClassLoader classLoader, MutablePicoContainer delegate) {
        super(delegate);
        parentClassLoader = classLoader;

    }

    public DefaultScriptedPicoContainer(ClassLoader classLoader, PicoContainer parent, ComponentMonitor componentMonitor) {
        super(new DefaultPicoContainer(new Caching(), parent));
        parentClassLoader = classLoader;
        ((ComponentMonitorStrategy) getDelegate()).changeMonitor(componentMonitor);
    }

    public DefaultScriptedPicoContainer(ComponentFactory componentFactory) {
        super(new DefaultPicoContainer(componentFactory, null));
        parentClassLoader = DefaultScriptedPicoContainer.class.getClassLoader();
    }

    public DefaultScriptedPicoContainer(PicoContainer parent) {
        super(new DefaultPicoContainer(parent));
        parentClassLoader = DefaultScriptedPicoContainer.class.getClassLoader();
    }

    public DefaultScriptedPicoContainer(MutablePicoContainer pico) {
        super(pico);
        parentClassLoader = DefaultScriptedPicoContainer.class.getClassLoader();
    }

    public DefaultScriptedPicoContainer(ClassLoader classLoader) {
        super(new DefaultPicoContainer());
        parentClassLoader = classLoader;
    }

    public DefaultScriptedPicoContainer() {
        super(new DefaultPicoContainer());
        parentClassLoader = DefaultScriptedPicoContainer.class.getClassLoader();
    }

    public DefaultScriptedPicoContainer(ComponentFactory componentFactory, LifecycleStrategy lifecycleStrategy,
            PicoContainer parent, ClassLoader cl, ComponentMonitor componentMonitor) {

        super(new DefaultPicoContainer(componentFactory, lifecycleStrategy, parent, componentMonitor));
        parentClassLoader = (cl != null) ? cl : DefaultScriptedPicoContainer.class.getClassLoader();
    }

    protected DefaultScriptedPicoContainer createChildContainer() {
        MutablePicoContainer child = getDelegate().makeChildContainer();
        DefaultScriptedPicoContainer container = new DefaultScriptedPicoContainer(getComponentClassLoader(), child);
        container.changeMonitor(currentMonitor());
        return container;
    }

    public void changeMonitor(ComponentMonitor monitor) {
        ((ComponentMonitorStrategy) getDelegate()).changeMonitor(monitor);
    }

    public ComponentMonitor currentMonitor() {
        return ((ComponentMonitorStrategy) getDelegate()).currentMonitor();
    }

    public final Object getComponent(Object componentKeyOrType) throws PicoException {

        if (componentKeyOrType instanceof ClassName) {
            componentKeyOrType = loadClass(((ClassName) componentKeyOrType).className);
        }

        Object instance = getDelegate().getComponent(componentKeyOrType);

        if (instance != null) {
            return instance;
        }

        ComponentAdapter<?> componentAdapter = null;
        if (componentKeyOrType.toString().startsWith("*")) {
            String candidateClassName = componentKeyOrType.toString().substring(1);
            Collection<ComponentAdapter<?>> cas = getComponentAdapters();
            for (ComponentAdapter<?> ca : cas) {
                Object key = ca.getComponentKey();
                if (key instanceof Class && candidateClassName.equals(((Class<?>) key).getName())) {
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
                MutablePicoContainer child = (MutablePicoContainer) o;
                return child.getComponent(remainder);
            }
        }
        return null;
    }

    public final MutablePicoContainer makeChildContainer() {
        return makeChildContainer("containers" + namedChildContainers.size());
    }

    /**
     * Makes a child container with the same basic characteristics of
     * <tt>this</tt> object (ComponentFactory, PicoContainer type, Behavior,
     * etc)
     * 
     * @param name the name of the child container
     * @return The child MutablePicoContainer
     */
    public ScriptedPicoContainer makeChildContainer(String name) {
        DefaultScriptedPicoContainer child = createChildContainer();
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

    protected final Map<String, PicoContainer> getNamedContainers() {
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
            String className = ((ClassName) implOrInstance).className;
            super.addComponent(loadClass(className));
        } else {
            super.addComponent(implOrInstance);
        }
        return this;
    }

    public MutablePicoContainer addComponent(Object key, Object componentImplementationOrInstance,
            Parameter... parameters) {
        super.addComponent(classNameToClassIfApplicable(key),
                classNameToClassIfApplicable(componentImplementationOrInstance), parameters);
        return this;
    }

    private Object classNameToClassIfApplicable(Object key) {
        if (key instanceof ClassName) {
            key = loadClass(((ClassName) key).getClassName());
        }
        return key;
    }

    public MutablePicoContainer addAdapter(ComponentAdapter<?> componentAdapter) throws PicoCompositionException {
        super.addAdapter(componentAdapter);
        return this;
    }

    public ClassLoader getComponentClassLoader() {
        if (componentClassLoader == null) {
            componentClassLoaderLocked = true;
            componentClassLoader = AccessController.doPrivileged(new PrivilegedAction<ClassLoader>() {
                public ClassLoader run() {
                    return new CustomPermissionsURLClassLoader(getURLs(classPathElements), makePermissions(),
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

    private Class<?> loadClass(final String className) {
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
            componentKey2 = loadClass(((ClassName) componentKey).className);
        }
        return super.getComponentAdapter(componentKey2);
    }

    public MutablePicoContainer change(Properties... properties) {
        super.change(properties);
        return this;
    }

    public MutablePicoContainer as(Properties... properties) {
        return new AsPropertiesPicoContainer(properties);
    }

    private class AsPropertiesPicoContainer implements ScriptedPicoContainer {
        private MutablePicoContainer delegate;

        public AsPropertiesPicoContainer(Properties... props) {
            delegate = DefaultScriptedPicoContainer.this.getDelegate().as(props);
        }

        public ClassPathElement addClassLoaderURL(URL url) {
            return DefaultScriptedPicoContainer.this.addClassLoaderURL(url);
        }

        public ClassLoader getComponentClassLoader() {
            return DefaultScriptedPicoContainer.this.getComponentClassLoader();
        }

        public ScriptedPicoContainer makeChildContainer(String name) {
            return DefaultScriptedPicoContainer.this.makeChildContainer(name);
        }

        public void addChildContainer(String name, PicoContainer child) {
            DefaultScriptedPicoContainer.this.addChildContainer(child);

        }

        public MutablePicoContainer addComponent(Object componentKey, Object componentImplementationOrInstance,
                Parameter... parameters) {
            delegate.addComponent(classNameToClassIfApplicable(componentKey),
                    classNameToClassIfApplicable(componentImplementationOrInstance), parameters);
            return DefaultScriptedPicoContainer.this;
        }

        public MutablePicoContainer addComponent(Object implOrInstance) {
            delegate.addComponent(classNameToClassIfApplicable(implOrInstance));
            return DefaultScriptedPicoContainer.this;
        }

        public MutablePicoContainer addConfig(String name, Object val) {
            delegate.addConfig(name, val);
            return DefaultScriptedPicoContainer.this;
        }

        public MutablePicoContainer addAdapter(ComponentAdapter<?> componentAdapter) {
            delegate.addAdapter(componentAdapter);
            return DefaultScriptedPicoContainer.this;
        }

        public ComponentAdapter removeComponent(Object componentKey) {
            return delegate.removeComponent(componentKey);
        }

        public ComponentAdapter removeComponentByInstance(Object componentInstance) {
            return delegate.removeComponentByInstance(componentInstance);
        }

        public MutablePicoContainer makeChildContainer() {
            return DefaultScriptedPicoContainer.this.makeChildContainer();
        }

        public MutablePicoContainer addChildContainer(PicoContainer child) {
            return DefaultScriptedPicoContainer.this.addChildContainer(child);
        }

        public boolean removeChildContainer(PicoContainer child) {
            return DefaultScriptedPicoContainer.this.removeChildContainer(child);
        }

        public MutablePicoContainer change(Properties... properties) {
            return DefaultScriptedPicoContainer.this.change(properties);
        }

        public MutablePicoContainer as(Properties... properties) {
            return new AsPropertiesPicoContainer(properties);
        }

        public Object getComponent(Object componentKeyOrType) {
            return DefaultScriptedPicoContainer.this.getComponent(componentKeyOrType);
        }

        public Object getComponent(Object componentKeyOrType, Type into) {
            return DefaultScriptedPicoContainer.this.getComponent(componentKeyOrType, into);
        }

        public <T> T getComponent(Class<T> componentType) {
            return DefaultScriptedPicoContainer.this.getComponent(componentType);
        }

        public <T> T getComponent(Class<T> componentType, Class<? extends Annotation> binding) {
            return DefaultScriptedPicoContainer.this.getComponent(componentType, binding);
        }

        public List<Object> getComponents() {
            return DefaultScriptedPicoContainer.this.getComponents();
        }

        public PicoContainer getParent() {
            return DefaultScriptedPicoContainer.this.getParent();
        }

        public ComponentAdapter<?> getComponentAdapter(Object componentKey) {
            return DefaultScriptedPicoContainer.this.getComponentAdapter(componentKey);
        }

        public <T> ComponentAdapter<T> getComponentAdapter(Class<T> componentType, NameBinding componentNameBinding) {
            return DefaultScriptedPicoContainer.this.getComponentAdapter(componentType, componentNameBinding);
        }

        public <T> ComponentAdapter<T> getComponentAdapter(Class<T> componentType, Class<? extends Annotation> binding) {
            return DefaultScriptedPicoContainer.this.getComponentAdapter(componentType, binding);
        }

        public Collection<ComponentAdapter<?>> getComponentAdapters() {
            return DefaultScriptedPicoContainer.this.getComponentAdapters();
        }

        public <T> List<ComponentAdapter<T>> getComponentAdapters(Class<T> componentType) {
            return DefaultScriptedPicoContainer.this.getComponentAdapters(componentType);
        }

        public <T> List<ComponentAdapter<T>> getComponentAdapters(Class<T> componentType,
                Class<? extends Annotation> binding) {
            return DefaultScriptedPicoContainer.this.getComponentAdapters(componentType, binding);
        }

        public <T> List<T> getComponents(Class<T> componentType) {
            return DefaultScriptedPicoContainer.this.getComponents(componentType);
        }

        public void accept(PicoVisitor visitor) {
            DefaultScriptedPicoContainer.this.accept(visitor);
        }

        public void start() {

        }

        public void stop() {

        }

        public void dispose() {

        }
    }

}
