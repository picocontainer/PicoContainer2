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

import org.picocontainer.ComponentAdapter;
import org.picocontainer.Parameter;
import org.picocontainer.PicoCompositionException;
import org.picocontainer.ComponentCharacteristics;
import org.picocontainer.ComponentMonitor;
import org.picocontainer.LifecycleStrategy;
import org.picocontainer.behaviors.AbstractBehaviorFactory;
import org.picocontainer.ComponentFactory;


/**
 * Factory for the AssimilatingBehavior. This factory will create {@link AssimilatingBehavior} instances for all
 * {@link ComponentAdapter} instances created by the delegate. This will assimilate every component for a specific type.
 * 
 * @author J&ouml;rg Schaible
 * @since 1.2
 */
public class AssimilatingBehaviorFactory extends AbstractBehaviorFactory {

    private final ProxyFactory proxyFactory;
    private final Class assimilationType;

    /**
     * Construct an AssimilatingBehaviorFactory. The instance will use the {@link StandardProxyFactory} using the JDK
     * implementation.
     * 
     * @param type The assimilated type.
     */
    public AssimilatingBehaviorFactory(final Class type) {
        this(type, new StandardProxyFactory());
    }

    /**
     * Construct an AssimilatingBehaviorFactory using a special {@link ProxyFactory}.
     * 
     * @param type The assimilated type.
     * @param proxyFactory The proxy factory to use.
     */
    public AssimilatingBehaviorFactory(final Class type, final ProxyFactory proxyFactory) {
        this.assimilationType = type;
        this.proxyFactory = proxyFactory;
    }

    /**
     * Create a {@link AssimilatingBehavior}. This adapter will wrap the returned {@link ComponentAdapter} of the
     * deleated {@link ComponentFactory}.
     * 
     * @see org.picocontainer.ComponentFactory#createComponentAdapter(org.picocontainer.ComponentMonitor,org.picocontainer.LifecycleStrategy,org.picocontainer.ComponentCharacteristics,Object,Class,org.picocontainer.Parameter...)
     */
    public ComponentAdapter createComponentAdapter(
            ComponentMonitor componentMonitor, LifecycleStrategy lifecycleStrategy, ComponentCharacteristics componentCharacteristics, final Object componentKey, final Class componentImplementation, final Parameter... parameters)
            throws PicoCompositionException
    {
        return new AssimilatingBehavior(assimilationType, super.createComponentAdapter(
                componentMonitor, lifecycleStrategy, null, componentKey, componentImplementation, parameters), proxyFactory);
    }
}
