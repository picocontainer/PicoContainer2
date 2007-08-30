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

import javax.management.MalformedObjectNameException;
import javax.management.NotCompliantMBeanException;

import org.picocontainer.gems.jmx.testmodel.DynamicMBeanPerson;
import org.picocontainer.gems.jmx.testmodel.Person;
import org.picocontainer.gems.jmx.testmodel.PersonMBean;
import org.picocontainer.adapters.InstanceAdapter;
import org.picocontainer.lifecycle.NullLifecycleStrategy;
import org.picocontainer.monitors.NullComponentMonitor;

import org.jmock.Mock;
import org.jmock.MockObjectTestCase;


/**
 * @author J&ouml;rg Schaible
 */
public class DynamicMBeanComponentProviderTest extends MockObjectTestCase {

    public void testDynamicMBeansAreIdentified() throws NotCompliantMBeanException {
        final PersonMBean person = new DynamicMBeanPerson();
        final DynamicMBeanProvider provider = new DynamicMBeanComponentProvider();
        JMXRegistrationInfo info = provider
                .provide(null, new InstanceAdapter(PersonMBean.class, new Person(), new NullLifecycleStrategy(),
                                                                        new NullComponentMonitor()));
        assertNull(info);
        info = provider.provide(null, new InstanceAdapter("JUnit", person, new NullLifecycleStrategy(),
                                                                        new NullComponentMonitor()));
        assertNotNull(info);
        assertSame(person, info.getMBean());
    }

    public void testDynamicMBeansAreOnlyProvidedWithObjectName() throws NotCompliantMBeanException {
        final PersonMBean person = new DynamicMBeanPerson();

        final Mock objectNameFactoryMock = mock(ObjectNameFactory.class);
        objectNameFactoryMock.expects(once()).method("create").with(eq("JUnit"), same(person)).will(returnValue(null));

        final DynamicMBeanProvider provider = new DynamicMBeanComponentProvider(
                (ObjectNameFactory)objectNameFactoryMock.proxy());
        final JMXRegistrationInfo info = provider.provide(null, new InstanceAdapter("JUnit", person, new NullLifecycleStrategy(),
                                                                        new NullComponentMonitor()));
        assertNull(info);
    }

    public void testDynamicMBeansWithMalformedObjectName() throws NotCompliantMBeanException {
        final PersonMBean person = new DynamicMBeanPerson();
        final Exception exception = new MalformedObjectNameException("JUnit");

        final Mock objectNameFactoryMock = mock(ObjectNameFactory.class);
        objectNameFactoryMock.expects(once()).method("create").with(eq("JUnit"), same(person)).will(
                throwException(exception));

        final DynamicMBeanProvider provider = new DynamicMBeanComponentProvider(
                (ObjectNameFactory)objectNameFactoryMock.proxy());
        try {
            provider.provide(null, new InstanceAdapter("JUnit", person, new NullLifecycleStrategy(),
                                                                        new NullComponentMonitor()));
            fail("JMXRegistrationException expected");
        } catch (final JMXRegistrationException e) {
            assertSame(exception, e.getCause());
        }
    }
}
