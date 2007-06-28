/*****************************************************************************
 * Copyright (C) PicoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the LICENSE.txt file.                                                     *
 *****************************************************************************/
package org.picocontainer.gems.monitors;

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
import org.picocontainer.monitors.AbstractComponentMonitor;

/**
 * @author Paul Hammant
 * @author Aslak Helles&oslash;y
 * @author Mauro Talevi
 * @author Juze Peleteiro
 * @version $Revision: 2024 $
 */
public abstract class AbstractComponentMonitorTestCase extends TestCase {
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
        assertFileContent(getLogPrefix() + AbstractComponentMonitor.format(AbstractComponentMonitor.INSTANTIATING, AbstractComponentMonitor.toString(constructor)));
    }

    public void testShouldTraceInstantiatedWithInjected() throws IOException {
        Object[] injected = new Object[0];
        Object instantiated = new Object();
        componentMonitor.instantiated(null, null, constructor, instantiated, injected, 543);
        String s = AbstractComponentMonitor.format(AbstractComponentMonitor.INSTANTIATED2, AbstractComponentMonitor.toString(constructor), (long) 543, instantiated.getClass().getName(), AbstractComponentMonitor.toString(injected));
        assertFileContent(getLogPrefix() + s);
    }


    public void testShouldTraceInstantiationFailed() throws IOException {
        componentMonitor.instantiationFailed(null, null, constructor, new RuntimeException("doh"));
        assertFileContent(getLogPrefix() + AbstractComponentMonitor.format(AbstractComponentMonitor.INSTANTIATION_FAILED, AbstractComponentMonitor.toString(constructor), "doh"));
    }

    public void testShouldTraceInvoking() throws IOException {
        componentMonitor.invoking(null, null, method, this);
        assertFileContent(getLogPrefix() + AbstractComponentMonitor.format(AbstractComponentMonitor.INVOKING, AbstractComponentMonitor.toString(method), this));
    }

    public void testShouldTraceInvoked() throws IOException {
        componentMonitor.invoked(null, null, method, this, 543);
        assertFileContent(getLogPrefix() + AbstractComponentMonitor.format(AbstractComponentMonitor.INVOKED, AbstractComponentMonitor.toString(method), this, (long) 543));
    }

    public void testShouldTraceInvocatiationFailed() throws IOException {
        componentMonitor.invocationFailed(method, this, new RuntimeException("doh"));
        assertFileContent(getLogPrefix() + AbstractComponentMonitor.format(AbstractComponentMonitor.INVOCATION_FAILED, AbstractComponentMonitor.toString(method), this, "doh"));
    }

    public void testShouldTraceNoComponent() throws IOException {
        componentMonitor.noComponent(null, "doh");
        assertFileContent(getLogPrefix() + AbstractComponentMonitor.format(AbstractComponentMonitor.NO_COMPONENT, "doh"));
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
