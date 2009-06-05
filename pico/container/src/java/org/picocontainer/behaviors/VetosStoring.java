package org.picocontainer.behaviors;

import org.picocontainer.PicoCompositionException;

import java.util.Map;
import java.util.AbstractMap;
import java.util.Set;
import java.util.Collections;

public class VetosStoring extends Storing {

    protected Map makeMap() {
        return new NotAMap();
    }

    private RuntimeException youCantAddToVetosStoringBehavior() {
        return new PicoCompositionException("You can't add components to a container that is " +
                "designated has org.picocontainer.behaviors.NeverStoring as a behavior");
    }

    private class NotAMap extends AbstractMap {

        public Set entrySet() {
            return Collections.EMPTY_SET;
        }

        public Object put(Object o, Object o1) {
            throw youCantAddToVetosStoringBehavior();
        }

        public void putAll(Map map) {
            throw youCantAddToVetosStoringBehavior();
        }

    }
}
