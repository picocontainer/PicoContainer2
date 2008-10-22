package org.picocontainer.injectors;

import org.picocontainer.ComponentAdapter;
import org.picocontainer.ComponentMonitor;
import org.picocontainer.InjectionFactory;
import org.picocontainer.LifecycleStrategy;
import org.picocontainer.Parameter;
import org.picocontainer.PicoCompositionException;
import org.picocontainer.PicoContainer;

import java.lang.reflect.Type;
import java.util.Properties;

public class Reinjection extends CompositeInjection {

    public Reinjection(InjectionFactory reinjectionFactory, final PicoContainer parent) {
        super(new AbstractInjectionFactory() {
            public <T> ComponentAdapter<T> createComponentAdapter(
                    ComponentMonitor componentMonitor, LifecycleStrategy lifecycleStrategy,
                    Properties componentProperties, final Object componentKey, Class<T> componentImplementation,
                    Parameter... parameters) throws PicoCompositionException {
                return new ReinjectionInjector(componentKey, componentImplementation, parameters, componentMonitor, lifecycleStrategy, parent);
            }
        }, reinjectionFactory);
    }

    private static class ReinjectionInjector<T> extends AbstractInjector {
        private final PicoContainer parent;

        public ReinjectionInjector(Object componentKey, Class<T> componentImplementation, Parameter[] parameters, ComponentMonitor componentMonitor, LifecycleStrategy lifecycleStrategy, PicoContainer parent) {
            super(componentKey, componentImplementation, parameters, componentMonitor, lifecycleStrategy, false);
            this.parent = parent;
        }

        public Object getComponentInstance(PicoContainer container, Type into) throws PicoCompositionException {
            return parent.getComponent(getComponentKey());
        }

    }
}
