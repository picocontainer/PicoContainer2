package org.picocontainer.gems.behaviors;

import org.picocontainer.LifecycleStrategy;
import org.picocontainer.ComponentAdapter;
import org.picocontainer.ComponentCharacteristics;
import org.picocontainer.Parameter;
import org.picocontainer.PicoCompositionException;
import org.picocontainer.ComponentMonitor;
import org.picocontainer.Characterizations;
import org.picocontainer.behaviors.AbstractBehaviorFactory;

public class ImplementationHidingBehaviorFactory extends AbstractBehaviorFactory {

    public ComponentAdapter createComponentAdapter(ComponentMonitor componentMonitor, LifecycleStrategy lifecycleStrategy, ComponentCharacteristics componentCharacteristics, Object componentKey, Class componentImplementation, Parameter... parameters)
            throws PicoCompositionException {
        Characterizations.HIDE.setAsProcessedIfSoCharacterized(componentCharacteristics);
        ComponentAdapter componentAdapter = super.createComponentAdapter(componentMonitor, lifecycleStrategy,
                                                                         componentCharacteristics, componentKey, componentImplementation, parameters);
        return new ImplementationHidingBehavior(componentAdapter);
    }
}