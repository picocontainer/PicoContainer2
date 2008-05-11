package org.nanocontainer.reflection;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Reader;
import java.io.StringReader;
import java.lang.reflect.InvocationTargetException;

import org.junit.Test;
import org.nanocontainer.integrationkit.ContainerPopulator;
import org.nanocontainer.script.xml.XMLContainerBuilder;
import org.nanocontainer.testmodel.FredImpl;
import org.nanocontainer.testmodel.ThingThatTakesParamsInConstructor;
import org.nanocontainer.testmodel.Wilma;
import org.nanocontainer.testmodel.WilmaImpl;
import org.picocontainer.DefaultPicoContainer;
import org.picocontainer.MutablePicoContainer;
import org.picocontainer.behaviors.Caching;
import org.picocontainer.behaviors.Storing;
import org.picocontainer.parameters.ComponentParameter;

/**
 * Test case to prove that the DefaultContainerRecorder can be replaced by use of Storing behaviours.
 * 
 * @author Konstantin Pribluda
 * @author Aslak Helles&oslash;y
 * @author Mauro Talevi
 */
public class StoringContainerTestCase {
    
    @Test public void testInvocationsCanBeRecordedAndReplayedOnADifferentContainerInstance() throws Exception {

        // This test case is not testing Storing. Its just testing that a Caching parent does so.

        DefaultPicoContainer recorded = new DefaultPicoContainer(new Caching());
        recorded.addComponent("fruit", "apple");
        recorded.addComponent("int", 239);
        recorded.addComponent("thing",
                ThingThatTakesParamsInConstructor.class,
                ComponentParameter.DEFAULT,
                ComponentParameter.DEFAULT);

        Storing storing1 = new Storing();
        DefaultPicoContainer replayed = new DefaultPicoContainer(storing1, recorded);
        assertEquals("store should be empty", 0, storing1.getCacheSize());
        Object a1 = replayed.getComponent("fruit");
        assertEquals("store should still be empty: its not used", 0, storing1.getCacheSize());
        ThingThatTakesParamsInConstructor a2 = (ThingThatTakesParamsInConstructor) replayed.getComponent("thing");
        assertEquals("apple", a1);
        assertEquals("apple239", a2.getValue());

        // test that we can replay once more
        Storing storing2 = new Storing();
        DefaultPicoContainer anotherReplayed = new DefaultPicoContainer(storing2, recorded);
        assertEquals("store should be empty", 0, storing2.getCacheSize());
        Object b1 = anotherReplayed.getComponent("fruit");
        assertEquals("store should still be empty: its not used", 0, storing2.getCacheSize());
        ThingThatTakesParamsInConstructor b2 = (ThingThatTakesParamsInConstructor) anotherReplayed.getComponent("thing");
        assertEquals("apple", b1);
        assertEquals("apple239", b2.getValue());

        assertSame("cache of 'recording' parent container should be caching", a1,b1); 
        assertSame("cache of 'recording' parent container should be caching", a2,b2);
    }

    @Test public void testRecorderWorksAfterSerialization() throws IOException, ClassNotFoundException, IllegalAccessException, InvocationTargetException {
        DefaultPicoContainer recorded = new DefaultPicoContainer(new Caching());
        recorded.addComponent("fruit", "apple");
        DefaultPicoContainer replayed = new DefaultPicoContainer(new Storing(), recorded);
        DefaultPicoContainer serializedReplayed = (DefaultPicoContainer) serializeAndDeserialize(replayed);
        assertEquals("apple", serializedReplayed.getComponent("fruit"));
    }

    private Object serializeAndDeserialize(Object o) throws IOException, ClassNotFoundException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(baos);

        oos.writeObject(o);
        ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(baos.toByteArray()));

        return ois.readObject();
    }


    @Test public void testXMLRecorderHierarchy() {
        MutablePicoContainer recorded = new DefaultPicoContainer(new Caching());
        DefaultPicoContainer parentReplayed = new DefaultPicoContainer(new Storing(), recorded);
        StringReader parentResource = new StringReader("" 
                + "<container>" 
                + "  <component-implementation key='wilma' class='"+WilmaImpl.class.getName()+"'/>"
                + "</container>" 
                );

        populateXMLContainer(parentReplayed, parentResource);        
        assertNull(parentReplayed.getComponent("fred"));
        assertNotNull(parentReplayed.getComponent("wilma"));

        DefaultPicoContainer childReplayed = new DefaultPicoContainer(new Storing(), parentReplayed);
        StringReader childResource = new StringReader("" 
                + "<container>" 
                + "  <component-implementation key='fred' class='"+FredImpl.class.getName()+"'>"
                + "     <parameter key='wilma'/>"  
               + "  </component-implementation>"
                + "</container>" 
                );
        populateXMLContainer(childReplayed, childResource);
        assertNotNull(childReplayed.getComponent("fred"));
        assertNotNull(childReplayed.getComponent("wilma"));
        FredImpl fred = (FredImpl)childReplayed.getComponent("fred");
        Wilma wilma = (Wilma)childReplayed.getComponent("wilma");
        assertSame(wilma, fred.wilma());
    }
                       
    private void populateXMLContainer(MutablePicoContainer container, Reader resource) {
        ContainerPopulator populator = new XMLContainerBuilder(resource, Thread.currentThread().getContextClassLoader());
        populator.populateContainer(container);
    }       
}
