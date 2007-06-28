/*****************************************************************************
 * Copyright (C) PicoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the LICENSE.txt file.                                                     *
 *****************************************************************************/
package org.picocontainer.gems.monitors;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.io.IOException;

import org.picocontainer.ComponentMonitor;

/**
 * @author Paul Hammant
 * @author Mauro Talevi
 * @version $Revision: 2024 $
 */
public class SingleLoggerLog4JComponentMonitorTestCase extends AbstractComponentMonitorTestCase {

    protected void setUp() throws Exception {
        super.setUp();
    }

    protected ComponentMonitor makeComponentMonitor() {
        return new Log4JComponentMonitor(Log4JComponentMonitor.class);
    }

    protected Constructor getConstructor() throws NoSuchMethodException {
        return getClass().getConstructor((Class[])null);
    }

    protected Method getMethod() throws NoSuchMethodException {
        return getClass().getDeclaredMethod("setUp", (Class[])null);
    }

    protected String getLogPrefix() {
        return "[" + Log4JComponentMonitor.class.getName() + "] ";
    }

    public void testShouldTraceNoComponent() throws IOException {
        super.testShouldTraceNoComponent();    
    }
}
