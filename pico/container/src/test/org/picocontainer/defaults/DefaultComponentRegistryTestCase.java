/*****************************************************************************
 * Copyright (c) PicoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the LICENSE.txt file.                                                     *
 *                                                                           *
 * Idea by Rachel Davies, Original code by Stacy Curl                        *
 *****************************************************************************/

package org.picocontainer.defaults;

import junit.framework.TestCase;
import org.picocontainer.ComponentAdapter;
import org.picocontainer.PicoCompositionException;
import org.picocontainer.DefaultPicoContainer;
import org.picocontainer.injectors.ConstructorInjector;
import org.picocontainer.injectors.AbstractInjector;
import org.picocontainer.testmodel.AlternativeTouchable;
import org.picocontainer.testmodel.SimpleTouchable;
import org.picocontainer.testmodel.Touchable;

public class DefaultComponentRegistryTestCase extends TestCase {
    private DefaultPicoContainer picoContainer;

    protected void setUp() throws Exception {
        picoContainer = new DefaultPicoContainer();
    }

    public void testRegisterComponent() throws PicoCompositionException {
        ComponentAdapter componentAdapter = createComponentAdapter();
        picoContainer.addAdapter(componentAdapter);
        assertTrue(picoContainer.getComponentAdapters().contains(componentAdapter));
    }

    public void testUnregisterComponent() throws PicoCompositionException {
        ComponentAdapter componentAdapter = createComponentAdapter();
        picoContainer.addAdapter(componentAdapter);
        picoContainer.removeComponent(Touchable.class);
        assertFalse(picoContainer.getComponentAdapters().contains(componentAdapter));
    }

    public void testCannotInstantiateAnUnregisteredComponent() throws
                                                               PicoCompositionException
    {
        ComponentAdapter componentAdapter = createComponentAdapter();
        picoContainer.addAdapter(componentAdapter);
        picoContainer.getComponents();
        picoContainer.removeComponent(Touchable.class);

        assertTrue(picoContainer.getComponents().isEmpty());
    }

    public void testCanInstantiateReplacedComponent() throws PicoCompositionException {
        ComponentAdapter componentAdapter = createComponentAdapter();
        picoContainer.addAdapter(componentAdapter);
        picoContainer.getComponents();
        picoContainer.removeComponent(Touchable.class);

        picoContainer.addComponent(Touchable.class, AlternativeTouchable.class);

        assertEquals("Container should container 1 addComponent",
                1, picoContainer.getComponents().size());
    }

    public void testUnregisterAfterInstantiateComponents() throws PicoCompositionException {
        ComponentAdapter componentAdapter = createComponentAdapter();
        picoContainer.addAdapter(componentAdapter);
        picoContainer.getComponents();
        picoContainer.removeComponent(Touchable.class);
        assertNull(picoContainer.getComponent(Touchable.class));
    }

    public void testReplacedInstantiatedComponentHasCorrectClass() throws
                                                                   PicoCompositionException
    {
        ComponentAdapter componentAdapter = createComponentAdapter();
        picoContainer.addAdapter(componentAdapter);
        picoContainer.getComponents();
        picoContainer.removeComponent(Touchable.class);

        picoContainer.addComponent(Touchable.class, AlternativeTouchable.class);
        Object component = picoContainer.getComponents().iterator().next();

        assertEquals(AlternativeTouchable.class, component.getClass());
    }

    private ComponentAdapter createComponentAdapter() throws PicoCompositionException {
        return new ConstructorInjector(Touchable.class, SimpleTouchable.class);
    }
}
