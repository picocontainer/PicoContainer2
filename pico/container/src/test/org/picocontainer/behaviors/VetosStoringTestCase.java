package org.picocontainer.behaviors;

import org.picocontainer.DefaultPicoContainer;
import org.picocontainer.PicoCompositionException;
import org.junit.Test;import static org.junit.Assert.fail;

import java.util.Set;
import java.util.HashSet;

public class VetosStoringTestCase {

    @Test
    public void testThatNeverStoringVetosAdditions() {
        VetosStoring neverStoring = new VetosStoring();
        DefaultPicoContainer pico = new DefaultPicoContainer(neverStoring);
        pico.addComponent(Set.class, HashSet.class);
        try {
            Set set = pico.getComponent(Set.class);
            fail("should have barfed");
        } catch (PicoCompositionException e) {
            //expected;
        }
    }

    @Test
    public void testThatParentAdditionsAreFine() {
        DefaultPicoContainer parent = new DefaultPicoContainer();
        parent.addComponent(Set.class, HashSet.class);
        VetosStoring neverStoring = new VetosStoring();
        DefaultPicoContainer pico = new DefaultPicoContainer(neverStoring, parent);
        Set set = pico.getComponent(Set.class);
    }


}
