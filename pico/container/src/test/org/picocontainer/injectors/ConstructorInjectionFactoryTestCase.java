/*****************************************************************************
 * Copyright (C) PicoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the LICENSE.txt file.                                                     *
 *                                                                           *
 * Idea by Rachel Davies, Original code by Aslak Hellesoy and Paul Hammant   *
 *****************************************************************************/
package org.picocontainer.injectors;

import org.picocontainer.injectors.ConstructorInjectionFactory;
import org.picocontainer.monitors.NullComponentMonitor;
import org.picocontainer.DefaultPicoContainer;
import org.picocontainer.ComponentFactory;
import org.picocontainer.injectors.ConstructorInjector;
import org.picocontainer.tck.AbstractComponentAdapterFactoryTestCase;
import org.picocontainer.tck.AbstractComponentAdapterTestCase.RecordingLifecycleStrategy;
import org.picocontainer.testmodel.NullLifecycle;
import org.picocontainer.testmodel.RecordingLifecycle;
import org.picocontainer.testmodel.RecordingLifecycle.One;

/**
 * @author Mauro Talevi
 * @version $Revision:  $
 */
public class ConstructorInjectionFactoryTestCase extends AbstractComponentAdapterFactoryTestCase {
    protected void setUp() throws Exception {
        picoContainer = new DefaultPicoContainer(createComponentFactory());
    }

    protected ComponentFactory createComponentFactory() {
        return new ConstructorInjectionFactory();
    }

    public void testCustomLifecycleCanBeInjected() throws NoSuchMethodException {
        RecordingLifecycleStrategy strategy = new RecordingLifecycleStrategy(new StringBuffer());
        ConstructorInjectionFactory componentFactory =
            new ConstructorInjectionFactory();
        ConstructorInjector cica =  (ConstructorInjector)
        componentFactory.createComponentAdapter(new NullComponentMonitor(), strategy, null, NullLifecycle.class, NullLifecycle.class);
        One one = new RecordingLifecycle.One(new StringBuffer());
        cica.start(one);
        cica.stop(one);        
        cica.dispose(one);
        assertEquals("<start<stop<dispose", strategy.recording());
    }    

}