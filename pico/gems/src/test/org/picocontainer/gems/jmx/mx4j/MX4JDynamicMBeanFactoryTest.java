/*****************************************************************************
 * Copyright (C) NanoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the LICENSE.txt file.                                                     *
 *                                                                           *
 * Original code by Michael Ward                                             *
 *****************************************************************************/

package org.picocontainer.gems.jmx.mx4j;

import javax.management.DynamicMBean;
import javax.management.MBeanInfo;

import org.picocontainer.gems.jmx.DynamicMBeanFactory;
import org.picocontainer.gems.jmx.testmodel.Person;
import org.picocontainer.testmodel.SimpleTouchable;

import junit.framework.TestCase;


/**
 * @author J&ouml;rg Schaible
 */
public class MX4JDynamicMBeanFactoryTest extends TestCase {

    public void testMBeanCreationFailsWithoutManagementInterface() {
        final DynamicMBeanFactory factory = new MX4JDynamicMBeanFactory();
        final MBeanInfo mBeanInfo = Person.createMBeanInfo();
        final DynamicMBean mBean = factory.create(new SimpleTouchable(), null, mBeanInfo);
        assertNotNull(mBean);
    }

}
