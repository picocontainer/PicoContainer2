package org.picocontainer.doc.tutorial.interfaces;

import junit.framework.TestCase;
import org.picocontainer.MutablePicoContainer;
import org.picocontainer.DefaultPicoContainer;

public class InterfacesTestCase extends TestCase {
    public void testKissing() {
        MutablePicoContainer pico = new DefaultPicoContainer();
        pico.addComponent(Boy.class);
        pico.addComponent(Girl.class);

        Girl girl = pico.getComponent(Girl.class);
        girl.kissSomeone();
    }
}
