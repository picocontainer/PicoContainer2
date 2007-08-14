/*****************************************************************************
 * Copyright (C) PicoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the LICENSE.txt file.                                                     *
 *                                                                           *
 * Original code by                                                          *
 *****************************************************************************/
package org.picocontainer.injectors;

import org.picocontainer.ComponentAdapter;
import org.picocontainer.ComponentMonitor;
import org.picocontainer.LifecycleStrategy;
import org.picocontainer.Parameter;
import org.picocontainer.PicoCompositionException;
import org.picocontainer.InjectionFactory;
import org.picocontainer.annotations.Inject;

import java.util.Properties;
import java.io.Serializable;

public class AnnotatedFieldInjection implements InjectionFactory, Serializable {

    private final Class injectionAnnotation;

    public AnnotatedFieldInjection(Class injectionAnnotation) {
        this.injectionAnnotation = injectionAnnotation;
    }

    public AnnotatedFieldInjection() {
        this(Inject.class);
    }

    public ComponentAdapter createComponentAdapter(ComponentMonitor componentMonitor,
                                                   LifecycleStrategy lifecycleStrategy,
                                                   Properties componentProperties,
                                                   Object componentKey,
                                                   Class componentImplementation,
                                                   Parameter... parameters)
        throws PicoCompositionException {
        return new AnnotatedFieldInjector(componentKey, componentImplementation, parameters, componentMonitor, lifecycleStrategy,
                                          injectionAnnotation);
    }
}
