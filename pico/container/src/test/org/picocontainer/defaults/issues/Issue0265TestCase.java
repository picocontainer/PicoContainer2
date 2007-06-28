package org.picocontainer.defaults.issues;

import org.jmock.MockObjectTestCase;
import org.jmock.Mock;
import org.jmock.core.Constraint;

import org.picocontainer.ComponentMonitor;
import org.picocontainer.Startable;
import org.picocontainer.DefaultPicoContainer;
import org.picocontainer.DefaultPicoContainerTestCase;
import org.picocontainer.Characterizations;

import java.lang.reflect.Method;

public class Issue0265TestCase extends MockObjectTestCase {

    public void testCanReallyChangeMonitor() throws SecurityException, NoSuchMethodException {
        Method start = Startable.class.getMethod("start");
        Method stop = Startable.class.getMethod("stop");
        Mock mockMonitor1 = mock(ComponentMonitor.class, "Monitor1");
        Mock mockMonitor2 = mock(ComponentMonitor.class, "Monitor2");
        DefaultPicoContainer pico = new DefaultPicoContainer((ComponentMonitor) mockMonitor1.proxy());
        pico.as(Characterizations.CACHE).addComponent(DefaultPicoContainerTestCase.MyStartable.class);
        mockMonitor1.expects(once()).method("instantiating").will(returnValue(DefaultPicoContainerTestCase.MyStartable.class.getConstructor()));
        mockMonitor1.expects(once()).method("instantiated");
        mockMonitor1.expects(once()).method("invoking").with(NULL, NULL, eq(start), ANYTHING);
        mockMonitor1.expects(once()).method("invoked").with(new Constraint[] {NULL, NULL, eq(start), ANYTHING, ANYTHING});
        mockMonitor1.expects(once()).method("invoking").with(NULL, NULL, eq(stop), ANYTHING);
        mockMonitor1.expects(once()).method("invoked").with(new Constraint[] {NULL, NULL, eq(stop), ANYTHING, ANYTHING});
        pico.start();
        pico.stop();
        Startable startable = pico.getComponent(DefaultPicoContainerTestCase.MyStartable.class);
        assertNotNull(startable);
        pico.changeMonitor((ComponentMonitor) mockMonitor2.proxy());
        mockMonitor2.expects(once()).method("invoking").with(NULL, NULL, eq(start), ANYTHING);
        mockMonitor2.expects(once()).method("invoked").with(new Constraint[] {NULL, NULL, eq(start), ANYTHING, ANYTHING});
        mockMonitor2.expects(once()).method("invoking").with(NULL, NULL, eq(stop), ANYTHING);
        mockMonitor2.expects(once()).method("invoked").with(new Constraint[] {NULL, NULL, eq(stop), ANYTHING, ANYTHING});
        pico.start();
        pico.stop();
    }

}
