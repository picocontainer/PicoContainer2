/*****************************************************************************
 * Copyright (C) PicoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the LICENSE.txt file.                                                     *
 *                                                                           *
 * Original code by                                                          *
 *****************************************************************************/
package org.picocontainer.behaviors;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import junit.framework.TestCase;

import org.picocontainer.ComponentAdapter;
import org.picocontainer.PicoCompositionException;
import org.picocontainer.lifecycle.NullLifecycleStrategy;
import org.picocontainer.monitors.NullComponentMonitor;
import org.picocontainer.behaviors.HiddenImplementation;
import org.picocontainer.injectors.ConstructorInjector;

public class HiddenImplementationTestCase extends TestCase {

    public void testMultipleInterfacesCanBeHidden() {
        ComponentAdapter ca = new ConstructorInjector(new Class[]{ActionListener.class, MouseListener.class}, Footle.class, null, new NullComponentMonitor(), new NullLifecycleStrategy(), false);
        HiddenImplementation ihca = new HiddenImplementation(ca);
        Object comp = ihca.getComponentInstance(null);
        assertNotNull(comp);
        assertTrue(comp instanceof ActionListener);
        assertTrue(comp instanceof MouseListener);
    }    

    public void testNonInterfaceInArrayCantBeHidden() {
        ComponentAdapter ca = new ConstructorInjector(new Class[]{String.class}, Footle.class, null, new NullComponentMonitor(), new NullLifecycleStrategy(), false);
        HiddenImplementation ihca = new HiddenImplementation(ca);
        try {
            ihca.getComponentInstance(null);
            fail("PicoCompositionException expected");
        } catch (PicoCompositionException e) {
            // expected        
        }
    }


    
    public class Footle implements ActionListener, MouseListener {
        public void actionPerformed(ActionEvent e) {
        }

        public void mouseClicked(MouseEvent e) {
        }

        public void mouseEntered(MouseEvent e) {
        }

        public void mouseExited(MouseEvent e) {
        }

        public void mousePressed(MouseEvent e) {
        }

        public void mouseReleased(MouseEvent e) {
        }

    }

}
