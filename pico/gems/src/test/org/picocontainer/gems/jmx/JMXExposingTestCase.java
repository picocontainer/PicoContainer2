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

import javax.management.MBeanServer;
import javax.management.NotCompliantMBeanException;
import javax.management.ObjectName;

import org.picocontainer.gems.jmx.testmodel.DynamicMBeanPerson;
import org.picocontainer.gems.jmx.testmodel.PersonMBean;
import org.picocontainer.ComponentAdapter;
import org.picocontainer.Characteristics;
import org.picocontainer.injectors.ConstructorInjection;
import org.picocontainer.lifecycle.NullLifecycleStrategy;
import org.picocontainer.monitors.NullComponentMonitor;

import java.util.Properties;

import org.jmock.Mock;
import org.jmock.MockObjectTestCase;


/**
 * @author J&ouml;rg Schaible
 */
public class JMXExposingTestCase extends MockObjectTestCase {

    private Mock mockMBeanServer;

    protected void setUp() throws Exception {
        super.setUp();
        mockMBeanServer = mock(MBeanServer.class);
    }

    public void testWillRegisterByDefaultComponentsThatAreMBeans() throws NotCompliantMBeanException {
        final JMXExposing componentFactory = new JMXExposing(
                (MBeanServer)mockMBeanServer.proxy());
        componentFactory.wrap(new ConstructorInjection());

        mockMBeanServer.expects(once()).method("registerMBean").with(
                isA(DynamicMBeanPerson.class), isA(ObjectName.class));

        final ComponentAdapter componentAdapter = componentFactory.createComponentAdapter(
                new NullComponentMonitor(), new NullLifecycleStrategy(), Characteristics.CDI, PersonMBean.class, DynamicMBeanPerson.class, null);
        assertNotNull(componentAdapter);
        assertNotNull(componentAdapter.getComponentInstance(null));
    }

    public void testWillRegisterByDefaultComponentsThatAreMBeansUnlessNOJMX() throws NotCompliantMBeanException {
        final JMXExposing componentFactory = new JMXExposing(
                (MBeanServer)mockMBeanServer.proxy());
        componentFactory.wrap(new ConstructorInjection());

        final Properties rc = new Properties(Characteristics.NO_JMX);

        final ComponentAdapter componentAdapter = componentFactory.createComponentAdapter(
                new NullComponentMonitor(), new NullLifecycleStrategy(), rc, PersonMBean.class, DynamicMBeanPerson.class, null);
        assertNotNull(componentAdapter);
        assertNotNull(componentAdapter.getComponentInstance(null));
    }

    public void testConstructorThrowsNPE() {
        try {
            new JMXExposing(
                    null, new DynamicMBeanProvider[]{});
            fail("NullPointerException expected");
        } catch (final NullPointerException e) {
        }
        try {
            new JMXExposing(
                    (MBeanServer)mockMBeanServer.proxy(), null);
            fail("NullPointerException expected");
        } catch (final NullPointerException e) {
        }
    }
}
