package org.picocontainer.defaults;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.core.JVM;
import com.thoughtworks.xstream.io.xml.XppDriver;
import junit.framework.TestCase;
import org.picocontainer.MutablePicoContainer;
import org.picocontainer.PicoContainer;
import org.picocontainer.DefaultPicoContainer;
import org.picocontainer.testmodel.DependsOnTouchable;
import org.picocontainer.testmodel.SimpleTouchable;

/**
 * @author Aslak Helles&oslash;y
 * @version $Revision$
 */
public final class XStreamSerialisationTestCase extends TestCase {
    private final XStream xStream = new XStream(new XppDriver());

    public void testShouldBeAbleToSerialiseEmptyPico() {
        if (JVM.is14()) {
            MutablePicoContainer pico = new DefaultPicoContainer();
            String picoXml = xStream.toXML(pico);
            PicoContainer serializedPico = (PicoContainer) xStream.fromXML(picoXml);

            assertEquals(0, serializedPico.getComponents().size());
        }
    }

    public void testShouldBeAbleToSerialisePicoWithUninstantiatedComponents() {
        if (JVM.is14()) {
            MutablePicoContainer pico = new DefaultPicoContainer();
            pico.addComponent(SimpleTouchable.class);
            pico.addComponent(DependsOnTouchable.class);
            String picoXml = xStream.toXML(pico);
            PicoContainer serializedPico = (PicoContainer) xStream.fromXML(picoXml);

            assertEquals(2, serializedPico.getComponents().size());
        }
    }

    public void testShouldBeAbleToSerialisePicoWithInstantiatedComponents() {
        if (JVM.is14()) {
            MutablePicoContainer pico = new DefaultPicoContainer();
            pico.addComponent(SimpleTouchable.class);
            pico.addComponent(DependsOnTouchable.class);
            pico.getComponents();
            String picoXml = xStream.toXML(pico);
            PicoContainer serializedPico = (PicoContainer) xStream.fromXML(picoXml);

            assertEquals(2, serializedPico.getComponents().size());
        }
    }
}