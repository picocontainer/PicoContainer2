/*****************************************************************************
 * Copyright (C) NanoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the LICENSE.txt file.                                                     *
 *                                                                           *
 * Original code by Joerg Schaibe                                            *
 *****************************************************************************/

package org.picocontainer.gems.behaviors;

import com.thoughtworks.proxy.ProxyFactory;
import com.thoughtworks.proxy.factory.StandardProxyFactory;
import com.thoughtworks.proxy.toys.delegate.Delegating;

import org.picocontainer.ComponentAdapter;
import org.picocontainer.PicoContainer;
import org.picocontainer.PicoCompositionException;
import org.picocontainer.behaviors.AbstractBehavior;

import java.lang.reflect.Method;


/**
 * ComponentAdapter, that assimilates a component for a specific type.
 * <p>
 * Allows the instance of another {@link ComponentAdapter} to be converted into interfacte <code>type</code>, that the
 * instance is not assignable from. In other words the instance of the delegated adapter does NOT necessarily implement the
 * <code>type</code> interface.
 * </p>
 * <p>
 * For Example:
 * </p>
 * <code><pre>
 * public interface Foo {
 *     int size();
 * }
 *        
 * public class Bar {
 *     public int size() {
 *         return 1;
 *     }
 * }
 *        
 * new AssimilatingBehavior(Foo.class, new InstanceAdapter(new Bar()));
 * </pre></code>
 * <p>
 * Notice how Bar does not implement the interface Foo. But Bar does have an identical <code>size()</code> method.
 * </p>
 * 
 * @author J&ouml;rg Schaible
 * @author Michael Ward
 * @since 1.2
 */
public final class AssimilatingBehavior extends AbstractBehavior {

    private final Class type;
    private final ProxyFactory proxyFactory;
    private final boolean isCompatible;

    /**
     * Construct an AssimilatingBehavior. The <code>type</code> may not implement the type of the component instance.
     * If the component instance <b>does</b> implement the interface, no proxy is used though.
     * 
     * @param type The class type used as key.
     * @param delegate The delegated {@link ComponentAdapter}.
     * @param proxyFactory The {@link ProxyFactory} to use.
     * @throws PicoCompositionException Thrown if the <code>type</code> is not compatible and cannot be proxied.
     */
    public AssimilatingBehavior(final Class type, final ComponentAdapter delegate, final ProxyFactory proxyFactory)
            throws PicoCompositionException
    {
        super(delegate);
        this.type = type;
        this.proxyFactory = proxyFactory;
        final Class delegationType = delegate.getComponentImplementation();
        this.isCompatible = type.isAssignableFrom(delegationType);
        if (!isCompatible) {
            if (!proxyFactory.canProxy(type)) {
                throw new PicoCompositionException("Cannot create proxy for type " + type.getName());
            }
            final Method[] methods = type.getMethods();
            for (final Method method : methods) {
                try {
                    delegationType.getMethod(method.getName(), method.getParameterTypes());
                } catch (final NoSuchMethodException e) {
                    throw new PicoCompositionException("Cannot create proxy for type "
                                                         + type.getName()
                                                         + ", because of incompatible method "
                                                         + method.toString());
                }
            }
        }
    }

    /**
     * Construct an AssimilatingBehavior. The <code>type</code> may not implement the type of the component instance.
     * The implementation will use JDK {@link java.lang.reflect.Proxy} instances. If the component instant <b>does </b>
     * implement the interface, no proxy is used anyway.
     * 
     * @param type The class type used as key.
     * @param delegate The delegated {@link ComponentAdapter}.
     */
    public AssimilatingBehavior(final Class type, final ComponentAdapter delegate) {
        this(type, delegate, new StandardProxyFactory());
    }

    /**
     * Create and return a component instance. If the component instance and the type to assimilate is not compatible, a proxy
     * for the instance is generated, that implements the assimilated type.
     * 
     * @see AbstractBehavior#getComponentInstance(org.picocontainer.PicoContainer)
     */
    public Object getComponentInstance(final PicoContainer container)
            throws PicoCompositionException
    {
        return isCompatible ? super.getComponentInstance(container) : Delegating.object(
                type, super.getComponentInstance(container), proxyFactory);
    }

    /**
     * Return the type of the component. If the component type is not compatible with the type to assimilate, the assimilated
     * type is returned.
     * 
     * @see AbstractBehavior#getComponentImplementation()
     */
    public Class getComponentImplementation() {
        return isCompatible ? super.getComponentImplementation() : type;
    }

    /**
     * Return the key of the component. If the key of the delegated component is a type, that is not compatible with the type to
     * assimilate, then the assimilated type replaces the original type.
     * 
     * @see AbstractBehavior#getComponentKey()
     */
    public Object getComponentKey() {
        final Object key = super.getComponentKey();
        if (key instanceof Class && (!isCompatible || !type.isAssignableFrom((Class)key))) {
            return type;
        }
        return key;
    }
}
