package org.picocontainer.injectors;

import org.picocontainer.ComponentAdapter;
import org.picocontainer.ComponentMonitor;
import org.picocontainer.LifecycleStrategy;
import org.picocontainer.Parameter;
import org.picocontainer.PicoCompositionException;

import java.util.Properties;

public class NamedMethodInjection extends AbstractInjectionFactory {

        private final String prefix;

    public NamedMethodInjection(String setterMethodPrefix) {
        this.prefix = setterMethodPrefix;
    }

    public NamedMethodInjection() {
        this("set");
    }

    public <T> ComponentAdapter<T> createComponentAdapter(ComponentMonitor monitor, LifecycleStrategy lifecycleStrategy, Properties componentProperties, Object componentKey, Class<T> componentImplementation, Parameter... parameters) throws PicoCompositionException {
        return wrapLifeCycle(monitor.newInjector(new NamedMethodInjector(componentKey, componentImplementation, parameters, monitor, prefix)), lifecycleStrategy);
    }
}
