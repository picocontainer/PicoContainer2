/*****************************************************************************
 * Copyright (C) PicoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the LICENSE.txt file.                                                     *
 *                                                                           *
 * Original code by                                                          *
 *****************************************************************************/
package org.picocontainer.behaviors;

import org.picocontainer.monitors.NullComponentMonitor;
import org.picocontainer.lifecycle.NullLifecycleStrategy;
import org.picocontainer.Characterizations;
import org.picocontainer.ComponentAdapter;
import org.picocontainer.annotations.Cache;
import org.picocontainer.injectors.SetterInjector;
import org.picocontainer.containers.EmptyPicoContainer;

import java.util.Map;
import java.util.HashMap;
import java.util.Properties;
import java.util.Enumeration;

import junit.framework.TestCase;
import com.thoughtworks.xstream.XStream;

public class AdaptiveBehaviorFactoryTestCase extends TestCase {

    public void testCachingBehaviorCanBeAddedByCharacteristics() {
        AdaptiveBehaviorFactory abf = new AdaptiveBehaviorFactory();
        Properties cc = new Properties();
        mergeInto(Characterizations.CACHE,cc);
        ComponentAdapter ca = abf.createComponentAdapter(new NullComponentMonitor(), new NullLifecycleStrategy(), cc, Map.class, HashMap.class);
        assertTrue(ca instanceof CachingBehavior);
        Map map = (Map)ca.getComponentInstance(new EmptyPicoContainer());
        assertNotNull(map);
        Map map2 = (Map)ca.getComponentInstance(new EmptyPicoContainer());
        assertSame(map, map2);
        assertEquals(0, cc.size());
    }

    public void testCachingBehaviorCanBeAddedByAnnotation() {
        AdaptiveBehaviorFactory abf = new AdaptiveBehaviorFactory();
        Properties cc = new Properties();
        ComponentAdapter ca = abf.createComponentAdapter(new NullComponentMonitor(), new NullLifecycleStrategy(), cc, Map.class, MyHashMap.class);
        assertTrue(ca instanceof CachingBehavior);
        Map map = (Map)ca.getComponentInstance(new EmptyPicoContainer());
        assertNotNull(map);
        Map map2 = (Map)ca.getComponentInstance(new EmptyPicoContainer());
        assertSame(map, map2);
        assertEquals(0, cc.size());
    }

    @Cache
    public static class MyHashMap extends HashMap {
        public MyHashMap() {
        }
    }

    public void testImplementationHidingBehaviorCanBeAddedByCharacteristics() {
        AdaptiveBehaviorFactory abf = new AdaptiveBehaviorFactory();
        Properties cc = new Properties();
        mergeInto(Characterizations.HIDE,cc);
        ComponentAdapter ca = abf.createComponentAdapter(new NullComponentMonitor(), new NullLifecycleStrategy(), cc, Map.class, HashMap.class);
        assertTrue(ca instanceof ImplementationHidingBehavior);
        Map map = (Map)ca.getComponentInstance(new EmptyPicoContainer());
        assertNotNull(map);
        assertTrue(!(map instanceof HashMap));

        assertEquals(0, cc.size());


    }

    public void testSetterInjectionCanBeTriggereedMeaningAdaptiveInjectorIsUsed() {
        AdaptiveBehaviorFactory abf = new AdaptiveBehaviorFactory();
        Properties cc = new Properties();
        mergeInto(Characterizations.SDI,cc);
        ComponentAdapter ca = abf.createComponentAdapter(new NullComponentMonitor(), new NullLifecycleStrategy(), cc, Map.class, HashMap.class);
        assertTrue(ca instanceof SetterInjector);
        Map map = (Map)ca.getComponentInstance(new EmptyPicoContainer());
        assertNotNull(map);
        assertEquals(0, cc.size());


    }

    public void testCachingAndImplHidingAndThreadSafetySetupCorrectly() {
        AdaptiveBehaviorFactory abf = new AdaptiveBehaviorFactory();
        Properties cc = new Properties();
        mergeInto(Characterizations.CACHE,cc);
        mergeInto(Characterizations.HIDE,cc);
        mergeInto(Characterizations.THREAD_SAFE,cc);
        ComponentAdapter ca = abf.createComponentAdapter(new NullComponentMonitor(), new NullLifecycleStrategy(), cc, Map.class, HashMap.class);
        assertTrue(ca instanceof CachingBehavior);
        Map map = (Map)ca.getComponentInstance(new EmptyPicoContainer());
        assertNotNull(map);
        assertTrue(!(map instanceof HashMap));

        XStream xs = new XStream();
        String foo = xs.toXML(ca);

        int ih = foo.indexOf(ImplementationHidingBehavior.class.getName());
        int sb = foo.indexOf(SynchronizedBehavior.class.getName());

        // check right nesting order
        assertTrue(ih>0);
        assertTrue(sb>0);
        assertTrue(sb>ih);

        assertEquals(0, cc.size());


    }

    public void testCachingAndImplHidingAndThreadSafetySetupCorrectlyForExtraCaching() {
        CachingBehaviorFactory cbf = new CachingBehaviorFactory();
        AdaptiveBehaviorFactory abf = new AdaptiveBehaviorFactory();
        cbf.forThis(abf);
        Properties cc = new Properties();
        mergeInto(Characterizations.CACHE,cc);
        mergeInto(Characterizations.HIDE,cc);
        mergeInto(Characterizations.THREAD_SAFE,cc);
        ComponentAdapter ca = cbf.createComponentAdapter(new NullComponentMonitor(), new NullLifecycleStrategy(), cc, Map.class, HashMap.class);
        assertTrue(ca instanceof CachingBehavior);
        Map map = (Map)ca.getComponentInstance(new EmptyPicoContainer());
        assertNotNull(map);
        assertTrue(!(map instanceof HashMap));

        XStream xs = new XStream();
        String foo = xs.toXML(ca);

        assertTrue(foo.indexOf("<" + CachingBehavior.class.getName() + ">", 0)  > -1);  // xml does start with CB
        assertFalse(foo.indexOf("<" + CachingBehavior.class.getName() + ">", 1)  > -1); // but only contains it once.

    }

    public void mergeInto(Properties p, Properties into) {
        Enumeration e = p.propertyNames();
        while (e.hasMoreElements()) {
            String s = (String)e.nextElement();
            into.setProperty(s, p.getProperty(s));
        }

    }


}
