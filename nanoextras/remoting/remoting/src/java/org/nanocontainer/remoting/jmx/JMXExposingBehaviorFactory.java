/*****************************************************************************
 * Copyright (C) NanoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the LICENSE.txt file.                                                     *
 *                                                                           *
 * Original code by Joerg Schaible                                           *
 *****************************************************************************/

package org.nanocontainer.remoting.jmx;

import javax.management.MBeanServer;

import org.picocontainer.ComponentAdapter;
import org.picocontainer.Parameter;
import org.picocontainer.PicoCompositionException;
import org.picocontainer.Characteristics;
import org.picocontainer.ComponentMonitor;
import org.picocontainer.LifecycleStrategy;
import org.picocontainer.behaviors.AbstractBehaviorFactory;

import java.util.Properties;


/**
 * {@link org.picocontainer.ComponentFactory} that instantiates {@link JMXExposingBehavior} instances.
 * @author J&ouml;rg Schaible
 */
public class JMXExposingBehaviorFactory extends AbstractBehaviorFactory {

    private final MBeanServer mBeanServer;
    private final DynamicMBeanProvider[] providers;

    /**
     * Construct a JMXExposingComponentAdapterFactory.
     * @param mBeanServer The {@link MBeanServer} used for registering the MBean.
     * @param providers An array with providers for converting the component instance into a
     *            {@link javax.management.DynamicMBean}.
     * @throws NullPointerException Thrown if the {@link MBeanServer} or the array with the {@link DynamicMBeanProvider}
     *             instances is null.
     */
    public JMXExposingBehaviorFactory(
            final MBeanServer mBeanServer,
            final DynamicMBeanProvider[] providers) throws NullPointerException {
        if (mBeanServer == null || providers == null) {
            throw new NullPointerException();
        }
        this.mBeanServer = mBeanServer;
        this.providers = providers;
    }

    /**
     * Construct a JMXExposingComponentAdapterFactory. This instance uses a {@link DynamicMBeanComponentProvider} as
     * default to register any component instance in the {@link MBeanServer}, that is already a
     * {@link javax.management.DynamicMBean}.
     * @param mBeanServer The {@link MBeanServer} used for registering the MBean.
     * @throws NullPointerException Thrown if the {@link MBeanServer} or the array with the {@link DynamicMBeanProvider}
     *             instances is null.
     */
    public JMXExposingBehaviorFactory(final MBeanServer mBeanServer)
            throws NullPointerException {
        this(mBeanServer, new DynamicMBeanProvider[]{new DynamicMBeanComponentProvider()});
    }

    /**
     * Retrieve a {@link ComponentAdapter}. Wrap the instance retrieved by the delegate with an instance of a
     * {@link JMXExposingBehavior}.
     * @see org.picocontainer.ComponentFactory#createComponentAdapter(ComponentMonitor,LifecycleStrategy,Properties,Object,Class,Parameter...)
     */
    public ComponentAdapter createComponentAdapter(
            ComponentMonitor componentMonitor, LifecycleStrategy lifecycleStrategy, Properties componentProperties, Object componentKey, Class componentImplementation, Parameter... parameters)
            throws PicoCompositionException
    {
        final ComponentAdapter componentAdapter = super.createComponentAdapter(
                componentMonitor, lifecycleStrategy,
                componentProperties, componentKey, componentImplementation, parameters);
        if (AbstractBehaviorFactory.removePropertiesIfPresent(componentProperties, Characteristics.NOJMX)) {
            return componentAdapter;            
        } else {
            return new JMXExposingBehavior(componentAdapter, mBeanServer, providers);
        }
    }

}
