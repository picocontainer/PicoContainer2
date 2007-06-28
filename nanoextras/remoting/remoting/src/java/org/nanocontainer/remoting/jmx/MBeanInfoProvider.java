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

import javax.management.MBeanInfo;

import org.picocontainer.ComponentAdapter;
import org.picocontainer.PicoContainer;


/**
 * Provide a MBeanInfo for a component. Several strategies exist and can be used as plugin.
 * @author J&ouml;rg Schaible
 * @since 1.0
 */
public interface MBeanInfoProvider {

    /**
     * Provide a MBeanInfo for the given component. An implementation should not create an instance of the addComponent
     * though.
     * @param picoContainer The picoContainer to resolve dependencies or other services necessary to get the MBeanInfo.
     * @param componentAdapter The ComponentAdapter of the component.
     * @return Returns the MBeanInfo for the compoennt or <code>null</code> if none could be found or created.
     */
    MBeanInfo provide(PicoContainer picoContainer, ComponentAdapter componentAdapter);
}
