/*****************************************************************************
 * Copyright (C) NanoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the LICENSE.txt file.                                                     *
 *                                                                           *
 * Original code by Joerg Schaible                                           *
 *****************************************************************************/

package org.picocontainer.gems.jmx;

import org.picocontainer.gems.jmx.testmodel.OtherPerson;
import org.picocontainer.gems.jmx.testmodel.Person;
import org.picocontainer.gems.jmx.testmodel.PersonMBean;

import org.jmock.MockObjectTestCase;
import org.jmock.util.Dummy;


/**
 * @author J&ouml;rg Schaible
 */
public class NamingConventionConstructingProviderTest extends MockObjectTestCase {

    private ObjectNameFactory nameFactory;

    protected void setUp() throws Exception {
        nameFactory = (ObjectNameFactory)Dummy.newDummy(ObjectNameFactory.class);
    }

    public void testObjectNameFactoryMustNotBeNull() {
        try {
            new NamingConventionConstructingProvider(null);
            fail("NullPointerException expected");
        } catch (final NullPointerException e) {
        }
    }

    public void testGivenObjectNameFactoryIsProvided() {
        final NamingConventionConstructingProvider provider = new NamingConventionConstructingProvider(nameFactory);
        assertSame(nameFactory, provider.getObjectNameFactory());
    }

    public void testReusesMBeanFactory() {
        final NamingConventionConstructingProvider provider = new NamingConventionConstructingProvider(nameFactory);
        final DynamicMBeanFactory beanFactory = provider.getMBeanFactory();
        assertNotNull(beanFactory);
        assertSame(beanFactory, provider.getMBeanFactory());
    }

    public void testUsesNamingConventionMBeanInfoProvidersInRightSequence() {
        final NamingConventionConstructingProvider provider = new NamingConventionConstructingProvider(nameFactory);
        final MBeanInfoProvider[] infoProviders = provider.getMBeanInfoProviders();
        assertNotNull(infoProviders);
        assertEquals(2, infoProviders.length);
        assertTrue(infoProviders[0] instanceof ComponentKeyConventionMBeanInfoProvider);
        assertTrue(infoProviders[1] instanceof ComponentTypeConventionMBeanInfoProvider);
        assertSame(infoProviders, provider.getMBeanInfoProviders());
    }

    public void testFindsManagementInterfaceAccordingNamingConventions() throws ClassNotFoundException {
        final NamingConventionConstructingProvider provider = new NamingConventionConstructingProvider(nameFactory);
        assertSame(PersonMBean.class, provider.getManagementInterface(Person.class, null));
    }

    public void testThrowsClassNotFoundExceptionIfNoManagementInterfaceCanBeFound() {
        final NamingConventionConstructingProvider provider = new NamingConventionConstructingProvider(nameFactory);
        try {
            provider.getManagementInterface(OtherPerson.class, null);
            fail("ClassNotFoundException expected");
        } catch (final ClassNotFoundException e) {
        }
    }
}
