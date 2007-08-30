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

import junit.framework.TestCase;
import org.jmock.Mock;
import org.jmock.MockObjectTestCase;
import org.picocontainer.gems.jmx.testmodel.DynamicMBeanPerson;
import org.picocontainer.gems.jmx.testmodel.Person;
import org.picocontainer.gems.jmx.testmodel.PersonMBean;
import org.picocontainer.ComponentAdapter;
import org.picocontainer.PicoCompositionException;
import org.picocontainer.monitors.NullComponentMonitor;
import org.picocontainer.lifecycle.NullLifecycleStrategy;
import org.picocontainer.adapters.InstanceAdapter;

import javax.management.DynamicMBean;
import javax.management.InstanceAlreadyExistsException;
import javax.management.MBeanRegistrationException;
import javax.management.MBeanServer;
import javax.management.MalformedObjectNameException;
import javax.management.NotCompliantMBeanException;
import javax.management.ObjectName;


/**
 * @author J&ouml;rg Schaible
 */
public class JMXExposedTestCase extends MockObjectTestCase {

    private Mock mockMBeanServer;

    protected void setUp() throws Exception {
        super.setUp();
        mockMBeanServer = mock(MBeanServer.class);
    }

    public void testWillRegisterByDefaultComponentsThatAreMBeans() throws NotCompliantMBeanException {
        final PersonMBean person = new DynamicMBeanPerson();
        final ComponentAdapter componentAdapter = new JMXExposed(new InstanceAdapter(
                PersonMBean.class, person, new NullLifecycleStrategy(),
                                                                        new NullComponentMonitor()), (MBeanServer)mockMBeanServer.proxy());

        mockMBeanServer.expects(once()).method("registerMBean").with(same(person), isA(ObjectName.class));

        assertSame(person, componentAdapter.getComponentInstance(null));
    }

    public void testWillRegisterAndUnRegisterByDefaultComponentsThatAreMBeans() throws NotCompliantMBeanException {
        final PersonMBean person = new DynamicMBeanPerson();
        final JMXExposed componentAdapter = new JMXExposed(new InstanceAdapter(
                PersonMBean.class, person, new NullLifecycleStrategy(),
                                                                        new NullComponentMonitor()), (MBeanServer)mockMBeanServer.proxy());

        mockMBeanServer.expects(once()).method("registerMBean").with(same(person), isA(ObjectName.class));
        mockMBeanServer.expects(once()).method("unregisterMBean").with(isA(ObjectName.class));

        assertSame(person, componentAdapter.getComponentInstance(null));
        componentAdapter.dispose( person );
    }

    public void testWillTryAnyDynamicMBeanProvider() throws MalformedObjectNameException, NotCompliantMBeanException {
        final Person person = new Person();
        final Mock mockProvider1 = mock(DynamicMBeanProvider.class);
        final Mock mockProvider2 = mock(DynamicMBeanProvider.class);
        final ObjectName objectName = new ObjectName(":type=Person");
        final DynamicMBean mBean = new DynamicMBeanPerson();
        final JMXRegistrationInfo info = new JMXRegistrationInfo(objectName, mBean);

        final ComponentAdapter componentAdapter = new JMXExposed(new InstanceAdapter(
                PersonMBean.class, person, new NullLifecycleStrategy(),
                                                                        new NullComponentMonitor()), (MBeanServer)mockMBeanServer.proxy(), new DynamicMBeanProvider[]{
                (DynamicMBeanProvider)mockProvider1.proxy(), (DynamicMBeanProvider)mockProvider2.proxy()});

        mockProvider1.expects(once()).method("provide").with(NULL, isA(ComponentAdapter.class)).will(returnValue(null));
        mockProvider2.expects(once()).method("provide").with(NULL, isA(ComponentAdapter.class)).will(returnValue(info));
        mockMBeanServer.expects(once()).method("registerMBean").with(same(mBean), eq(objectName));

        assertSame(person, componentAdapter.getComponentInstance(null));
    }

    public void testThrowsPicoInitializationExceptionIfMBeanIsAlreadyRegistered() throws NotCompliantMBeanException {
        final PersonMBean person = new DynamicMBeanPerson();
        final Exception exception = new InstanceAlreadyExistsException("JUnit");
        final ComponentAdapter componentAdapter = new JMXExposed(new InstanceAdapter(
                PersonMBean.class, person, new NullLifecycleStrategy(),
                                                                        new NullComponentMonitor()), (MBeanServer)mockMBeanServer.proxy());

        mockMBeanServer.expects(once()).method("registerMBean").with(same(person), isA(ObjectName.class)).will(
                throwException(exception));

        try {
            assertSame(person, componentAdapter.getComponentInstance(null));
            fail("PicoCompositionException expected");
        } catch (final PicoCompositionException e) {
            assertSame(exception, e.getCause());
        }
    }

    public void testThrowsPicoInitializationExceptionIfMBeanCannotBeRegistered() throws NotCompliantMBeanException {
        final PersonMBean person = new DynamicMBeanPerson();
        final Exception exception = new MBeanRegistrationException(new Exception(), "JUnit");
        final ComponentAdapter componentAdapter = new JMXExposed(new InstanceAdapter(
                PersonMBean.class, person, new NullLifecycleStrategy(),
                                                                        new NullComponentMonitor()), (MBeanServer)mockMBeanServer.proxy());

        mockMBeanServer.expects(once()).method("registerMBean").with(same(person), isA(ObjectName.class)).will(
                throwException(exception));

        try {
            assertSame(person, componentAdapter.getComponentInstance(null));
            fail("PicoCompositionException expected");
        } catch (final PicoCompositionException e) {
            assertSame(exception, e.getCause());
        }
    }

    public void testThrowsPicoInitializationExceptionIfMBeanNotCompliant() throws NotCompliantMBeanException {
        final PersonMBean person = new DynamicMBeanPerson();
        final Exception exception = new NotCompliantMBeanException("JUnit");
        final ComponentAdapter componentAdapter = new JMXExposed(new InstanceAdapter(
                PersonMBean.class, person, new NullLifecycleStrategy(),
                                                                        new NullComponentMonitor()), (MBeanServer)mockMBeanServer.proxy());

        mockMBeanServer.expects(once()).method("registerMBean").with(same(person), isA(ObjectName.class)).will(
                throwException(exception));

        try {
            assertSame(person, componentAdapter.getComponentInstance(null));
            fail("PicoCompositionException expected");
        } catch (final PicoCompositionException e) {
            assertSame(exception, e.getCause());
        }
    }

    public void testConstructorThrowsNPE() {
        try {
            new JMXExposed(
                    new InstanceAdapter(TestCase.class, this, new NullLifecycleStrategy(),
                                                                        new NullComponentMonitor()), null, new DynamicMBeanProvider[]{});
            fail("NullPointerException expected");
        } catch (final NullPointerException e) {
        }
        try {
            new JMXExposed(
                    new InstanceAdapter(TestCase.class, this, new NullLifecycleStrategy(),
                                                                        new NullComponentMonitor()), (MBeanServer)mockMBeanServer.proxy(), null);
            fail("NullPointerException expected");
        } catch (final NullPointerException e) {
        }
    }
}
