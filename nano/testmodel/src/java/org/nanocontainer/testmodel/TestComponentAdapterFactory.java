package org.nanocontainer.testmodel;

import org.picocontainer.ComponentAdapter;
import org.picocontainer.Parameter;
import org.picocontainer.PicoCompositionException;
import org.picocontainer.ComponentCharacteristics;
import org.picocontainer.ComponentMonitor;
import org.picocontainer.LifecycleStrategy;
import org.picocontainer.injectors.AdaptiveInjectionFactory;

/**
 * @author Paul Hammant
 * @version $Revision$
 */
public final class TestComponentAdapterFactory extends AdaptiveInjectionFactory {

    public final StringBuffer sb;

    public TestComponentAdapterFactory(StringBuffer sb) {
        this.sb = sb;
    }

    public ComponentAdapter createComponentAdapter(ComponentMonitor componentMonitor, LifecycleStrategy lifecycleStrategy, ComponentCharacteristics componentCharacteristics, Object componentKey, Class componentImplementation, Parameter[] parameters) throws
                                                                                                                                                                                                                                                        PicoCompositionException
    {
        sb.append("called");
        return super.createComponentAdapter(componentMonitor, lifecycleStrategy,
                                            componentCharacteristics, componentKey, componentImplementation, parameters);
    }
}
