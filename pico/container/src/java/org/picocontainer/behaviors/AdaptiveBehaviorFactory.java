/*****************************************************************************
 * Copyright (C) PicoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the LICENSE.txt file.                                                     *
 *                                                                           *
 * Original code by                                                          *
 *****************************************************************************/
package org.picocontainer.behaviors;

import org.picocontainer.ComponentAdapter;
import org.picocontainer.ComponentMonitor;
import org.picocontainer.LifecycleStrategy;
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
import java.util.Properties;

public class AdaptiveBehaviorFactory implements ComponentFactory, Serializable {

    public ComponentAdapter createComponentAdapter(ComponentMonitor componentMonitor,
                                                   LifecycleStrategy lifecycleStrategy,
                                                   Properties componentProperties,
                                                   Object componentKey,
                                                   Class componentImplementation,
                                                   Parameter... parameters) throws PicoCompositionException {
        List<ComponentFactory> list = new ArrayList<ComponentFactory>();
        ComponentFactory lastFactory = makeInjectionFactory();
        processThreadSafe(componentProperties, list);
        processImplementationHiding(componentProperties, list);
        processCachedInstance(componentProperties, componentImplementation, list);

        //Instantiate Chain of ComponentFactories
        for (ComponentFactory componentFactory : list) {
            if (lastFactory != null && componentFactory instanceof BehaviorFactory) {
                ((BehaviorFactory)componentFactory).forThis(lastFactory);
            }
            lastFactory = componentFactory;
        }

        return lastFactory.createComponentAdapter(componentMonitor,
                                                  lifecycleStrategy,
                                                  componentProperties,
                                                  componentKey,
                                                  componentImplementation,
                                                  parameters);
    }

    protected AdaptiveInjectionFactory makeInjectionFactory() {
        return new AdaptiveInjectionFactory();
    }

    protected void processThreadSafe(Properties componentProperties, List<ComponentFactory> list) {
        if (AbstractBehaviorFactory.removePropertiesIfPresent(componentProperties,Characterizations.THREAD_SAFE)) {
            list.add(new SynchronizedBehaviorFactory());
        }
    }

    protected void processCachedInstance(Properties componentProperties,
                                       Class componentImplementation,
                                       List<ComponentFactory> list) {
        if (AbstractBehaviorFactory.removePropertiesIfPresent(componentProperties,Characterizations.CACHE) ||
            componentImplementation.getAnnotation(Cache.class) != null) {
            list.add(new CachingBehaviorFactory());
        }
    }

    protected void processImplementationHiding(Properties componentProperties,
                                             List<ComponentFactory> list) {
        if (AbstractBehaviorFactory.removePropertiesIfPresent(componentProperties,Characterizations.HIDE)) {
            list.add(new ImplementationHidingBehaviorFactory());
        }
    }

}
