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

import javax.management.MBeanServer;
import javax.management.NotCompliantMBeanException;
import javax.management.ObjectName;

import org.nanocontainer.remoting.jmx.testmodel.DynamicMBeanPerson;
import org.nanocontainer.remoting.jmx.testmodel.PersonMBean;
import org.picocontainer.ComponentAdapter;
import org.picocontainer.Characterizations;
import org.picocontainer.ComponentCharacteristics;
import org.picocontainer.injectors.ConstructorInjectionFactory;
import org.picocontainer.lifecycle.NullLifecycleStrategy;
import org.picocontainer.monitors.NullComponentMonitor;

import org.jmock.Mock;
import org.jmock.MockObjectTestCase;


/**
 * @author J&ouml;rg Schaible
 */
public class JMXExposingBehaviorFactoryTestCase extends MockObjectTestCase {

    private Mock mockMBeanServer;

    protected void setUp() throws Exception {
        super.setUp();
        mockMBeanServer = mock(MBeanServer.class);
    }

    public void testWillRegisterByDefaultComponentsThatAreMBeans() throws NotCompliantMBeanException {
        final JMXExposingBehaviorFactory componentFactory = new JMXExposingBehaviorFactory(
                (MBeanServer)mockMBeanServer.proxy());
        componentFactory.forThis(new ConstructorInjectionFactory());

        mockMBeanServer.expects(once()).method("registerMBean").with(
                isA(DynamicMBeanPerson.class), isA(ObjectName.class));

        final ComponentAdapter componentAdapter = componentFactory.createComponentAdapter(
                new NullComponentMonitor(), new NullLifecycleStrategy(), Characterizations.CDI, PersonMBean.class, DynamicMBeanPerson.class, null);
        assertNotNull(componentAdapter);
        assertNotNull(componentAdapter.getComponentInstance(null));
    }

    public void testWillRegisterByDefaultComponentsThatAreMBeansUnlessNOJMX() throws NotCompliantMBeanException {
        final JMXExposingBehaviorFactory componentFactory = new JMXExposingBehaviorFactory(
                (MBeanServer)mockMBeanServer.proxy());
        componentFactory.forThis(new ConstructorInjectionFactory());

        final ComponentCharacteristics rc = new ComponentCharacteristics() {
        };

        Characterizations.NOJMX.mergeInto(rc);

        final ComponentAdapter componentAdapter = componentFactory.createComponentAdapter(
                new NullComponentMonitor(), new NullLifecycleStrategy(), rc, PersonMBean.class, DynamicMBeanPerson.class, null);
        assertNotNull(componentAdapter);
        assertNotNull(componentAdapter.getComponentInstance(null));
    }

    public void testConstructorThrowsNPE() {
        try {
            new JMXExposingBehaviorFactory(
                    null, new DynamicMBeanProvider[]{});
            fail("NullPointerException expected");
        } catch (final NullPointerException e) {
        }
        try {
            new JMXExposingBehaviorFactory(
                    (MBeanServer)mockMBeanServer.proxy(), null);
            fail("NullPointerException expected");
        } catch (final NullPointerException e) {
        }
    }
}
