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

import java.util.Set;

import javax.management.DynamicMBean;
import javax.management.MBeanRegistrationException;
import javax.management.MBeanServer;
import javax.management.MalformedObjectNameException;
import javax.management.ObjectInstance;
import javax.management.ObjectName;

import org.nanocontainer.remoting.jmx.testmodel.Person;
import org.picocontainer.ComponentAdapter;
import org.picocontainer.MutablePicoContainer;
import org.picocontainer.DefaultPicoContainer;
import org.picocontainer.monitors.NullComponentMonitor;
import org.picocontainer.lifecycle.NullLifecycleStrategy;
import org.picocontainer.adapters.InstanceAdapter;
import org.picocontainer.parameters.ConstantParameter;

import org.jmock.Mock;
import org.jmock.MockObjectTestCase;


/**
 * @author Michael Ward
 * @author J&ouml;rg Schaible
 * @version $Revision$
 */
public class JMXVisitorTestCase extends MockObjectTestCase {

    private MutablePicoContainer picoContainer;
    private Mock mBeanServerMock;
    private Mock dynamicMBeanProviderMock;
    private Mock dynamicMBeanMock;

    /**
     * @see junit.framework.TestCase#setUp()
     */
    protected void setUp() throws Exception {
        super.setUp();

        picoContainer = new DefaultPicoContainer();
        mBeanServerMock = mock(MBeanServer.class);
        dynamicMBeanProviderMock = mock(DynamicMBeanProvider.class);
        dynamicMBeanMock = mock(DynamicMBean.class);
    }

    private JMXVisitor createVisitor(final int providerCount) {
        final DynamicMBeanProvider[] providers = new DynamicMBeanProvider[providerCount];
        for (int i = 0; i < providers.length; i++) {
            providers[i] = (DynamicMBeanProvider)dynamicMBeanProviderMock.proxy();
        }
        return new JMXVisitor((MBeanServer)mBeanServerMock.proxy(), providers);
    }

    /**
     * Test visit with registration.
     * @throws MalformedObjectNameException
     */
    public void testVisitWithRegistration() throws MalformedObjectNameException {
        final JMXVisitor jmxVisitor = createVisitor(1);
        final ObjectName objectName = new ObjectName(":type=JUnit");
        final JMXRegistrationInfo registrationInfo = new JMXRegistrationInfo(objectName, (DynamicMBean)dynamicMBeanMock
                .proxy());
        final ObjectInstance objectInstance = new ObjectInstance(objectName, Person.class.getName());

        // parameter fixes coverage of visitParameter !!
        final ComponentAdapter componentAdapter = picoContainer.addComponent(
                Person.class, Person.class, new ConstantParameter("John Doe")).getComponentAdapter(Person.class);

        dynamicMBeanProviderMock.expects(once()).method("provide").with(same(picoContainer), same(componentAdapter))
                .will(returnValue(registrationInfo));
        mBeanServerMock.expects(once()).method("registerMBean").with(
                same(registrationInfo.getMBean()), same(registrationInfo.getObjectName())).will(
                returnValue(objectInstance));

        final Set set = (Set)jmxVisitor.traverse(picoContainer);
        assertEquals(1, set.size());
        assertSame(objectInstance, set.iterator().next());
    }

    /**
     * Test the trial of multiple providers and ensure, that the first provider delivering a JMXRegistrationInfo is
     * used.
     * @throws MalformedObjectNameException
     */
    public void testVisitWithMultipleProviders() throws MalformedObjectNameException {
        final JMXVisitor jmxVisitor = createVisitor(2);
        final JMXRegistrationInfo registrationInfo = new JMXRegistrationInfo(
                new ObjectName(":type=JUnit"), (DynamicMBean)dynamicMBeanMock.proxy());

        final ComponentAdapter componentAdapter1 = picoContainer.addComponent(this).getComponentAdapter(this.getClass());
        final ComponentAdapter componentAdapter2 = picoContainer.addComponent(Person.class).getComponentAdapter(Person.class);

        dynamicMBeanProviderMock.expects(once()).method("provide").with(same(picoContainer), same(componentAdapter1))
                .will(returnValue(null));
        dynamicMBeanProviderMock.expects(once()).method("provide").with(same(picoContainer), same(componentAdapter1))
                .will(returnValue(null));
        dynamicMBeanProviderMock.expects(once()).method("provide").with(same(picoContainer), same(componentAdapter2))
                .will(returnValue(registrationInfo));
        mBeanServerMock.expects(once()).method("registerMBean").with(
                same(registrationInfo.getMBean()), same(registrationInfo.getObjectName()));

        jmxVisitor.traverse(picoContainer);
    }

    /**
     * Test the traversal of the visitor.
     */
    public void testTraversal() {
        final JMXVisitor jmxVisitor = createVisitor(1);
        final MutablePicoContainer child = new DefaultPicoContainer();
        picoContainer.addChildContainer(child);

        final ComponentAdapter componentAdapter = child.addComponent(Person.class).getComponentAdapter(Person.class);

        dynamicMBeanProviderMock.expects(once()).method("provide").with(same(child), same(componentAdapter)).will(
                returnValue(null));

        jmxVisitor.traverse(picoContainer);
    }

    /**
     * Test ctor.
     */
    public void testInvalidConstructorArguments() {
        try {
            new JMXVisitor(null, new DynamicMBeanProvider[]{(DynamicMBeanProvider)dynamicMBeanProviderMock.proxy()});
            fail("NullPointerException expected");
        } catch (final NullPointerException e) {
            // fine
        }
        try {
            new JMXVisitor((MBeanServer)mBeanServerMock.proxy(), null);
            fail("NullPointerException expected");
        } catch (final NullPointerException e) {
            // fine
        }
        try {
            new JMXVisitor((MBeanServer)mBeanServerMock.proxy(), new DynamicMBeanProvider[]{});
            fail("IllegalArgumentException expected");
        } catch (final IllegalArgumentException e) {
            // fine
        }
    }

    /**
     * Test illegal call of visitComponentAdapter
     */
    public void testIllegalVisit() {
        final JMXVisitor jmxVisitor = createVisitor(1);
        final ComponentAdapter componentAdapter = new InstanceAdapter(this, this, NullLifecycleStrategy.getInstance(),
                                                                        NullComponentMonitor.getInstance());
        try {
            jmxVisitor.traverse(componentAdapter);
            fail("JMXRegistrationException expected");
        } catch (final JMXRegistrationException e) {
            // fine
        }
    }

    /**
     * Test failing registration.
     * @throws MalformedObjectNameException
     */
    public void testFailingMBeanRegistration() throws MalformedObjectNameException {
        final JMXVisitor jmxVisitor = createVisitor(1);
        final JMXRegistrationInfo registrationInfo = new JMXRegistrationInfo(
                new ObjectName(":type=JUnit"), (DynamicMBean)dynamicMBeanMock.proxy());
        final Exception exception = new MBeanRegistrationException(null, "JUnit");

        // parameter fixes coverage of visitParameter !!
        final ComponentAdapter componentAdapter = picoContainer.addComponent(Person.class).getComponentAdapter(Person.class);

        dynamicMBeanProviderMock.expects(once()).method("provide").with(same(picoContainer), same(componentAdapter))
                .will(returnValue(registrationInfo));
        mBeanServerMock.expects(once()).method("registerMBean").with(
                same(registrationInfo.getMBean()), same(registrationInfo.getObjectName())).will(
                throwException(exception));

        try {
            jmxVisitor.traverse(picoContainer);
            fail("JMXRegistrationException expected");
        } catch (final JMXRegistrationException e) {
            assertSame(exception, e.getCause());
        }
    }
}
