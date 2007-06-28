package org.picocontainer.gems;

import org.picocontainer.gems.behaviors.ImplementationHidingBehaviorFactory;
import org.picocontainer.gems.monitors.Log4JComponentMonitor;
import org.picocontainer.ComponentMonitor;
import org.picocontainer.BehaviorFactory;

public class PicoGemsBuilder {

    public static BehaviorFactory IMPL_HIDING() {
        return new ImplementationHidingBehaviorFactory();
    }

    public static ComponentMonitor LOG4J() {
        return new Log4JComponentMonitor();
    }



}
