/*****************************************************************************
 * Copyright (C) NanoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the LICENSE.txt file.                                                     *
 *                                                                           *
 * Original code by Joerg Schaible                                           *
 *****************************************************************************/

package org.picocontainer.gems.jmx;

import org.picocontainer.ComponentAdapter;
import org.picocontainer.PicoContainer;
import org.picocontainer.PicoCompositionException;
import org.picocontainer.behaviors.AbstractBehavior;
import org.picocontainer.behaviors.Cached;

import java.util.List;
import java.util.ArrayList;
import javax.management.InstanceAlreadyExistsException;
import javax.management.MBeanRegistrationException;
import javax.management.MBeanServer;
import javax.management.NotCompliantMBeanException;
import javax.management.ObjectName;
import javax.management.InstanceNotFoundException;


/**
 * {@link ComponentAdapter} that is exposing a component as MBean in a MBeanServer.
 * @author J&ouml;rg Schaible
 */
public class JMXExposed extends AbstractBehavior {

    private final MBeanServer mBeanServer;
    private final DynamicMBeanProvider[] providers;
    private List registeredObjectNames;

    /**
     * Construct a JMXExposed behaviour
     * @param delegate The delegated {@link ComponentAdapter}.
     * @param mBeanServer The {@link MBeanServer} used for registering the MBean.
     * @param providers An array with providers for converting the component instance into a
     *            {@link javax.management.DynamicMBean}.
     * @throws NullPointerException Thrown if the {@link MBeanServer} or the array with the {@link DynamicMBeanProvider}
     *             instances is null.
     */
    public JMXExposed(
            final ComponentAdapter delegate, final MBeanServer mBeanServer, final DynamicMBeanProvider[] providers)
            throws NullPointerException {
        super(delegate);
        if (mBeanServer == null || providers == null) {
            throw new NullPointerException();
        }
        this.mBeanServer = mBeanServer;
        this.providers = providers;
    }

    /**
     * Construct a JMXExposed behaviour. This instance uses a {@link DynamicMBeanComponentProvider} as default to
     * register any component instance in the {@link MBeanServer}, that is already a
     * {@link javax.management.DynamicMBean}.
     * @param delegate The delegated {@link ComponentAdapter}.
     * @param mBeanServer The {@link MBeanServer} used for registering the MBean.
     * @throws NullPointerException Thrown if the {@link MBeanServer} or the array with the {@link DynamicMBeanProvider}
     *             instances is null.
     */
    public JMXExposed(final ComponentAdapter delegate, final MBeanServer mBeanServer)
            throws NullPointerException {
        this(delegate, mBeanServer, new DynamicMBeanProvider[]{new DynamicMBeanComponentProvider()});
    }

    /**
     * Retrieve the component instance. The implementation will automatically register it in the {@link MBeanServer},
     * if a provider can return a {@link javax.management.DynamicMBean} for it.
     * <p>
     * Note, that you will have to wrap this {@link ComponentAdapter} with a {@link Cached} to avoid
     * the registration of the same component again.
     * </p>
     * @throws PicoCompositionException Thrown by the delegate or if the registering of the
     *             {@link javax.management.DynamicMBean} in the {@link MBeanServer } fails.
     * @see AbstractBehavior#getComponentInstance(org.picocontainer.PicoContainer)
     */
    public Object getComponentInstance(final PicoContainer container)
            throws PicoCompositionException
    {
        final ComponentAdapter componentAdapter = new Cached(getDelegate());
        final Object componentInstance = componentAdapter.getComponentInstance(container);
        for (DynamicMBeanProvider provider : providers) {
            final JMXRegistrationInfo info = provider.provide(container, componentAdapter);
            if (info != null) {
                Exception exception = null;
                try {
                    mBeanServer.registerMBean(info.getMBean(), info.getObjectName());
                } catch (final InstanceAlreadyExistsException e) {
                    exception = e;
                } catch (final MBeanRegistrationException e) {
                    exception = e;
                } catch (final NotCompliantMBeanException e) {
                    exception = e;
                }
                if (null == registeredObjectNames) {
                    registeredObjectNames = new ArrayList();
                }
                registeredObjectNames.add(info.getObjectName());
                if (exception != null) {
                    throw new PicoCompositionException("Registering MBean failed", exception);
                }
            }
        }
        return componentInstance;
    }

    public String getDescriptor() {
        return "ExposedJMX";
    }

    public void dispose(Object component) {
        if( null != registeredObjectNames ) {
            for (Object registeredObjectName : registeredObjectNames) {
                try {
                    mBeanServer.unregisterMBean((ObjectName)registeredObjectName);
                } catch (InstanceNotFoundException e) {
                    throw new JMXRegistrationException(e);
                } catch (MBeanRegistrationException e) {
                    throw new JMXRegistrationException(e);
                }
            }
        }

		if( super.hasLifecycle( getComponentImplementation( ) ) ) {
			super.dispose(component);
		}
	}

	public boolean hasLifecycle( Class type ) {
		return true;
	}

}
