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
import org.picocontainer.ComponentCharacteristics;
import org.picocontainer.ComponentMonitor;
import org.picocontainer.LifecycleStrategy;
import org.picocontainer.InjectionFactory;

import java.io.Serializable;


/**
 * A {@link org.picocontainer.ComponentFactory} for JavaBeans.
 * The factory creates {@link SetterInjector}.
 *
 * @author J&ouml;rg Schaible
 * @version $Revision$
 */
public class SetterInjectionFactory implements InjectionFactory, Serializable {

    /**
     * Create a {@link SetterInjector}.
     *
     * @param componentMonitor
     * @param lifecycleStrategy
     * @param componentCharacteristics
     * @param componentKey                The component's key
     * @param componentImplementation     The class of the bean.
     * @param parameters                  Any parameters for the setters. If null the adapter solves the
     *                                    dependencies for all setters internally. Otherwise the number parameters must match
     *                                    the number of the setter. @return Returns a new {@link SetterInjector}. @throws PicoCompositionException if dependencies cannot be solved
     * @throws org.picocontainer.PicoCompositionException
     *          if the implementation is an interface or an
     *          abstract class.
     */
    public ComponentAdapter createComponentAdapter(ComponentMonitor componentMonitor, LifecycleStrategy lifecycleStrategy, ComponentCharacteristics componentCharacteristics, Object componentKey, Class componentImplementation, Parameter... parameters)
            throws PicoCompositionException
    {
        return new SetterInjector(componentKey, componentImplementation, parameters, componentMonitor, lifecycleStrategy);
    }
}
