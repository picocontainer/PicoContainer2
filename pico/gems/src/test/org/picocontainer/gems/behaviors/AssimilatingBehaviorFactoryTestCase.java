/*****************************************************************************
 * Copyright (C) NanoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the LICENSE.txt file.                                                     *
 *                                                                           *
 * Original code by Joerg Schaibe                                            *
 *****************************************************************************/

package org.picocontainer.gems.behaviors;

import org.picocontainer.ComponentFactory;
import org.picocontainer.DefaultPicoContainer;
import org.picocontainer.PicoCompositionException;
import org.picocontainer.gems.behaviors.AssimilatingBehaviorFactory;
import org.picocontainer.injectors.ConstructorInjectionFactory;
import org.picocontainer.tck.AbstractComponentAdapterFactoryTestCase;
import org.picocontainer.testmodel.AlternativeTouchable;
import org.picocontainer.testmodel.CompatibleTouchable;
import org.picocontainer.testmodel.SimpleTouchable;
import org.picocontainer.testmodel.Touchable;

import java.util.List;


/**
 * @author J&ouml;rg Schaible
 */
public class AssimilatingBehaviorFactoryTestCase extends AbstractComponentAdapterFactoryTestCase {

    /**
     * @see org.picocontainer.tck.AbstractComponentAdapterFactoryTestCase#createComponentFactory()
     */
    protected ComponentFactory createComponentFactory() {
        return new AssimilatingBehaviorFactory(Touchable.class).forThis(new ConstructorInjectionFactory());
    }

    /**
     * Test automatic assimilation of registered components.
     */
    public void testAutomaticAssimilation() {
        picoContainer = new DefaultPicoContainer(createComponentFactory());
        picoContainer.addComponent(SimpleTouchable.class);
        picoContainer.addComponent(AlternativeTouchable.class);
        picoContainer.addComponent(CompatibleTouchable.class);
        final List list = picoContainer.getComponents(Touchable.class);
        assertEquals(3, list.size());
    }

    /**
     * Test automatic assimilation of registered components.
     */
    public void testOnlyOneTouchableComponentKeyPossible() {
        picoContainer = new DefaultPicoContainer(createComponentFactory());
        picoContainer.addComponent(Touchable.class, SimpleTouchable.class);
        try {
            picoContainer.addComponent(CompatibleTouchable.class);
            fail("DuplicateComponentKeyRegistrationException expected");
        } catch (final PicoCompositionException e) {
            assertTrue(e.getMessage().startsWith("Duplicate"));
            // fine
        }
    }

    /**
     * Test automatic assimilation of registered components.
     */
    public void testMultipleAssimilatedComponentsWithUserDefinedKeys() {
        picoContainer = new DefaultPicoContainer(createComponentFactory());
        picoContainer.addComponent(Touchable.class, SimpleTouchable.class);
        picoContainer.addComponent("1", CompatibleTouchable.class);
        picoContainer.addComponent("2", CompatibleTouchable.class);
        picoContainer.addComponent("3", CompatibleTouchable.class);
        final List list = picoContainer.getComponents(Touchable.class);
        assertEquals(4, list.size());
    }
}
