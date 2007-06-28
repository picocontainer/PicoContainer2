/*****************************************************************************
 * Copyright (c) PicoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the LICENSE.txt file.                                                     *
 *                                                                           *
 * Idea by Rachel Davies, Original code by Stacy Curl                        *
 *****************************************************************************/

package org.picocontainer.injectors;

import org.picocontainer.ComponentAdapter;
import org.picocontainer.Parameter;
import org.picocontainer.PicoCompositionException;
import org.picocontainer.Characterizations;
import org.picocontainer.lifecycle.NullLifecycleStrategy;
import org.picocontainer.lifecycle.ReflectionLifecycleStrategy;
import org.picocontainer.monitors.NullComponentMonitor;
import org.picocontainer.monitors.ConsoleComponentMonitor;
import org.picocontainer.ComponentFactory;
import org.picocontainer.DefaultPicoContainer;
import org.picocontainer.ComponentCharacteristics;
import org.picocontainer.injectors.AdaptiveInjectionFactory;
import org.picocontainer.injectors.FieldAnnotationInjectorTestCase;
import org.picocontainer.injectors.MethodAnnotationInjectorTestCase;
import org.picocontainer.injectors.MethodAnnotationInjector;
import org.picocontainer.injectors.FieldAnnotationInjector;
import org.picocontainer.injectors.ConstructorInjector;
import org.picocontainer.tck.AbstractComponentAdapterFactoryTestCase;
import org.picocontainer.testmodel.SimpleTouchable;
import org.picocontainer.testmodel.Touchable;

import java.util.Map;
import java.util.HashMap;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;

public class AdaptiveInjectionFactoryTestCase extends AbstractComponentAdapterFactoryTestCase {

    XStream xs;

    protected void setUp() throws Exception {
        super.setUp();
        xs = new XStream();
        xs.alias("RLS",ReflectionLifecycleStrategy.class);
        xs.alias("CCM",ConsoleComponentMonitor.class);
        xs.alias("Method-Injection", MethodAnnotationInjector.class);
        xs.alias("Field-Injection", FieldAnnotationInjector.class);
        xs.alias("Constructor-Injection", ConstructorInjector.class);
        //xs.alias("CCM", ConsoleComponentMonitor.class);
        xs.registerConverter(new Converter() {
            public boolean canConvert(Class aClass) {
                return aClass.getName().equals("org.picocontainer.monitors.ConsoleComponentMonitor") ||
                       aClass.getName().equals("org.picocontainer.lifecycle.ReflectionLifecycleStrategy");

            }

            public void marshal(Object object,
                                HierarchicalStreamWriter hierarchicalStreamWriter,
                                MarshallingContext marshallingContext) {
            }

            public Object unmarshal(HierarchicalStreamReader hierarchicalStreamReader,
                                    UnmarshallingContext unmarshallingContext) {
                return null;
            }
        });

    }

    protected ComponentFactory createComponentFactory() {
        return new AdaptiveInjectionFactory();
    }

    public void testInstantiateComponentWithNoDependencies() throws PicoCompositionException
    {
        ComponentAdapter componentAdapter =
                createComponentFactory().createComponentAdapter(new NullComponentMonitor(), new NullLifecycleStrategy(), Characterizations.CDI, Touchable.class, SimpleTouchable.class, (Parameter[])null);

        Object comp = componentAdapter.getComponentInstance(new DefaultPicoContainer());
        assertNotNull(comp);
        assertTrue(comp instanceof SimpleTouchable);
    }

    public void testSingleUsecanBeInstantiatedByDefaultComponentAdapter() {
        ComponentAdapter componentAdapter = createComponentFactory().createComponentAdapter(new NullComponentMonitor(), new NullLifecycleStrategy(), Characterizations.CDI, "o", Object.class, (Parameter[])null);
        Object component = componentAdapter.getComponentInstance(new DefaultPicoContainer());
        assertNotNull(component);
    }


    public void testFactoryMakesConstructorInjector() {

        ComponentFactory cf = createComponentFactory();

        ConsoleComponentMonitor cm = new ConsoleComponentMonitor();
        ComponentAdapter ca = cf.createComponentAdapter(cm, new ReflectionLifecycleStrategy(cm), new ComponentCharacteristics(),
                                                        Map.class, HashMap.class, Parameter.DEFAULT);
        
        String foo = xs.toXML(ca).replace("\"","");

        assertEquals("<Constructor-Injection>\n" +
                     "  <lifecycleStrategy class=RLS/>\n" +
                     "  <componentKey class=java-class>java.util.Map</componentKey>\n" +
                     "  <componentImplementation>java.util.HashMap</componentImplementation>\n" +
                     "  <componentMonitor class=CCM/>\n" +
                     "</Constructor-Injection>", foo);


    }

    public void testFactoryMakesFieldAnnotationInjector() {

        ComponentFactory cf = createComponentFactory();

        ConsoleComponentMonitor cm = new ConsoleComponentMonitor();
        ComponentAdapter ca = cf.createComponentAdapter(cm, new ReflectionLifecycleStrategy(cm), new ComponentCharacteristics(),
                                                        FieldAnnotationInjectorTestCase.Helicopter.class, FieldAnnotationInjectorTestCase.Helicopter.class, Parameter.DEFAULT);

        String foo = xs.toXML(ca).replace("\"","");

        assertEquals("<Field-Injection>\n" +
                     "  <lifecycleStrategy class=RLS/>\n" +
                     "  <componentKey class=java-class>org.picocontainer.injectors.FieldAnnotationInjectorTestCase$Helicopter</componentKey>\n" +
                     "  <componentImplementation>org.picocontainer.injectors.FieldAnnotationInjectorTestCase$Helicopter</componentImplementation>\n" +
                     "  <componentMonitor class=CCM/>\n" +
                     "</Field-Injection>", foo);


    }

    public void testFactoryMakesMethodAnnotationInjector() {

        ComponentFactory cf = createComponentFactory();

        ConsoleComponentMonitor cm = new ConsoleComponentMonitor();
        ComponentAdapter ca = cf.createComponentAdapter(cm, new ReflectionLifecycleStrategy(cm), new ComponentCharacteristics(),
                                                        MethodAnnotationInjectorTestCase.AnnotatedBurp.class, MethodAnnotationInjectorTestCase.AnnotatedBurp.class, Parameter.DEFAULT);

        String foo = xs.toXML(ca).replace("\"","");

        assertEquals("<Method-Injection>\n" +
                     "  <lifecycleStrategy class=RLS/>\n" +
                     "  <componentKey class=java-class>org.picocontainer.injectors.MethodAnnotationInjectorTestCase$AnnotatedBurp</componentKey>\n" +
                     "  <componentImplementation>org.picocontainer.injectors.MethodAnnotationInjectorTestCase$AnnotatedBurp</componentImplementation>\n" +
                     "  <componentMonitor class=CCM/>\n" +
                     "</Method-Injection>", foo);


    }


}
