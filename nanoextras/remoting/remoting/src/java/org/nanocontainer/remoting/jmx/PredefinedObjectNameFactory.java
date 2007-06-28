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

import javax.management.DynamicMBean;
import javax.management.ObjectName;


/**
 * An ObjectNameFactory, that uses the key of the Pico component as {@link ObjectName}, if the key is of this type.
 * @author J&ouml;rg Schaible
 * @since 1.0
 */
public class PredefinedObjectNameFactory implements ObjectNameFactory {

    /**
     * Return the <code>key</code> if it is an {@link ObjectName}.
     * @see org.nanocontainer.remoting.jmx.ObjectNameFactory#create(java.lang.Object, javax.management.DynamicMBean)
     */
    public ObjectName create(final Object key, final DynamicMBean mBean) {
        return key instanceof ObjectName ? (ObjectName)key : null;
    }

}
