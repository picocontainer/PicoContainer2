/*****************************************************************************
 * Copyright (C) NanoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the LICENSE.txt file.                                                     *
 *                                                                           *
 * Original code by Michael Ward                                    		 *
 *****************************************************************************/

package org.nanocontainer.remoting.jmx;

import javax.management.DynamicMBean;
import javax.management.MBeanInfo;


/**
 * This factory is responsible for creating instances of DynamicMBean without being dependent on one particular
 * implementation or external dependency.
 * @author Michael Ward
 * @author J&ouml;rg Schaible
 * @since 1.0
 */
public interface DynamicMBeanFactory {

    /**
     * Create a DynamicMBean from instance and the provided {@link MBeanInfo}.
     * @param componentInstance the instance of the Object being exposed for management.
     * @param management the management interface (can be <code>null</code>).
     * @param mBeanInfo the explicitly provided management information (can be <code>null</code>).
     * @return the {@link DynamicMBean}.
     */
    public DynamicMBean create(Object componentInstance, Class management, MBeanInfo mBeanInfo);
}
