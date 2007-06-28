package org.picocontainer.containers;

import org.picocontainer.DefaultPicoContainer;
import org.picocontainer.PicoContainer;
import org.picocontainer.injectors.ConstructorInjectionFactory;
import org.picocontainer.behaviors.CachingBehaviorFactory;
import org.picocontainer.lifecycle.NullLifecycleStrategy;
import org.picocontainer.monitors.NullComponentMonitor;

public class TransientPicoContainer extends DefaultPicoContainer {

    public TransientPicoContainer() {
        super(new CachingBehaviorFactory().forThis(new ConstructorInjectionFactory()), NullLifecycleStrategy.getInstance(), null, NullComponentMonitor.getInstance());
    }

    public TransientPicoContainer(PicoContainer parent) {
        super(new CachingBehaviorFactory().forThis(new ConstructorInjectionFactory()), NullLifecycleStrategy.getInstance(), parent, NullComponentMonitor.getInstance());
    }
}
