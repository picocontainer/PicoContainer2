/*****************************************************************************
 * Copyright (C) NanoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the LICENSE.txt file.                                                     *
 *                                                                           *
 * Original code by Michael Ward                                             *
 *****************************************************************************/

package org.picocontainer.gems.jmx;

import javax.management.DynamicMBean;
import javax.management.MBeanInfo;
import javax.management.modelmbean.ModelMBeanAttributeInfo;
import javax.management.modelmbean.ModelMBeanInfoSupport;

import org.picocontainer.gems.jmx.testmodel.Person;
import org.picocontainer.gems.jmx.testmodel.PersonMBean;
import org.picocontainer.testmodel.SimpleTouchable;
import org.picocontainer.testmodel.Touchable;

import junit.framework.TestCase;


/**
 * @author Michael Ward
 * @author J&ouml;rg Schaible
 * @version $Revision$
 */
public class StandardMBeanFactoryTestCase extends TestCase {

    public void testMBeanCreationWithMBeanInfo() {
        final DynamicMBeanFactory factory = new StandardMBeanFactory();
        final MBeanInfo mBeanInfo = Person.createMBeanInfo();
        final DynamicMBean mBean = factory.create(new Person(), null, mBeanInfo);
        assertNotNull(mBean);
        assertEquals(mBeanInfo, mBean.getMBeanInfo());
    }

    public void testMBeanCreationWithoutMBeanInfo() {
        final DynamicMBeanFactory factory = new StandardMBeanFactory();
        final DynamicMBean mBean = factory.create(new Person(), null, null);
        assertNotNull(mBean);
        assertNotNull(mBean.getMBeanInfo());
    }

    public void testMBeanCreationWithMBeanInfoAndArbitraryInterfaceName() {
        final DynamicMBeanFactory factory = new StandardMBeanFactory();
        final MBeanInfo mBeanInfo = Person.createMBeanInfo();
        final DynamicMBean mBean = factory.create(new SimpleTouchable(), Touchable.class, mBeanInfo);
        assertNotNull(mBean);
    }

    public void testMBeanCreationFailsWithoutManagementInterface() {
        final DynamicMBeanFactory factory = new StandardMBeanFactory();
        final MBeanInfo mBeanInfo = Person.createMBeanInfo();
        try {
            factory.create(new SimpleTouchable(), null, mBeanInfo);
            fail("JMXRegistrationException expected");
        } catch (final JMXRegistrationException e) {
            // fine
        }
    }

    public void testMBeanCreationWithoutManagementInterfaceWorksForModelMBeanInfo() {
        final DynamicMBeanFactory factory = new StandardMBeanFactory();
        final ModelMBeanAttributeInfo[] attributes = new ModelMBeanAttributeInfo[]{new ModelMBeanAttributeInfo(
                "Name", String.class.getName(), "desc", true, false, false)};
        final MBeanInfo mBeanInfo = new ModelMBeanInfoSupport(
                Person.class.getName(), "Description of Person", attributes, null, null, null);

        final DynamicMBean mBean = factory.create(new SimpleTouchable(), null, mBeanInfo);
        assertNotNull(mBean);
    }

    public void testGetDefaultManagementInterfaceFromMBeanType() throws ClassNotFoundException {
        final StandardMBeanFactory factory = new StandardMBeanFactory();
        assertSame(PersonMBean.class, factory.getDefaultManagementInterface(Person.class, null));
    }

    public void testGetDefaultManagementInterfaceFromMBeanInfo() throws ClassNotFoundException {
        final StandardMBeanFactory factory = new StandardMBeanFactory();
        final MBeanInfo mBeanInfo = Person.createMBeanInfo();
        assertSame(PersonMBean.class, factory.getDefaultManagementInterface(SimpleTouchable.class, mBeanInfo));
    }
}
