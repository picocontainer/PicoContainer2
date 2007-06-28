package org.nanocontainer.reflection;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Reader;
import java.io.StringReader;
import java.lang.reflect.InvocationTargetException;

import junit.framework.TestCase;

import org.nanocontainer.integrationkit.ContainerPopulator;
import org.nanocontainer.integrationkit.ContainerRecorder;
import org.nanocontainer.script.xml.XMLContainerBuilder;
import org.nanocontainer.testmodel.FredImpl;
import org.nanocontainer.testmodel.ThingThatTakesParamsInConstructor;
import org.nanocontainer.testmodel.Wilma;
import org.nanocontainer.testmodel.WilmaImpl;
import org.nanocontainer.DefaultNanoContainer;
import org.picocontainer.MutablePicoContainer;
import org.picocontainer.DefaultPicoContainer;
import org.picocontainer.injectors.AdaptiveInjectionFactory;
import org.picocontainer.behaviors.CachingBehaviorFactory;
import org.picocontainer.parameters.ComponentParameter;

/**
 * @author Konstantin Pribluda ( konstantin.pribluda(at)infodesire.com )
 * @author Aslak Helles&oslash;y
 */
public class DefaultContainerRecorderTestCase extends TestCase {
    public void testInvocationsCanBeRecordedAndReplayedOnADifferentContainerInstance() throws Exception {
        ContainerRecorder recorder = new DefaultContainerRecorder(new DefaultNanoContainer());
        MutablePicoContainer recorded = recorder.getContainerProxy();

        recorded.addComponent("fruit", "apple");
        recorded.addComponent("int", 239);
        recorded.addComponent("thing",
                ThingThatTakesParamsInConstructor.class,
                ComponentParameter.DEFAULT,
                ComponentParameter.DEFAULT);

        MutablePicoContainer slave = new DefaultPicoContainer();
        recorder.replay(slave);
        assertEquals("apple", slave.getComponent("fruit"));
        assertEquals("apple239", ((ThingThatTakesParamsInConstructor) slave.getComponent("thing")).getValue());

        // test that we can replay once more
        MutablePicoContainer anotherSlave = new DefaultPicoContainer();
        recorder.replay(anotherSlave);
        assertEquals("apple", anotherSlave.getComponent("fruit"));
        assertEquals("apple239", ((ThingThatTakesParamsInConstructor) anotherSlave.getComponent("thing")).getValue());
    }

    public void testRecorderWorksAfterSerialization() throws IOException, ClassNotFoundException, IllegalAccessException, InvocationTargetException {
        ContainerRecorder recorder = new DefaultContainerRecorder(new DefaultPicoContainer());
        MutablePicoContainer recorded = recorder.getContainerProxy();
        recorded.addComponent("fruit", "apple");

        ContainerRecorder serializedRecorder = (ContainerRecorder) serializeAndDeserialize(recorder);
        MutablePicoContainer slave = new DefaultPicoContainer();
        serializedRecorder.replay(slave);
        assertEquals("apple", slave.getComponent("fruit"));
    }

    private Object serializeAndDeserialize(Object o) throws IOException, ClassNotFoundException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(baos);

        oos.writeObject(o);
        ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(baos.toByteArray()));

        return ois.readObject();
    }


    public void testXMLRecorderHierarchy() {
        MutablePicoContainer parentPrototype = new DefaultPicoContainer(new CachingBehaviorFactory().forThis(new AdaptiveInjectionFactory()));
        DefaultContainerRecorder parentRecorder = new DefaultContainerRecorder(parentPrototype);
        StringReader parentResource = new StringReader("" 
                + "<container>" 
                + "  <component-implementation key='wilma' class='"+WilmaImpl.class.getName()+"'/>"
                + "</container>" 
                );

        populateXMLContainer(parentRecorder, parentResource);
        MutablePicoContainer parentContainer = parentRecorder.getContainerProxy();
        assertNull(parentContainer.getComponent("fred"));
        assertNotNull(parentContainer.getComponent("wilma"));

        MutablePicoContainer childPrototype = new DefaultPicoContainer(parentPrototype);
        DefaultContainerRecorder childRecorder = new DefaultContainerRecorder(childPrototype);
        StringReader childResource = new StringReader("" 
                + "<container>" 
                + "  <component-implementation key='fred' class='"+FredImpl.class.getName()+"'>"
                + "     <parameter key='wilma'/>"  
               + "  </component-implementation>"
                + "</container>" 
                );
        populateXMLContainer(childRecorder, childResource);
        MutablePicoContainer childContainer = childRecorder.getContainerProxy();
        assertNotNull(childContainer.getComponent("fred"));
        assertNotNull(childContainer.getComponent("wilma"));
        FredImpl fred = (FredImpl)childContainer.getComponent("fred");
        Wilma wilma = (Wilma)childContainer.getComponent("wilma");
        assertSame(wilma, fred.wilma());
    }
                       
    private void populateXMLContainer(ContainerRecorder recorder, Reader resource) {
        ContainerPopulator populator = new XMLContainerBuilder(resource, Thread.currentThread().getContextClassLoader());
        populator.populateContainer(recorder.getContainerProxy());
    }       
}
