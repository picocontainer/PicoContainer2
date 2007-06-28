/*****************************************************************************
 * Copyright (C) NanoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the LICENSE.txt file.                                                     *
 *                                                                           *
 * Original code by Joerg Schaible                                  		 *
 *****************************************************************************/

package org.nanocontainer.remoting.jmx;

import javax.management.DynamicMBean;

import org.picocontainer.ComponentAdapter;
import org.picocontainer.PicoContainer;


/**
 * Provide instances of DynamicMBean from Pico components.
 * @author J&ouml;rg Schaible
 * @since 1.0
 */
public interface DynamicMBeanProvider {

    /**
     * Provide a {@link DynamicMBean} from the component delivered by the ComponentAdapter.
     * @param picoContainer The {@link PicoContainer} to resolve dependencies.
     * @param componentAdapter The {@link ComponentAdapter} referring the component.
     * @return Returns the registration information.
     */
    public JMXRegistrationInfo provide(PicoContainer picoContainer, ComponentAdapter componentAdapter);
}
