/*****************************************************************************
 * Copyright (C) NanoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the LICENSE.txt file.                                                     *
 *                                                                           *
 * Original code by Paul Hammant                                             *
 *****************************************************************************/

package org.nanocontainer;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.Properties;
import java.util.List;

import org.junit.Test;
import org.nanocontainer.DefaultNanoContainer;
import org.nanocontainer.NanoContainer;
import org.picocontainer.Characteristics;
import org.picocontainer.DefaultPicoContainer;
import org.picocontainer.MutablePicoContainer;
import org.picocontainer.PicoContainer;
import org.picocontainer.behaviors.Caching;
import org.picocontainer.behaviors.AdaptingBehavior;
import org.picocontainer.tck.AbstractPicoContainerTest;

/**
 * @author Paul Hammant
 */
public class DefaultNanoContainerTestCase extends AbstractPicoContainerTest {

    protected MutablePicoContainer createPicoContainer(PicoContainer parent) {
        return new DefaultNanoContainer(this.getClass().getClassLoader(), new DefaultPicoContainer(new Caching(), parent));
    }


    protected Properties[] getProperties() {
        return new Properties[] { Characteristics.NONE};
    }

    // TODO - go to a Nano TCK?
    @Test public void testNamedChildContainerIsAccessible()  {
        StringBuffer sb = new StringBuffer();
        final NanoContainer parent = (NanoContainer) createPicoContainer(null);
        parent.addComponent(sb);
        final NanoContainer child = (NanoContainer) parent.makeChildContainer("foo");
        child.addComponent(LifeCycleMonitoring.class,LifeCycleMonitoring.class);
        LifeCycleMonitoring o = (LifeCycleMonitoring) parent.getComponent((Object)("foo/*" + LifeCycleMonitoring.class.getName()));
        assertNotNull(o);
    }

    // TODO - go to a Nano TCK?
    @Test public void testNamedChildContainerIsAccessibleForStringKeys() {
        StringBuffer sb = new StringBuffer();
        final NanoContainer parent = (NanoContainer) createPicoContainer(null);
        parent.addComponent(sb);
        final MutablePicoContainer child = parent.makeChildContainer("foo");
        child.addComponent("lcm",LifeCycleMonitoring.class);
        Object o = parent.getComponent("foo/lcm");
        assertNotNull(o);
        assertTrue(sb.toString().indexOf("-instantiated") != -1);
    }

    // TODO - go to a Nano TCK?
    @Test public void testNamedChildContainerIsAccessibleForClassKeys() {
        StringBuffer sb = new StringBuffer();
        final NanoContainer parent = (NanoContainer) createPicoContainer(null);
        parent.addComponent(sb);
        final MutablePicoContainer child = parent.makeChildContainer("foo");
        child.addComponent(LifeCycleMonitoring.class,LifeCycleMonitoring.class);
        Object o = parent.getComponent("foo/*" + LifeCycleMonitoring.class.getName());
        assertNotNull(o);
        assertTrue(sb.toString().indexOf("-instantiated") != -1);
    }

    @Test public void testMakeRemoveChildContainer() {
        final NanoContainer parent = (NanoContainer) createPicoContainer(null);
        parent.addComponent("java.lang.String", "This is a test");
        MutablePicoContainer pico = parent.makeChildContainer();
        // Verify they are indeed wired together.
        assertNotNull(pico.getComponent("java.lang.String"));
        boolean result = parent.removeChildContainer(pico);
        assertTrue(result);
    }

    // test methods inherited. This container is otherwise fully compliant.


    @Test public void testAcceptImplementsBreadthFirstStrategy() {
        super.testAcceptImplementsBreadthFirstStrategy();
    }

    protected void addContainers(List expectedList) {
        expectedList.add(DefaultNanoContainer.class);
        expectedList.add(DefaultPicoContainer.class);
    }


    protected void addDefaultComponentFactories(List expectedList) {
        expectedList.add(Caching.class);
    }


}
