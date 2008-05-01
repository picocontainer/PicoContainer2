/*****************************************************************************
 * Copyright (C) PicoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the LICENSE.txt file.                                                     *
 *                                                                           *
 * Original code by                                                          *
 *****************************************************************************/
package org.picocontainer.injectors;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.junit.Assert.assertEquals;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.junit.Test;
import org.picocontainer.ComponentAdapter;
import org.picocontainer.Parameter;
import org.picocontainer.PicoCompositionException;
import org.picocontainer.lifecycle.ReflectionLifecycleStrategy;
import org.picocontainer.monitors.ConsoleComponentMonitor;

public class NamedFieldInjectionTestCase {

    @Test public void testFactoryMakesNamedInjector() {

        NamedFieldInjection injectionFactory = new NamedFieldInjection();

        ConsoleComponentMonitor cm = new ConsoleComponentMonitor();
        Properties props = new Properties();
        props.setProperty("injectionFieldNames", " aa pogo bb ");
        ComponentAdapter ca = injectionFactory.createComponentAdapter(cm, new ReflectionLifecycleStrategy(cm),
                props, Map.class, HashMap.class, Parameter.DEFAULT);
        
        assertTrue(ca instanceof NamedFieldInjector);

        NamedFieldInjector nfi = (NamedFieldInjector) ca;

        assertEquals(3, nfi.getInjectionFieldNames().size());
        assertEquals("pogo", nfi.getInjectionFieldNames().get(1));
    }

    @Test public void testPropertiesAreRight() {
        Properties props = NamedFieldInjection.injectionFieldNames("aa","pogo","bb");
        assertEquals("aa pogo bb", props.remove("injectionFieldNames"));
    }


}
