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
        
        DefaultPicoContainer recorded = new DefaultPicoContainer(new Caching());
        recorded.addComponent("fruit", "apple");
        recorded.addComponent("int", 239);
        recorded.addComponent("thing",
                ThingThatTakesParamsInConstructor.class,
                ComponentParameter.DEFAULT,
                ComponentParameter.DEFAULT);

        DefaultPicoContainer replayed = new DefaultPicoContainer(new Storing(), recorded);
        assertEquals("apple", replayed.getComponent("fruit"));
        assertEquals("apple239", ((ThingThatTakesParamsInConstructor) replayed.getComponent("thing")).getValue());

        // test that we can replay once more
        DefaultPicoContainer anotherReplayed = new DefaultPicoContainer(new Storing(), recorded);
        assertEquals("apple", anotherReplayed.getComponent("fruit"));
        assertEquals("apple239", ((ThingThatTakesParamsInConstructor) anotherReplayed.getComponent("thing")).getValue());
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
