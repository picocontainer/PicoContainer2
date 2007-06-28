/*****************************************************************************
 * Copyright (C) PicoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the LICENSE.txt file.                                                     *
 *                                                                           *
 * Original code by                                                          *
 *****************************************************************************/
package org.picocontainer.tck;

import junit.framework.TestCase;
import org.picocontainer.ComponentAdapter;
import org.picocontainer.PicoCompositionException;
import org.picocontainer.Characterizations;
import org.picocontainer.lifecycle.NullLifecycleStrategy;
import org.picocontainer.monitors.NullComponentMonitor;
import org.picocontainer.ComponentFactory;
import org.picocontainer.DefaultPicoContainer;
import org.picocontainer.testmodel.SimpleTouchable;
import org.picocontainer.testmodel.Touchable;

/**
 * @author Aslak Helles&oslash;y
 * @version $Revision$
 */
public abstract class AbstractComponentAdapterFactoryTestCase extends TestCase {

    protected DefaultPicoContainer picoContainer;

    protected abstract ComponentFactory createComponentFactory();

    protected void setUp() throws Exception {
        picoContainer = new DefaultPicoContainer();
    }

    public void testEquals() throws PicoCompositionException {
        ComponentAdapter componentAdapter = createComponentFactory().createComponentAdapter(new NullComponentMonitor(), new NullLifecycleStrategy(), Characterizations.CDI, Touchable.class, SimpleTouchable.class);

        assertEquals(componentAdapter, componentAdapter);
        assertTrue(!componentAdapter.equals("blah"));
    }

    public void testRegisterComponent() throws PicoCompositionException {
        ComponentAdapter componentAdapter =
                createComponentFactory().createComponentAdapter(new NullComponentMonitor(), new NullLifecycleStrategy(), Characterizations.CDI, Touchable.class, SimpleTouchable.class);

        picoContainer.addAdapter(componentAdapter);

        assertTrue(picoContainer.getComponentAdapters().contains(componentAdapter));
    }

    public void testUnregisterComponent() throws PicoCompositionException {
        ComponentAdapter componentAdapter =
                createComponentFactory().createComponentAdapter(new NullComponentMonitor(), new NullLifecycleStrategy(), Characterizations.CDI, Touchable.class, SimpleTouchable.class);

        picoContainer.addAdapter(componentAdapter);
        picoContainer.removeComponent(Touchable.class);

        assertFalse(picoContainer.getComponentAdapters().contains(componentAdapter));
    }
}
