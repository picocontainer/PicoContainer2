/*******************************************************************************
 * Copyright (C) PicoContainer Organization. All rights reserved. *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD * style
 * license a copy of which has been included with this distribution in * the
 * LICENSE.txt file. * * Original code by *
 ******************************************************************************/
package org.picocontainer.behaviors;

import org.picocontainer.ComponentAdapter;
import org.picocontainer.Parameter;
import org.picocontainer.PicoCompositionException;
import org.picocontainer.ComponentMonitor;
import org.picocontainer.ComponentFactory;
import org.picocontainer.LifecycleStrategy;
import org.picocontainer.BehaviorFactory;
import org.picocontainer.injectors.AdaptiveInjection;

import java.io.Serializable;
import java.util.Properties;
import java.util.Enumeration;

public class AbstractBehaviorFactory implements ComponentFactory, Serializable, BehaviorFactory {

    private ComponentFactory delegate;

    public ComponentFactory wrap(ComponentFactory delegate) {
        this.delegate = delegate;
        return this;
    }

    public <T> ComponentAdapter<T> createComponentAdapter(ComponentMonitor componentMonitor,
            LifecycleStrategy lifecycleStrategy, Properties componentProperties, Object componentKey,
            Class<T> componentImplementation, Parameter... parameters) throws PicoCompositionException {
        if (delegate == null) {
            delegate = new AdaptiveInjection();
        }
        return delegate.createComponentAdapter(componentMonitor, lifecycleStrategy, componentProperties, componentKey,
                componentImplementation, parameters);
    }

    public <T> ComponentAdapter<T> addComponentAdapter(ComponentMonitor componentMonitor,
            LifecycleStrategy lifecycleStrategy, Properties componentProperties, ComponentAdapter<T> adapter) {
        if (delegate != null && delegate instanceof BehaviorFactory) {
            return ((BehaviorFactory) delegate).addComponentAdapter(componentMonitor, lifecycleStrategy,
                    componentProperties, adapter);
        }
        return adapter;
    }

    public static boolean removePropertiesIfPresent(Properties current, Properties present) {
        Enumeration<?> keys = present.keys();
        while (keys.hasMoreElements()) {
            String key = (String) keys.nextElement();
            String presentValue = present.getProperty(key);
            String currentValue = current.getProperty(key);
            if (currentValue == null) {
                return false;
            }
            if (!presentValue.equals(currentValue)) {
                return false;
            }
        }
        keys = present.keys();
        while (keys.hasMoreElements()) {
            Object key = keys.nextElement();
            current.remove(key);
        }
        return true;
    }

    protected void mergeProperties(Properties into, Properties from) {
        Enumeration<?> e = from.propertyNames();
        while (e.hasMoreElements()) {
            String s = (String) e.nextElement();
            into.setProperty(s, from.getProperty(s));
        }

    }
}
