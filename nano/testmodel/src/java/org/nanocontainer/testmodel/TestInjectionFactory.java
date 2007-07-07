package org.nanocontainer.testmodel;

import org.picocontainer.ComponentAdapter;
import org.picocontainer.Parameter;
import org.picocontainer.PicoCompositionException;
import org.picocontainer.ComponentMonitor;
import org.picocontainer.LifecycleStrategy;
import org.picocontainer.injectors.AdaptiveInjectionFactory;

import java.util.Properties;

/**
 * @author Paul Hammant
 * @version $Revision$
 */
public final class TestInjectionFactory extends AdaptiveInjectionFactory {

    public final StringBuffer sb;

    public TestInjectionFactory(StringBuffer sb) {
        this.sb = sb;
    }

    public ComponentAdapter createComponentAdapter(ComponentMonitor componentMonitor, LifecycleStrategy lifecycleStrategy,
                    Properties componentProperties, Object componentKey, Class componentImplementation, Parameter... parameters) throws PicoCompositionException {
        sb.append("called");
        return super.createComponentAdapter(componentMonitor, lifecycleStrategy,
                                            componentProperties, componentKey, componentImplementation, parameters);
    }
}
