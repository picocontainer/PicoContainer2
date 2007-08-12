/*****************************************************************************
 * Copyright (C) PicoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the LICENSE.txt file.                                                     *
 *****************************************************************************/
package org.picocontainer.gems.monitors;

import static org.picocontainer.monitors.ComponentMonitorHelper.methodToString;
import static org.picocontainer.monitors.ComponentMonitorHelper.ctorToString;
import static org.picocontainer.monitors.ComponentMonitorHelper.parmsToString;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import junit.framework.TestCase;

import org.picocontainer.ComponentMonitor;
import org.picocontainer.monitors.ComponentMonitorHelper;

/**
 * @author Paul Hammant
 * @author Aslak Helles&oslash;y
 * @author Mauro Talevi
 * @author Juze Peleteiro
 */
public abstract class ComponentMonitorHelperTestCase extends TestCase {
    private ComponentMonitor componentMonitor;
    private Constructor constructor;
    private Method method;
    
    protected void setUp() throws Exception {
        constructor = getConstructor();
        method = getMethod();
        componentMonitor = makeComponentMonitor();
    }

    protected abstract ComponentMonitor makeComponentMonitor();
    
    protected abstract Constructor getConstructor() throws NoSuchMethodException;

    protected abstract Method getMethod() throws NoSuchMethodException;
    
    protected abstract String getLogPrefix();

    protected void tearDown() throws Exception {
    	ForTestSakeAppender.CONTENT = "";
    }

    public void testShouldTraceInstantiating() throws IOException {
        componentMonitor.instantiating(null, null, constructor);
        assertFileContent(getLogPrefix() + ComponentMonitorHelper.format(ComponentMonitorHelper.INSTANTIATING, ctorToString(constructor)));
    }

    public void testShouldTraceInstantiatedWithInjected() throws IOException {
        Object[] injected = new Object[0];
        Object instantiated = new Object();
        componentMonitor.instantiated(null, null, constructor, instantiated, injected, 543);
        String s = ComponentMonitorHelper.format(ComponentMonitorHelper.INSTANTIATED, ctorToString(constructor), (long) 543, instantiated.getClass().getName(), parmsToString(injected));
        assertFileContent(getLogPrefix() + s);
    }


    public void testShouldTraceInstantiationFailed() throws IOException {
        componentMonitor.instantiationFailed(null, null, constructor, new RuntimeException("doh"));
        assertFileContent(getLogPrefix() + ComponentMonitorHelper.format(ComponentMonitorHelper.INSTANTIATION_FAILED, ctorToString(constructor), "doh"));
    }

    public void testShouldTraceInvoking() throws IOException {
        componentMonitor.invoking(null, null, method, this);
        assertFileContent(getLogPrefix() + ComponentMonitorHelper.format(ComponentMonitorHelper.INVOKING, methodToString(method), this));
    }

    public void testShouldTraceInvoked() throws IOException {
        componentMonitor.invoked(null, null, method, this, 543);
        assertFileContent(getLogPrefix() + ComponentMonitorHelper.format(ComponentMonitorHelper.INVOKED, methodToString(method), this, (long) 543));
    }

    public void testShouldTraceInvocatiationFailed() throws IOException {
        componentMonitor.invocationFailed(method, this, new RuntimeException("doh"));
        assertFileContent(getLogPrefix() + ComponentMonitorHelper.format(ComponentMonitorHelper.INVOCATION_FAILED, methodToString(method), this, "doh"));
    }

    public void testShouldTraceNoComponent() throws IOException {
        componentMonitor.noComponentFound(null, "doh");
        assertFileContent(getLogPrefix() + ComponentMonitorHelper.format(ComponentMonitorHelper.NO_COMPONENT, "doh"));
    }


    protected void assertFileContent(String line) throws IOException{
        List lines = toLines( new StringReader( ForTestSakeAppender.CONTENT ) );
        String s = lines.toString();
        assertTrue("Line '" + line + "' not found", s.indexOf(line) > 0);
    }
    
    protected List toLines(Reader resource) throws IOException {
        BufferedReader br = new BufferedReader(resource);
        List lines = new ArrayList();
        String line = br.readLine();
        while ( line != null) {
            lines.add(line);
            line = br.readLine();
        }
        return lines;
    } 

}
