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

import static org.picocontainer.monitors.ComponentMonitorHelper.methodToString;
import static org.picocontainer.monitors.ComponentMonitorHelper.parmsToString;
import static org.picocontainer.monitors.ComponentMonitorHelper.ctorToString;
import static org.picocontainer.monitors.ComponentMonitorHelper.format;

import java.io.StringWriter;
import java.io.Writer;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import junit.framework.TestCase;

import org.picocontainer.ComponentMonitor;
import org.picocontainer.PicoContainer;
import org.picocontainer.PicoCompositionException;
import org.picocontainer.PicoLifecycleException;
import org.picocontainer.adapters.AbstractAdapter;
import org.picocontainer.containers.TransientPicoContainer;

/**
 * @author Aslak Helles&oslash;y
 * @author Mauro Talevi
 */
public class WriterComponentMonitorTestCase extends TestCase {
    private Writer out;
    private ComponentMonitor componentMonitor;
    private static final String NL = System.getProperty("line.separator");
    private Constructor constructor;
    private Method method;

    protected void setUp() throws Exception {
        out = new StringWriter();
        constructor = getClass().getConstructor((Class[])null);
        method = getClass().getDeclaredMethod("setUp", (Class[])null);
        componentMonitor = new WriterComponentMonitor(out);
    }

    public void testShouldTraceInstantiating() {
        componentMonitor.instantiating(null, null, constructor);
        assertEquals(format(ComponentMonitorHelper.INSTANTIATING, ctorToString(constructor)) +NL,  out.toString());
    }

    public void testShouldTraceInstantiatedWithInjected() {
        Object[] injected = new Object[0];
        Object instantiated = new Object();
        componentMonitor.instantiated(null, null, constructor, instantiated, injected, 543);
        assertEquals(format(ComponentMonitorHelper.INSTANTIATED,
                                                   ctorToString(constructor),
                                                   (long)543,
                                                   instantiated.getClass().getName(), parmsToString(injected)) +NL,  out.toString());
    }


    public void testShouldTraceInstantiationFailed() {
        componentMonitor.instantiationFailed(null, null, constructor, new RuntimeException("doh"));
        assertEquals(format(ComponentMonitorHelper.INSTANTIATION_FAILED,
                                                   ctorToString(constructor), "doh") +NL,  out.toString());
    }

    public void testShouldTraceInvoking() {
        componentMonitor.invoking(null, null, method, this);
        assertEquals(format(ComponentMonitorHelper.INVOKING,
                                                   methodToString(method), this) +NL,  out.toString());
    }

    public void testShouldTraceInvoked() {
        componentMonitor.invoked(null, null, method, this, 543);
        assertEquals(format(ComponentMonitorHelper.INVOKED,
                                                   methodToString(method), this,
                                                   (long)543) +NL,  out.toString());
    }

    public void testShouldTraceInvocatiationFailed() {
        componentMonitor.invocationFailed(method, this, new RuntimeException("doh"));
        assertEquals(format(ComponentMonitorHelper.INVOCATION_FAILED,
                                                   methodToString(method), this, "doh") +NL,  out.toString());
    }

    public void testShouldTraceLifecycleInvocationFailed() {
        try {
            componentMonitor.lifecycleInvocationFailed(new TransientPicoContainer(),
                                                       new AbstractAdapter(Map.class, HashMap.class) {
                                                           public Object getComponentInstance(PicoContainer container)
                                                               throws PicoCompositionException {
                                                               return "x";
                                                           }

                                                           public void verify(PicoContainer container)
                                                               throws PicoCompositionException{
                                                           }

                                                           public String getDescriptor() {
                                                               return null;
                                                           }
                                                       },
                                                       method,
                                                       "fooooo",
                                                       new RuntimeException("doh"));
            fail("should have barfed");
        } catch (PicoLifecycleException e) {
            //expected
        }
        assertEquals(format(ComponentMonitorHelper.LIFECYCLE_INVOCATION_FAILED,
                                                   methodToString(method), "fooooo", "doh") + NL,
                     out.toString());
    }

    public void testNoComponent() {
        
        componentMonitor.noComponentFound(new TransientPicoContainer(), "foo");
        assertEquals(format(ComponentMonitorHelper.NO_COMPONENT,
                                                   "foo") +NL,  out.toString());
    }


}
