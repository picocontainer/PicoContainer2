package org.picocontainer.tck;

import org.picocontainer.PicoException;
import org.picocontainer.MutablePicoContainer;

import java.util.Map;
import java.util.HashMap;
import java.io.IOException;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

/**
 * @author Paul Hammant
 * @version $Revision$
 */
public abstract class AbstractImplementationHidingPicoContainerTestCase extends AbstractPicoContainerTestCase {


    public void testInstanceIsNotAutomaticallyHidden() {
        MutablePicoContainer pc = createImplementationHidingPicoContainer();
        pc.addComponent(Map.class, new HashMap());
        Map map = pc.getComponent(Map.class);
        assertNotNull(map);
        assertTrue(map instanceof HashMap);
    }

    protected abstract MutablePicoContainer createImplementationHidingPicoContainer();


    public void testImplementaionIsAutomaticallyHidden() {
        MutablePicoContainer pc = createImplementationHidingPicoContainer();
        pc.addComponent(Map.class, HashMap.class);
        Map map = pc.getComponent(Map.class);
        assertNotNull(map);
        assertFalse(map instanceof HashMap);
    }

    public void testNonInterfaceImplementaionIsAutomaticallyHidden() {
        MutablePicoContainer pc = createImplementationHidingPicoContainer();
        pc.addComponent(HashMap.class, HashMap.class);
        Map map = pc.getComponent(HashMap.class);
        assertNotNull(map);
        assertTrue(map instanceof HashMap);
    }

    public void testNonInterfaceImplementaionWithParametersIsAutomaticallyHidden() {
        MutablePicoContainer pc = createImplementationHidingPicoContainer();
        pc.addComponent(HashMap.class, HashMap.class);
        Map map = pc.getComponent(HashMap.class);
        assertNotNull(map);
        assertTrue(map instanceof HashMap);
    }


    public void testImplementaionWithParametersIsAutomaticallyHidden() {
        MutablePicoContainer pc = createImplementationHidingPicoContainer();
        pc.addComponent(Map.class, HashMap.class);
        Map map = pc.getComponent(Map.class);
        assertNotNull(map);
        assertFalse(map instanceof HashMap);
    }

    public void testSerializedContainerCanRetrieveImplementation() throws PicoException,
                                                                          IOException, ClassNotFoundException {
        try {
            super.testSerializedContainerCanRetrieveImplementation();
            fail("The ImplementationHidingPicoContainer should not be able to retrieve the component impl");
        } catch (ClassCastException cce) {
            // expected.
        }
    }

    public void testExceptionThrowingFromHiddenComponent() {
        MutablePicoContainer pc = createImplementationHidingPicoContainer();
        pc.addComponent(ActionListener.class, Burp.class);
        try {
            ActionListener ac = pc.getComponent(ActionListener.class);
            ac.actionPerformed(null);
            fail("Oh no.");
        } catch (RuntimeException e) {
            assertEquals("woohoo", e.getMessage());
        }
    }

    public static class Burp implements ActionListener {

        public void actionPerformed(ActionEvent e) {
            throw new RuntimeException("woohoo");
        }
    }

}
