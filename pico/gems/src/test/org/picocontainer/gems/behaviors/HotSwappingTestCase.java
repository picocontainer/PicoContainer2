/*****************************************************************************
 * Copyright (c) PicoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the LICENSE.txt file.                                                     *
 *                                                                           *
 *****************************************************************************/

package org.picocontainer.gems.behaviors;

import org.picocontainer.MutablePicoContainer;
import org.picocontainer.ComponentFactory;
import org.picocontainer.DefaultPicoContainer;
import org.picocontainer.lifecycle.NullLifecycleStrategy;
import org.picocontainer.monitors.NullComponentMonitor;
import org.picocontainer.gems.behaviors.HotSwapping;
import org.picocontainer.gems.behaviors.HotSwappable;
import org.picocontainer.injectors.AdaptingInjection;
import org.picocontainer.injectors.ConstructorInjector;
import org.picocontainer.injectors.ConstructorInjection;
import org.picocontainer.tck.AbstractComponentFactoryTestCase;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.HashMap;


public final class HotSwappingTestCase extends AbstractComponentFactoryTestCase {
    private final ComponentFactory implementationHidingComponentFactory = new HotSwapping().wrap(new AdaptingInjection());

    // START SNIPPET: man
    public static interface Man {
        Woman getWoman();

        void kiss();

        boolean wasKissed();
    }

    // END SNIPPET: man

    // START SNIPPET: woman
    public static interface Woman {
        Man getMan();
    }

    // END SNIPPET: woman

    public static class Wife implements Woman {
        public final Man partner;

        public Wife(Man partner) {
            this.partner = partner;
        }

        public Man getMan() {
            return partner;
        }
    }


    public void testHotSwappingNaturaelyCaches() {
        DefaultPicoContainer pico = new DefaultPicoContainer(new HotSwapping().wrap(new ConstructorInjection()));
        pico.addComponent(Map.class, HashMap.class);
        Map firstMap = pico.getComponent(Map.class);
        Map secondMap = pico.getComponent(Map.class);
        assertSame(firstMap, secondMap);

    }


    public void testSwappingViaSwappableInterface() {
        MutablePicoContainer pico = new DefaultPicoContainer();
        ConstructorInjector constructorInjector = new ConstructorInjector("l", ArrayList.class, null, new NullComponentMonitor(), new NullLifecycleStrategy(), false);
        HotSwappable hsca = (HotSwappable) pico.addAdapter(new HotSwappable(constructorInjector)).getComponentAdapter(constructorInjector.getComponentKey());
        List l = (List)pico.getComponent("l");
        l.add("Hello");
        final ArrayList newList = new ArrayList();

        ArrayList oldSubject = (ArrayList) hsca.swapRealInstance(newList);
        assertEquals("Hello", oldSubject.get(0));
        assertTrue(l.isEmpty());
        l.add("World");
        assertEquals("World", l.get(0));
    }


    protected ComponentFactory createComponentFactory() {
        return implementationHidingComponentFactory;
    }

}
