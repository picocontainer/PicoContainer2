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

import org.picocontainer.monitors.ConsoleComponentMonitor;
import org.picocontainer.lifecycle.ReflectionLifecycleStrategy;
import org.picocontainer.Parameter;
import org.picocontainer.ComponentAdapter;

import java.util.Map;
import java.util.HashMap;
import java.util.Properties;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.annotation.ElementType;

import junit.framework.TestCase;
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;

public class AnnotatedFieldInjectionTestCase extends TestCase {

    public void testFactoryMakesAnnotationInjector() {

        AnnotatedFieldInjection injectionFactory = new AnnotatedFieldInjection();

        ConsoleComponentMonitor cm = new ConsoleComponentMonitor();
        ComponentAdapter ca = injectionFactory.createComponentAdapter(cm, new ReflectionLifecycleStrategy(cm), new Properties(), Map.class, HashMap.class, Parameter.DEFAULT);
        
        XStream xs = new XStream();
        //xs.alias("CCM", ConsoleComponentMonitor.class);
        xs.registerConverter(new Converter() {
            public boolean canConvert(Class aClass) {
                return aClass.getName().equals("org.picocontainer.monitors.ConsoleComponentMonitor") ||
                       aClass.getName().equals("org.picocontainer.lifecycle.ReflectionLifecycleStrategy");

            }

            public void marshal(Object object,
                                HierarchicalStreamWriter hierarchicalStreamWriter,
                                MarshallingContext marshallingContext)
            {
            }

            public Object unmarshal(HierarchicalStreamReader hierarchicalStreamReader,
                                    UnmarshallingContext unmarshallingContext)
            {
                return null;
            }
        });

        String foo = xs.toXML(ca);

        assertEquals("<org.picocontainer.injectors.AnnotatedFieldInjector>\n" +
                     "  <injectionAnnotation>org.picocontainer.annotations.Inject</injectionAnnotation>\n" +
                     "  <lifecycleStrategy class=\"org.picocontainer.lifecycle.ReflectionLifecycleStrategy\"/>\n" +
                     "  <useNames>false</useNames>\n" +
                     "  <componentKey class=\"java-class\">java.util.Map</componentKey>\n" +
                     "  <componentImplementation>java.util.HashMap</componentImplementation>\n" +
                     "  <componentMonitor class=\"org.picocontainer.monitors.ConsoleComponentMonitor\"/>\n" +
                     "</org.picocontainer.injectors.AnnotatedFieldInjector>", foo);


    }


}
