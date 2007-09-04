/*****************************************************************************
 * Copyright (c) PicoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the LICENSE.txt file.                                                     *
 *                                                                           *
 * Idea by Rachel Davies, Original code by Aslak Hellesoy and Paul Hammant   *
 *****************************************************************************/

package org.picocontainer.injectors;

import org.picocontainer.ComponentAdapter;
import org.picocontainer.Parameter;
import org.picocontainer.PicoCompositionException;
import org.picocontainer.ComponentMonitor;
import org.picocontainer.LifecycleStrategy;
import org.picocontainer.InjectionFactory;
import org.picocontainer.annotations.Inject;

import java.io.Serializable;
import java.util.Properties;


/**
 * A {@link org.picocontainer.InjectionFactory} for Guice-style annotated methods.
 * The factory creates {@link AnnotatedMethodInjector}.
 *
 * @author Paul Hammant
 */
public class AnnotatedMethodInjection implements InjectionFactory, Serializable {

    private final Class injectionAnnotation;
    private final boolean useNames;

    public AnnotatedMethodInjection(Class injectionAnnotation, boolean useNames) {
        this.injectionAnnotation = injectionAnnotation;
        this.useNames = useNames;
    }

    public AnnotatedMethodInjection() {
        this(Inject.class, false);
    }

    /**
     * Create a {@link SetterInjector}.
     * 
     * @param componentMonitor
     * @param lifecycleStrategy
     * @param componentProperties
     * @param componentKey The component's key
     * @param componentImplementation The class of the bean.
     * @param parameters Any parameters for the setters. If null the adapter
     *            solves the dependencies for all setters internally. Otherwise
     *            the number parameters must match the number of the setter.
     * @return Returns a new {@link SetterInjector}.
     * @throws org.picocontainer.PicoCompositionException if dependencies cannot
     *             be solved or if the implementation is an interface or an
     *             abstract class.
     */
    public <T> ComponentAdapter<T> createComponentAdapter(ComponentMonitor componentMonitor, LifecycleStrategy lifecycleStrategy, Properties componentProperties,
                                                   Object componentKey, Class<T> componentImplementation, Parameter... parameters)
            throws PicoCompositionException {
        return new AnnotatedMethodInjector(componentKey, componentImplementation, parameters, componentMonitor, lifecycleStrategy, injectionAnnotation, useNames);
    }
}