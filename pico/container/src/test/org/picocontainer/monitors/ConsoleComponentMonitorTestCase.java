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

import java.io.PrintStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

import junit.framework.TestCase;

import org.picocontainer.ComponentMonitor;

/**
 * @author Aslak Helles&oslash;y
 * @author Mauro Talevi
 */
public class ConsoleComponentMonitorTestCase extends TestCase {
    private ComponentMonitor componentMonitor;
    private Constructor constructor;
    private Method method;

    protected void setUp() throws Exception {
        PrintStream out = System.out;
        constructor = getClass().getConstructor((Class[])null);
        method = getClass().getDeclaredMethod("setUp", (Class[])null);
        componentMonitor = new ConsoleComponentMonitor(out);
    }

    public void testShouldTraceInstantiating() {
        componentMonitor.instantiating(null, null, constructor);
    }

    public void testShouldTraceInstantiatedWithInjected() {
        componentMonitor.instantiated(null, null, constructor, new Object(), new Object[0], 543);
    }

    public void testShouldTraceInstantiationFailed() {
        componentMonitor.instantiationFailed(null, null, constructor, new RuntimeException("doh"));
    }

    public void testShouldTraceInvoking() {
        componentMonitor.invoking(null, null, method, this);
    }

    public void testShouldTraceInvoked() {
        componentMonitor.invoked(null, null, method, this, 543);
    }

    public void testShouldTraceInvocatiationFailed() {
        componentMonitor.invocationFailed(method, this, new RuntimeException("doh"));
    }

}
