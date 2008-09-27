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
            public <T> ComponentAdapter<T> createComponentAdapter(ComponentMonitor componentMonitor,
                                                                  LifecycleStrategy lifecycleStrategy,
                                                                  Properties componentProperties,
                                                                  final Object componentKey, Class<T> componentImplementation,
                                                                  Parameter... parameters) throws PicoCompositionException {
                return new AbstractInjector(componentKey, componentImplementation, parameters, componentMonitor, lifecycleStrategy, false) {
                    public Object getComponentInstance(PicoContainer container, Type into) throws PicoCompositionException {
                        return parent.getComponent(componentKey);
                    }

                    public void decorateComponentInstance(PicoContainer container, Type into, Object instance) {
                        System.out.println("");
                    }
                };
            }
        }, reinjectionFactory);
    }

}
