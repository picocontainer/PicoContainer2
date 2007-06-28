package org.picocontainer.doc.tutorial.simple;

import junit.framework.TestCase;
import org.picocontainer.MutablePicoContainer;
import org.picocontainer.DefaultPicoContainer;

public class ConcreteClassesTestCase extends TestCase {

    public void testAssembleComponentsAndInstantiateAndUseThem() {
        // START SNIPPET: assemble
        MutablePicoContainer pico = new DefaultPicoContainer();
        pico.addComponent(Boy.class);
        pico.addComponent(Girl.class);
        // END SNIPPET: assemble

        // START SNIPPET: instantiate-and-use
        Girl girl = pico.getComponent(Girl.class);
        girl.kissSomeone();
        // END SNIPPET: instantiate-and-use
    }


}
