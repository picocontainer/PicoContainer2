/*****************************************************************************
 * Copyright (C) PicoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the LICENSE.txt file.                                                     *
 *                                                                           *
 * Original code by                                                          *
 *****************************************************************************/
package org.picocontainer.behaviors;

import org.picocontainer.ComponentAdapter;
import org.picocontainer.Parameter;
import org.picocontainer.PicoCompositionException;
import org.picocontainer.ComponentMonitor;
import org.picocontainer.ComponentFactory;
import org.picocontainer.LifecycleStrategy;
import org.picocontainer.BehaviorFactory;
import org.picocontainer.injectors.AdaptiveInjectionFactory;

import java.io.Serializable;
import java.util.Properties;
import java.util.Iterator;
import java.util.Enumeration;

public class AbstractBehaviorFactory implements ComponentFactory, Serializable, BehaviorFactory {

    private ComponentFactory delegate;

    public ComponentFactory forThis(ComponentFactory delegate) {
        this.delegate = delegate;
        return this;
    }
    public ComponentAdapter createComponentAdapter(ComponentMonitor componentMonitor, LifecycleStrategy lifecycleStrategy, Properties componentProperties, Object componentKey,
                                                   Class componentImplementation,
                                                   Parameter... parameters) throws PicoCompositionException {
        if (delegate == null) {
            delegate = new AdaptiveInjectionFactory();
        }
        return delegate.createComponentAdapter(componentMonitor, lifecycleStrategy,
                                               componentProperties, componentKey, componentImplementation, parameters);
    }


    public ComponentAdapter addComponentAdapter(ComponentMonitor componentMonitor,
                                                LifecycleStrategy lifecycleStrategy,
                                                Properties componentProperties,
                                                ComponentAdapter adapter) {
        if (delegate != null && delegate instanceof BehaviorFactory) {
            return ((BehaviorFactory) delegate).addComponentAdapter(componentMonitor, lifecycleStrategy, componentProperties, adapter);
        }
        return adapter;
    }

    public static boolean removePropertiesIfPresent(Properties currProperties, Properties hasProperties) {
        Enumeration props = hasProperties.keys();
        while (props.hasMoreElements()) {
            String o = (String)props.nextElement();
            String q = hasProperties.getProperty(o);
            String p = currProperties.getProperty(o);
            if (p == null) {
                return false;
            }
            if (!q.equals(p)) {
                return false;
            }
        }
        props = hasProperties.keys();
        while (props.hasMoreElements()) {
            Object o = props.nextElement();
            currProperties.remove(o);
        }
        return true;
    }
}
