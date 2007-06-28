package org.picocontainer.doc.tutorial.lifecycle;

import junit.framework.TestCase;
import org.picocontainer.MutablePicoContainer;
import org.picocontainer.DefaultPicoContainer;
import org.picocontainer.doc.tutorial.interfaces.Boy;

public class LifecycleTestCase extends TestCase {

    public void testStartStopDispose() {
        MutablePicoContainer pico = new DefaultPicoContainer();
        pico.addComponent(Boy.class);
        pico.addComponent(Girl.class);

// START SNIPPET: start
        pico.start();
// END SNIPPET: start

// START SNIPPET: stopdispose
        pico.stop();
        pico.dispose();
// END SNIPPET: stopdispose
    }


}
