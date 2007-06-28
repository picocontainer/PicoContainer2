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

public class MultipleLoggerCommonsLoggingComponentMonitorTestCase extends AbstractComponentMonitorTestCase {

    String logPrefixName = String.class.getName();


    protected void setUp() throws Exception {
        super.setUp();
    }

    protected ComponentMonitor makeComponentMonitor() {
        return new CommonsLoggingComponentMonitor();
    }

    protected Method getMethod() throws NoSuchMethodException {
        return String.class.getMethod("toString");
    }

    protected Constructor getConstructor() {
        return String.class.getConstructors()[0];
    }

    protected String getLogPrefix() {
        return "[" + logPrefixName + "] ";
    }

    public void testShouldTraceNoComponent() throws IOException {
        logPrefixName = ComponentMonitor.class.getName();
        super.testShouldTraceNoComponent();
    }
}
