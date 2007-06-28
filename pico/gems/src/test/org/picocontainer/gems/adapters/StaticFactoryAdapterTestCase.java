/*****************************************************************************
 * Copyright (C) PicoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the LICENSE.txt file.                                                     *
 *                                                                           *
 * Original code by Leo Simmons & Joerg Schaible                             *
 *****************************************************************************/
package org.picocontainer.gems.adapters;

import junit.framework.TestCase;

import org.picocontainer.ComponentAdapter;
import org.picocontainer.DefaultPicoContainer;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;


/**
 * @author J&ouml;rg Schaible
 */
public class StaticFactoryAdapterTestCase extends TestCase {

    public void testStaticFactoryInAction() {
        ComponentAdapter componentAdapter = new StaticFactoryAdapter(Registry.class, new StaticFactory() {
            public Object get() {
                try {
                    return LocateRegistry.getRegistry();
                } catch (RemoteException e) {
                    return null;
                }
            }
        });

        DefaultPicoContainer pico = new DefaultPicoContainer();
        pico.addAdapter(componentAdapter).getComponentAdapter(componentAdapter.getComponentKey()).verify(pico);
        Registry registry = pico.getComponent(Registry.class);
        assertNotNull(registry);
    }
}
