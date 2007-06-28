package org.picocontainer.behaviors;

import org.picocontainer.monitors.NullComponentMonitor;
import org.picocontainer.lifecycle.NullLifecycleStrategy;
import org.picocontainer.ComponentCharacteristics;
import org.picocontainer.Characterizations;
import org.picocontainer.ComponentAdapter;
import org.picocontainer.annotations.Cache;
import org.picocontainer.injectors.SetterInjector;
import org.picocontainer.containers.EmptyPicoContainer;

import java.util.Map;
import java.util.HashMap;

import junit.framework.TestCase;
import com.thoughtworks.xstream.XStream;

public class AdaptiveBehaviorFactoryTestCase extends TestCase {

    public void testCachingBehaviorCanBeAddedByCharacteristics() {
        AdaptiveBehaviorFactory abf = new AdaptiveBehaviorFactory();
        ComponentCharacteristics cc = new ComponentCharacteristics();
        Characterizations.CACHE.mergeInto(cc);
        ComponentAdapter ca = abf.createComponentAdapter(new NullComponentMonitor(), new NullLifecycleStrategy(), cc, Map.class, HashMap.class);
        assertTrue(ca instanceof CachingBehavior);
        Map map = (Map)ca.getComponentInstance(new EmptyPicoContainer());
        assertNotNull(map);
        Map map2 = (Map)ca.getComponentInstance(new EmptyPicoContainer());
        assertSame(map, map2);
        assertFalse(cc.hasUnProcessedEntries());
    }

    public void testCachingBehaviorCanBeAddedByAnnotation() {
        AdaptiveBehaviorFactory abf = new AdaptiveBehaviorFactory();
        ComponentCharacteristics cc = new ComponentCharacteristics();
        ComponentAdapter ca = abf.createComponentAdapter(new NullComponentMonitor(), new NullLifecycleStrategy(), cc, Map.class, MyHashMap.class);
        assertTrue(ca instanceof CachingBehavior);
        Map map = (Map)ca.getComponentInstance(new EmptyPicoContainer());
        assertNotNull(map);
        Map map2 = (Map)ca.getComponentInstance(new EmptyPicoContainer());
        assertSame(map, map2);
        assertFalse(cc.hasUnProcessedEntries());
    }

    @Cache
    public static class MyHashMap extends HashMap {
        public MyHashMap() {
        }
    }

    public void testImplementationHidingBehaviorCanBeAddedByCharacteristics() {
        AdaptiveBehaviorFactory abf = new AdaptiveBehaviorFactory();
        ComponentCharacteristics cc = new ComponentCharacteristics();
        Characterizations.HIDE.mergeInto(cc);
        ComponentAdapter ca = abf.createComponentAdapter(new NullComponentMonitor(), new NullLifecycleStrategy(), cc, Map.class, HashMap.class);
        assertTrue(ca instanceof ImplementationHidingBehavior);
        Map map = (Map)ca.getComponentInstance(new EmptyPicoContainer());
        assertNotNull(map);
        assertTrue(!(map instanceof HashMap));

        assertFalse(cc.hasUnProcessedEntries());


    }

    public void testSetterInjectionCanBeTriggereedMeaningAdaptiveInjectorIsUsed() {
        AdaptiveBehaviorFactory abf = new AdaptiveBehaviorFactory();
        ComponentCharacteristics cc = new ComponentCharacteristics();
        Characterizations.SDI.mergeInto(cc);
        ComponentAdapter ca = abf.createComponentAdapter(new NullComponentMonitor(), new NullLifecycleStrategy(), cc, Map.class, HashMap.class);
        assertTrue(ca instanceof SetterInjector);
        Map map = (Map)ca.getComponentInstance(new EmptyPicoContainer());
        assertNotNull(map);
        assertFalse(cc.hasUnProcessedEntries());


    }

    public void testCachingAndImplHidingAndThreadSafetySetupCorrectly() {
        AdaptiveBehaviorFactory abf = new AdaptiveBehaviorFactory();
        ComponentCharacteristics cc = new ComponentCharacteristics();
        Characterizations.CACHE.mergeInto(cc);
        Characterizations.HIDE.mergeInto(cc);
        Characterizations.THREAD_SAFE.mergeInto(cc);
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

        assertFalse(cc.hasUnProcessedEntries());


    }

    public void testCachingAndImplHidingAndThreadSafetySetupCorrectlyForExtraCaching() {
        CachingBehaviorFactory cbf = new CachingBehaviorFactory();
        AdaptiveBehaviorFactory abf = new AdaptiveBehaviorFactory();
        cbf.forThis(abf);
        ComponentCharacteristics cc = new ComponentCharacteristics();
        Characterizations.CACHE.mergeInto(cc);
        Characterizations.HIDE.mergeInto(cc);
        Characterizations.THREAD_SAFE.mergeInto(cc);
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


}
