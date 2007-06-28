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
import org.picocontainer.ComponentMonitor;
import org.picocontainer.Parameter;
import org.picocontainer.PicoCompositionException;
import org.picocontainer.ComponentCharacteristics;
import org.picocontainer.LifecycleStrategy;
import org.picocontainer.InjectionFactory;

import java.io.Serializable;

/**
 * @author Jon Tirs&eacute;n
 * @version $Revision$
 */
public class ConstructorInjectionFactory implements InjectionFactory, Serializable {


    public ComponentAdapter createComponentAdapter(ComponentMonitor componentMonitor, LifecycleStrategy lifecycleStrategy, ComponentCharacteristics componentCharacteristics, Object componentKey,
                                                   Class componentImplementation,
                                                   Parameter... parameters)
            throws PicoCompositionException
    {
        return new ConstructorInjector(componentKey, componentImplementation, parameters,
                    componentMonitor, lifecycleStrategy);
    }
}
