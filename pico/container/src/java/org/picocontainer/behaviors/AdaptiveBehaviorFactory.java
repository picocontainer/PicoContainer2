package org.picocontainer.behaviors;

import org.picocontainer.ComponentAdapter;
import org.picocontainer.ComponentMonitor;
import org.picocontainer.LifecycleStrategy;
import org.picocontainer.ComponentCharacteristics;
import org.picocontainer.Parameter;
import org.picocontainer.PicoCompositionException;
import org.picocontainer.Characterizations;
import org.picocontainer.ComponentFactory;
import org.picocontainer.BehaviorFactory;
import org.picocontainer.annotations.Cache;
import org.picocontainer.injectors.AdaptiveInjectionFactory;

import java.io.Serializable;
import java.util.List;
import java.util.ArrayList;

public class AdaptiveBehaviorFactory implements ComponentFactory, Serializable {

    public ComponentAdapter createComponentAdapter(ComponentMonitor componentMonitor,
                                                   LifecycleStrategy lifecycleStrategy,
                                                   ComponentCharacteristics componentCharacteristics,
                                                   Object componentKey,
                                                   Class componentImplementation,
                                                   Parameter... parameters) throws PicoCompositionException {
        List<ComponentFactory> list = new ArrayList<ComponentFactory>();
        ComponentFactory lastFactory = makeInjectionFactory();
        processThreadSafe(componentCharacteristics, list);
        processImplementationHiding(componentCharacteristics, list);
        processCachedInstance(componentCharacteristics, componentImplementation, list);

        //Instantiate Chain of ComponentFactories
        for (ComponentFactory componentFactory : list) {
            if (lastFactory != null && componentFactory instanceof BehaviorFactory) {
                ((BehaviorFactory)componentFactory).forThis(lastFactory);
            }
            lastFactory = componentFactory;
        }

        return lastFactory.createComponentAdapter(componentMonitor,
                                                  lifecycleStrategy,
                                                  componentCharacteristics,
                                                  componentKey,
                                                  componentImplementation,
                                                  parameters);
    }

    protected AdaptiveInjectionFactory makeInjectionFactory() {
        return new AdaptiveInjectionFactory();
    }

    protected void processThreadSafe(ComponentCharacteristics componentCharacteristics, List<ComponentFactory> list) {
        if (Characterizations.THREAD_SAFE.setAsProcessedIfSoCharacterized(componentCharacteristics)) {
            list.add(new SynchronizedBehaviorFactory());
        }
    }

    protected void processCachedInstance(ComponentCharacteristics componentCharacteristics,
                                       Class componentImplementation,
                                       List<ComponentFactory> list) {
        if (Characterizations.CACHE.setAsProcessedIfSoCharacterized(componentCharacteristics) ||
            componentImplementation.getAnnotation(Cache.class) != null) {
            list.add(new CachingBehaviorFactory());
        }
    }

    protected void processImplementationHiding(ComponentCharacteristics componentCharacteristics,
                                             List<ComponentFactory> list) {
        if (Characterizations.HIDE.setAsProcessedIfSoCharacterized(componentCharacteristics)) {
            list.add(new ImplementationHidingBehaviorFactory());
        }
    }

}
