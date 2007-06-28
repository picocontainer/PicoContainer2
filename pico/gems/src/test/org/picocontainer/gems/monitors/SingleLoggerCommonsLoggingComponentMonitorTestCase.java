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

import org.picocontainer.ComponentMonitor;

public class SingleLoggerCommonsLoggingComponentMonitorTestCase extends AbstractComponentMonitorTestCase {

    protected void setUp() throws Exception {
        super.setUp();
    }

    protected ComponentMonitor makeComponentMonitor() {
        return new CommonsLoggingComponentMonitor(CommonsLoggingComponentMonitor.class);
    }

    protected Constructor getConstructor() throws NoSuchMethodException {
        return getClass().getConstructor((Class[])null);
    }

    protected Method getMethod() throws NoSuchMethodException {
        return getClass().getDeclaredMethod("setUp", (Class[])null);
    }

    protected String getLogPrefix() {
        return "[" + CommonsLoggingComponentMonitor.class.getName() + "] ";
    }

}
