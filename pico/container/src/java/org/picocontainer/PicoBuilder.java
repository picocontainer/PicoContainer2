package org.picocontainer;

import static org.picocontainer.behaviors.Behaviors.caching;
import static org.picocontainer.behaviors.Behaviors.implHiding;
import org.picocontainer.behaviors.PropertyApplyingBehaviorFactory;
import org.picocontainer.behaviors.SynchronizedBehaviorFactory;
import org.picocontainer.containers.EmptyPicoContainer;
import org.picocontainer.containers.TransientPicoContainer;
import static org.picocontainer.injectors.Injectors.CDI;
import static org.picocontainer.injectors.Injectors.MADI;
import static org.picocontainer.injectors.Injectors.SDI;
import static org.picocontainer.injectors.Injectors.anyDI;
import org.picocontainer.lifecycle.NullLifecycleStrategy;
import org.picocontainer.lifecycle.ReflectionLifecycleStrategy;
import org.picocontainer.lifecycle.StartableLifecycleStrategy;
import org.picocontainer.monitors.ConsoleComponentMonitor;
import org.picocontainer.monitors.NullComponentMonitor;

import java.util.ArrayList;
import java.util.Stack;
import java.util.List;

public class PicoBuilder {

    private PicoContainer parentContainer;
    private Class<? extends MutablePicoContainer> mpcClass = DefaultPicoContainer.class;
    private ComponentMonitor componentMonitor;
    private List<Object> containerComps = new ArrayList<Object>();

    public PicoBuilder(PicoContainer parentContainer, InjectionFactory injectionType) {
        this.injectionType = injectionType;
        if (parentContainer != null) {
            this.parentContainer = parentContainer;
        } else {
            this.parentContainer = new EmptyPicoContainer();
        }
    }

    public PicoBuilder(PicoContainer parentContainer) {
        this(parentContainer, anyDI());
    }

    public PicoBuilder(InjectionFactory injectionType) {
        this(new EmptyPicoContainer(), injectionType);
    }

    public PicoBuilder() {
        this(new EmptyPicoContainer(), anyDI());
    }

    private final Stack<Object> componentFactories = new Stack<Object>();

    private InjectionFactory injectionType;

    private Class<? extends ComponentMonitor> componentMonitorClass = NullComponentMonitor.class;
    private Class<? extends LifecycleStrategy> lifecycleStrategyClass = NullLifecycleStrategy.class;

    public PicoBuilder withLifecycle() {
        lifecycleStrategyClass = StartableLifecycleStrategy.class;
        return this;
    }

    public PicoBuilder withReflectionLifecycle() {
        lifecycleStrategyClass = ReflectionLifecycleStrategy.class;
        return this;
    }

    public PicoBuilder withConsoleMonitor() {
        componentMonitorClass =  ConsoleComponentMonitor.class;
        return this;
    }

    public PicoBuilder withMonitor(Class<? extends ComponentMonitor> cmClass) {
        if (cmClass == null) {
            throw new NullPointerException("monitor class cannot be null");
        }
        if (!ComponentMonitor.class.isAssignableFrom(cmClass)) {
            throw new ClassCastException(cmClass.getName() + " is not a " + ComponentMonitor.class.getName());

        }
        componentMonitorClass = cmClass;
        componentMonitor = null;
        return this;
    }

    public MutablePicoContainer build() {

        DefaultPicoContainer temp = new TransientPicoContainer();
        temp.addComponent(PicoContainer.class, parentContainer);

        for (Object containerComp : containerComps) {
            temp.addComponent(containerComp);
        }

        ComponentFactory lastCaf = injectionType;
        while (!componentFactories.empty()) {
            Object componentFactory = componentFactories.pop();
            DefaultPicoContainer temp2 = new TransientPicoContainer(temp);
            temp2.addComponent("componentFactory", componentFactory);
            if (lastCaf != null) {
                temp2.addComponent(ComponentFactory.class, lastCaf);
            }
            ComponentFactory penultimateCaf = lastCaf;
            lastCaf = (ComponentFactory) temp2.getComponent("componentFactory");
            if (lastCaf instanceof BehaviorFactory) {
                ((BehaviorFactory) lastCaf).forThis(penultimateCaf);
            }
        }

        temp.addComponent(ComponentFactory.class, lastCaf);
        if (componentMonitorClass == null) {
            temp.addComponent(ComponentMonitor.class, componentMonitor);
        } else {
            temp.addComponent(ComponentMonitor.class, componentMonitorClass);
        }
        temp.addComponent(LifecycleStrategy.class, lifecycleStrategyClass);
        temp.addComponent("mpc", mpcClass);


        return (MutablePicoContainer) temp.getComponent("mpc");
    }

    public PicoBuilder withHiddenImplementations() {
        componentFactories.push(implHiding());
        return this;
    }

    public PicoBuilder withSetterInjection() {
        injectionType = SDI();
        return this;
    }

    public PicoBuilder withAnnotationInjection() {
        injectionType = MADI();
        return this;
    }

    public PicoBuilder withConstructorInjection() {
        injectionType = CDI();
        return this;
    }

    public PicoBuilder withCaching() {
        componentFactories.push(caching());
        return this;
    }

    public PicoBuilder withComponentFactory(ComponentFactory componentFactory) {
        if (componentFactory == null) {
            throw new NullPointerException("CAF cannot be null");
        }
        componentFactories.push(componentFactory);
        return this;
    }

    public PicoBuilder withThreadSafety() {
        componentFactories.push(SynchronizedBehaviorFactory.class);
        return this;
    }


    public PicoBuilder withBehaviors(BehaviorFactory... factories) {
        for (ComponentFactory componentFactory : factories) {
            componentFactories.push(componentFactory);
        }
        return this;
    }

    public PicoBuilder implementedBy(Class<? extends MutablePicoContainer> containerClass) {
        mpcClass = containerClass;
        return this;
    }

    public PicoBuilder withMonitor(ComponentMonitor componentMonitor) {
        this.componentMonitor = componentMonitor;
        componentMonitorClass = null;
        return this;
    }

    public PicoBuilder withComponentFactory(Class<? extends ComponentFactory> componentFactoryClass) {
        componentFactories.push(componentFactoryClass);
        return this;
    }

    public PicoBuilder withCustomContainerComponent(Object containerDependency) {
        containerComps.add(containerDependency);
        return this;
    }

    public PicoBuilder withPropertyApplier() {
        componentFactories.push(PropertyApplyingBehaviorFactory.class);
        return this;
    }
}
