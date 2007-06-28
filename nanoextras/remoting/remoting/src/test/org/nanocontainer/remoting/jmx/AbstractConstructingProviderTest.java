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
import javax.management.MBeanInfo;
import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;

import org.nanocontainer.remoting.jmx.testmodel.OtherPerson;
import org.nanocontainer.remoting.jmx.testmodel.Person;
import org.nanocontainer.remoting.jmx.testmodel.PersonMBean;
import org.picocontainer.ComponentAdapter;
import org.picocontainer.MutablePicoContainer;
import org.picocontainer.DefaultPicoContainer;

import org.jmock.Mock;
import org.jmock.MockObjectTestCase;
import org.jmock.util.Dummy;


/**
 * @author J&ouml;rg Schaible
 */
public class AbstractConstructingProviderTest extends MockObjectTestCase {

    private Mock mockObjectNameFactory;
    private Mock mockDynamicMBeanFactory;
    private MBeanInfoProvider[] mBeanInfoProviders;
    private ObjectName objectName;
    private MutablePicoContainer pico;

    private class ConstructingProvider extends AbstractConstructingProvider {

        protected ObjectNameFactory getObjectNameFactory() {
            return (ObjectNameFactory)mockObjectNameFactory.proxy();
        }

        protected MBeanInfoProvider[] getMBeanInfoProviders() {
            return mBeanInfoProviders;
        }

        protected DynamicMBeanFactory getMBeanFactory() {
            return (DynamicMBeanFactory)mockDynamicMBeanFactory.proxy();
        }

        protected Class getManagementInterface(final Class implementation, final MBeanInfo mBeanInfo)
                throws ClassNotFoundException {
            if (implementation.equals(Person.class)) {
                return PersonMBean.class;
            }
            throw new ClassNotFoundException();
        }

    }

    protected void setUp() throws Exception {
        super.setUp();
        mockObjectNameFactory = mock(ObjectNameFactory.class);
        mockDynamicMBeanFactory = mock(DynamicMBeanFactory.class);
        objectName = new ObjectName(":type=JUnit");
        pico = new DefaultPicoContainer();
        mBeanInfoProviders = new MBeanInfoProvider[0];
    }

    public void testCanCreateMBean() {
        final Person person = new Person();
        final ComponentAdapter componentAdapter = pico.addComponent(person).getComponentAdapter(person.getClass());
        final DynamicMBean dynamicMBean = (DynamicMBean)Dummy.newDummy(DynamicMBean.class);
        final Mock mockMBeanInfoProvider = mock(MBeanInfoProvider.class);
        mBeanInfoProviders = new MBeanInfoProvider[]{(MBeanInfoProvider)mockMBeanInfoProvider.proxy()};

        mockMBeanInfoProvider.expects(once()).method("provide").with(same(pico), same(componentAdapter)).will(
                returnValue(Person.createMBeanInfo()));
        mockDynamicMBeanFactory.expects(once()).method("create").with(
                same(person), same(PersonMBean.class), eq(Person.createMBeanInfo())).will(returnValue(dynamicMBean));
        mockObjectNameFactory.expects(once()).method("create").with(same(Person.class), isA(DynamicMBean.class)).will(
                returnValue(objectName));

        final DynamicMBeanProvider provider = new ConstructingProvider();
        final JMXRegistrationInfo info = provider.provide(pico, componentAdapter);
        assertNotNull(info);
    }

    public void testNoInstanceIsCreatedIfManagementInterfaceIsMissing() {
        final ComponentAdapter componentAdapter = pico.addComponent(OtherPerson.class).getComponentAdapter(OtherPerson.class);
        final DynamicMBeanProvider provider = new ConstructingProvider();
        assertNull(provider.provide(pico, componentAdapter));
    }

    public void testObjectNameMustBeGiven() {
        mockDynamicMBeanFactory.expects(once()).method("create").with(isA(Person.class), same(PersonMBean.class), NULL)
                .will(returnValue(Dummy.newDummy(DynamicMBean.class)));
        mockObjectNameFactory.expects(once()).method("create").with(same(Person.class), isA(DynamicMBean.class)).will(
                returnValue(null));

        final ComponentAdapter componentAdapter = pico.addComponent(Person.class).getComponentAdapter(Person.class);
        final DynamicMBeanProvider provider = new ConstructingProvider();
        assertNull(provider.provide(pico, componentAdapter));
    }

    public void testMalformedObjectNameThrowsJMXRegistrationException() {
        mockDynamicMBeanFactory.expects(once()).method("create").with(isA(Person.class), same(PersonMBean.class), NULL)
                .will(returnValue(Dummy.newDummy(DynamicMBean.class)));
        mockObjectNameFactory.expects(once()).method("create").with(same(Person.class), isA(DynamicMBean.class)).will(
                throwException(new MalformedObjectNameException("JUnit")));

        final ComponentAdapter componentAdapter = pico.addComponent(Person.class).getComponentAdapter(Person.class);
        final DynamicMBeanProvider provider = new ConstructingProvider();
        try {
            provider.provide(pico, componentAdapter);
            fail("JMXRegistrationException expected");
        } catch (final JMXRegistrationException e) {
            assertEquals("JUnit", e.getCause().getMessage());
        }
    }
}
