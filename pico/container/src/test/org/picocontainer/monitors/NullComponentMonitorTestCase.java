/*****************************************************************************
 * Copyright (C) PicoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the LICENSE.txt file.                                                     *
 *                                                                           *
 * Original code by                                                          *
 *****************************************************************************/
package org.picocontainer.monitors;

import org.picocontainer.ComponentAdapter;
import org.picocontainer.MutablePicoContainer;
import org.picocontainer.PicoLifecycleException;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

import org.jmock.MockObjectTestCase;

public class NullComponentMonitorTestCase extends MockObjectTestCase {

    public void testItAll() throws NoSuchMethodException {

        NullComponentMonitor ncm = new NullComponentMonitor();
        ncm.instantiated(makePico(), makeCA(), makeConstructor(), "foo", new Object[0], 10);
        assertEquals(makeConstructor(), ncm.instantiating(makePico(), makeCA(), makeConstructor()));
        ncm.instantiationFailed(makePico(), makeCA(), makeConstructor(), new Exception());
        ncm.invocationFailed(makeConstructor(), "foo", new Exception());
        ncm.invoked(makePico(), makeCA(), makeMethod(), "foo", 10);
        ncm.invoking(makePico(), makeCA(), makeMethod(), "foo");
        try {
            ncm.lifecycleInvocationFailed(makePico(), makeCA(), makeMethod(), "foo", new RuntimeException());
        } catch (PicoLifecycleException e) {
            assertEquals(makeMethod(), e.getMethod());
            assertEquals("foo", e.getInstance());
            assertEquals("PicoLifecycleException: method 'public java.lang.String java.lang.String.toString()', instance 'foo, java.lang.RuntimeException", e.getMessage());
        }
        assertNull(ncm.noComponentFound(makePico(), String.class));

    }

    private MutablePicoContainer makePico() {
        return (MutablePicoContainer)mock(MutablePicoContainer.class).proxy();
    }

    private ComponentAdapter makeCA() {
        return (ComponentAdapter)mock(ComponentAdapter.class).proxy();
    }

    private Constructor makeConstructor() {
        return String.class.getConstructors()[0];
    }

    private Method makeMethod() throws NoSuchMethodException {
        return String.class.getMethod("toString");
    }


}
