package org.picocontainer.doc.tutorial.simple2;

import junit.framework.TestCase;
import org.picocontainer.MutablePicoContainer;
import org.picocontainer.DefaultPicoContainer;
import org.picocontainer.doc.tutorial.interfaces.Kissable;
import org.picocontainer.doc.tutorial.simple.Girl;

public class ConcreteClasses2TestCase extends TestCase {

    public void testAssembleComponentsAndInstantiateAndUseThem() {
        // START SNIPPET: assemble
        MutablePicoContainer pico = new DefaultPicoContainer();
        pico.addComponent(Kissable.class, Boy.class);
        pico.addComponent(Girl.class);
        // END SNIPPET: assemble

    }


}
