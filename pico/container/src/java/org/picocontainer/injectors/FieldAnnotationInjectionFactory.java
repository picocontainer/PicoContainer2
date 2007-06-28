package org.picocontainer.injectors;

import org.picocontainer.InjectionFactory;
import org.picocontainer.ComponentAdapter;
import org.picocontainer.ComponentMonitor;
import org.picocontainer.LifecycleStrategy;
import org.picocontainer.ComponentCharacteristics;
import org.picocontainer.Parameter;
import org.picocontainer.PicoCompositionException;

public class FieldAnnotationInjectionFactory implements InjectionFactory {


    public ComponentAdapter createComponentAdapter(ComponentMonitor componentMonitor,
                                                   LifecycleStrategy lifecycleStrategy,
                                                   ComponentCharacteristics componentCharacteristics,
                                                   Object componentKey,
                                                   Class componentImplementation,
                                                   Parameter... parameters)
        throws PicoCompositionException {
        return new FieldAnnotationInjector(componentKey, componentImplementation, parameters, componentMonitor, lifecycleStrategy);
    }
}
