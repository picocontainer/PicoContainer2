/*****************************************************************************
 * Copyright (C) PicoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the LICENSE.txt file.                                                     *
 *                                                                           *
 * Original code by                                                          *
 *****************************************************************************/
package org.picocontainer.doc.tutorial.blocks;

import junit.framework.TestCase;

import org.picocontainer.MutablePicoContainer;
import org.picocontainer.Parameter;
import org.picocontainer.DefaultPicoContainer;
import org.picocontainer.monitors.NullComponentMonitor;
import org.picocontainer.lifecycle.NullLifecycleStrategy;
import org.picocontainer.behaviors.Caching;
import org.picocontainer.behaviors.Cached;
import org.picocontainer.injectors.ConstructorInjector;
import org.picocontainer.adapters.InstanceAdapter;
import org.picocontainer.injectors.SetterInjector;
import org.picocontainer.injectors.SetterInjection;
import org.picocontainer.behaviors.Synchronized;
import org.picocontainer.behaviors.Synchronizing;
import org.picocontainer.doc.introduction.Apple;
import org.picocontainer.doc.introduction.Juicer;
import org.picocontainer.doc.introduction.Peeler;


/**
 * Test case for the snippets used in "Component Adapters and Factories"
 * 
 * @author J&ouml;rg Schaible
 */
public class BuildingBlocksTestCase extends TestCase {
    public void testRegisterConvenient() {
        // START SNIPPET: register-convenient
        MutablePicoContainer picoContainer = new DefaultPicoContainer();
        picoContainer.addComponent(Juicer.class);
        picoContainer.addComponent("My Peeler", Peeler.class);
        picoContainer.addComponent(new Apple());
        // END SNIPPET: register-convenient
        // START SNIPPET: register-direct
        picoContainer.addAdapter(new InstanceAdapter("Another Apple", new Apple(), new NullLifecycleStrategy(),
                                                                        new NullComponentMonitor()));
        // END SNIPPET: register-direct
    }

    public void testRegisterEquivalentConvenient() {
        MutablePicoContainer picoContainer = new DefaultPicoContainer();
        // START SNIPPET: register-equivalent-convenient
        picoContainer.addComponent(Juicer.class);
        // END SNIPPET: register-equivalent-convenient
    }

    public void testRegisterEquivalentAtLength() {
        MutablePicoContainer picoContainer = new DefaultPicoContainer();
        // START SNIPPET: register-equivalent-at-length
        picoContainer.addAdapter(
                new Cached(
                        new ConstructorInjector(Juicer.class, Juicer.class, null, new NullComponentMonitor(), new NullLifecycleStrategy(), false)));
        // END SNIPPET: register-equivalent-at-length
    }

    public void testRegisterDifferentComponentFactory() {

        // START SNIPPET: register-different-componentFactory
        MutablePicoContainer picoContainer = new DefaultPicoContainer(
                new Synchronizing().wrap(new Caching().wrap(new SetterInjection())));
        // END SNIPPET: register-different-componentFactory
    }

    public void testRegisterEquivalentAtLength2() {
        MutablePicoContainer picoContainer = new DefaultPicoContainer();
        // START SNIPPET: register-equivalent-at-length2
        picoContainer.addAdapter(
                new Synchronized(
                        new Cached(
                                new SetterInjector(
                                        JuicerBean.class, JuicerBean.class, (Parameter[])null, new NullComponentMonitor(), new NullLifecycleStrategy(),
                                        "set", false))));
        // END SNIPPET: register-equivalent-at-length2
    }
}
