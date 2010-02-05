package org.picocontainer.injectors;

import org.junit.Test;
import org.picocontainer.ComponentMonitorStrategy;
import org.picocontainer.Parameter;
import org.picocontainer.monitors.NullComponentMonitor;
import org.picocontainer.monitors.WriterComponentMonitor;
import org.picocontainer.tck.AbstractComponentAdapterTest;
import org.picocontainer.testmodel.NullLifecycle;
import org.picocontainer.testmodel.SimpleTouchable;
import org.picocontainer.testmodel.Touchable;

import java.io.PrintWriter;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class LifecycleAdapterTestCase {

    private final ConstructorInjector INJECTOR = new ConstructorInjector(
            NullLifecycle.class, NullLifecycle.class, new Parameter[0],
            new NullComponentMonitor(), false);

    private AbstractComponentAdapterTest.RecordingLifecycleStrategy strategy = new AbstractComponentAdapterTest.RecordingLifecycleStrategy(new StringBuffer());

    @Test
    public void passesOnLifecycleOperations() {
        AbstractInjectionFactory.LifecycleAdapter la = new AbstractInjectionFactory.LifecycleAdapter(INJECTOR, strategy);
        Touchable touchable = new SimpleTouchable();
        la.start(touchable);
        la.stop(touchable);
        la.dispose(touchable);
        assertEquals("<start<stop<dispose", strategy.recording());
    }

    @Test
    public void canHaveMonitorChanged() {
        AbstractComponentAdapterTest.RecordingLifecycleStrategy strategy = new AbstractComponentAdapterTest.RecordingLifecycleStrategy(new StringBuffer());
        ComponentMonitorStrategy cms = new AbstractInjectionFactory.LifecycleAdapter(INJECTOR, strategy);
        assertTrue(cms.currentMonitor() instanceof NullComponentMonitor);
        cms.changeMonitor(new WriterComponentMonitor(new PrintWriter(System.out)));
        assertTrue(cms.currentMonitor() instanceof WriterComponentMonitor);

    }
}
