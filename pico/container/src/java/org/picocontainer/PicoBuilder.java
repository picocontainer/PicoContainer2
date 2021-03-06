/*****************************************************************************
 * Copyright (C) PicoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the LICENSE.txt file.                                                     *
 *                                                                           *
 * Original code by                                                          *
 *****************************************************************************/
package org.picocontainer;

import org.picocontainer.behaviors.Automating;
import org.picocontainer.behaviors.Locking;
import org.picocontainer.behaviors.PropertyApplying;
import org.picocontainer.behaviors.Synchronizing;
import org.picocontainer.containers.EmptyPicoContainer;
import org.picocontainer.containers.TransientPicoContainer;
import org.picocontainer.injectors.CompositeInjection;
import org.picocontainer.injectors.MethodInjection;
import org.picocontainer.lifecycle.JavaEE5LifecycleStrategy;
import org.picocontainer.lifecycle.NullLifecycleStrategy;
import org.picocontainer.lifecycle.ReflectionLifecycleStrategy;
import org.picocontainer.lifecycle.StartableLifecycleStrategy;
import org.picocontainer.monitors.ConsoleComponentMonitor;
import org.picocontainer.monitors.NullComponentMonitor;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

import static org.picocontainer.behaviors.Behaviors.caching;
import static org.picocontainer.behaviors.Behaviors.implementationHiding;
import static org.picocontainer.injectors.Injectors.CDI;
import static org.picocontainer.injectors.Injectors.SDI;
import static org.picocontainer.injectors.Injectors.adaptiveDI;
import static org.picocontainer.injectors.Injectors.annotatedFieldDI;
import static org.picocontainer.injectors.Injectors.annotatedMethodDI;
import static org.picocontainer.injectors.Injectors.namedField;
import static org.picocontainer.injectors.Injectors.namedMethod;
import static org.picocontainer.injectors.Injectors.typedFieldDI;

/**
 * Helps assembles the myriad items available to a picocontainer.
 * <p>Simple Example:</p>
 * <pre>
 * MutablePicoContainer mpc = new PicoBuilder()
 * &nbsp;&nbsp;.withCaching()
 * &nbsp;&nbsp;.withLifecycle()
 * &nbsp;&nbsp;.build();
 * </pre>
 * @author Paul Hammant
 */
public class PicoBuilder {

    private PicoContainer parentContainer;
    private Class<? extends MutablePicoContainer> mpcClass = DefaultPicoContainer.class;
    private ComponentMonitor componentMonitor;
    private List<Object> containerComps = new ArrayList<Object>();
    private boolean addChildToParent;
    private LifecycleStrategy lifecycleStrategy;
    private final Stack<Object> behaviors = new Stack<Object>();
    private final List<InjectionFactory> injectors = new ArrayList<InjectionFactory>();
    private Class<? extends ComponentMonitor> componentMonitorClass = NullComponentMonitor.class;
    private Class<? extends LifecycleStrategy> lifecycleStrategyClass = NullLifecycleStrategy.class;


    public PicoBuilder(PicoContainer parentContainer, InjectionFactory injectionType) {
        this(parentContainer);
        addInjector(injectionType);
    }

    /**
     * Constructs a PicoBuilder using the specified PicoContainer as an argument.  Note
     * that this only creates child -&gt; parent references.  You must use  parentContainer.addChildContainer()
     * to the instance built here if you require child  &lt;-&gt; parent references. 
     * @param parentContainer
     */
    public PicoBuilder(PicoContainer parentContainer) {
        if (parentContainer != null) {
            this.parentContainer = parentContainer;
        } else {
            this.parentContainer = new EmptyPicoContainer();
        }
    }

    public PicoBuilder(InjectionFactory injectionType) {
        this(new EmptyPicoContainer(), injectionType);
    }

    /**
     * Will be used to build a PicoContainer not bound to any parent container.
     */
    public PicoBuilder() {
        this(new EmptyPicoContainer());
    }

    public PicoBuilder withLifecycle() {
        lifecycleStrategyClass = StartableLifecycleStrategy.class;
        lifecycleStrategy = null;
        return this;
    }

    /**
     * Constructed PicoContainer will use {@linkplain org.picocontainer.lifecycle.ReflectionLifecycleStrategy ReflectionLifecycle}.
     * @return <em>this</em> to allow for method chaining.
     */
    public PicoBuilder withReflectionLifecycle() {
        lifecycleStrategyClass = ReflectionLifecycleStrategy.class;
        lifecycleStrategy = null;
        return this;
    }

    /**
     * Allows you to specify your own lifecycle strategy class.
     * @param specifiedLifecycleStrategyType lifecycle strategy type.
     * @return <em>this</em> to allow for method chaining.
     */
    public PicoBuilder withLifecycle(Class<? extends LifecycleStrategy> specifiedLifecycleStrategyType) {
        this.lifecycleStrategyClass = specifiedLifecycleStrategyType;
        lifecycleStrategy = null;
        return this;
    }

    /**
     * Constructed PicoContainer will use {@linkplain org.picocontainer.lifecycle.JavaEE5LifecycleStrategy JavaEE5LifecycleStrategy}.
     * @return <em>this</em> to allow for method chaining.
     */    
    public PicoBuilder withJavaEE5Lifecycle() {
        this.lifecycleStrategyClass = JavaEE5LifecycleStrategy.class;
        lifecycleStrategy = null;
        return this;
    }

    /**
     * Allows you to fully specify your lifecycle strategy by passing in a built instance     
     * @param specifiedLifecycleStrategy
     * @return <em>this</em> to allow for method chaining.
     */
    public PicoBuilder withLifecycle(LifecycleStrategy specifiedLifecycleStrategy) {
        this.lifecycleStrategy = specifiedLifecycleStrategy;
        lifecycleStrategyClass = null;
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

        DefaultPicoContainer tempContainer = new TransientPicoContainer();
        tempContainer.addComponent(PicoContainer.class, parentContainer);

        addContainerComponents(tempContainer);

        ComponentFactory componentFactory;
        if (injectors.size() == 1) {
            componentFactory = injectors.get(0);
        } else if (injectors.size() == 0) {
            componentFactory = adaptiveDI();
        } else {
            componentFactory = new CompositeInjection(injectors.toArray(new InjectionFactory[injectors.size()]));
        }
        
        Stack<Object> clonedBehaviors = (Stack< Object >) behaviors.clone();
        while (!clonedBehaviors.empty()) {            
            componentFactory = buildComponentFactory(tempContainer, componentFactory, clonedBehaviors);
        }

        tempContainer.addComponent(ComponentFactory.class, componentFactory);

        buildComponentMonitor(tempContainer);

        if (lifecycleStrategy == null) {
            tempContainer.addComponent(LifecycleStrategy.class, lifecycleStrategyClass);
        } else {
            tempContainer.addComponent(LifecycleStrategy.class, lifecycleStrategy);

        }
        tempContainer.addComponent("mpc", mpcClass);

        MutablePicoContainer newContainer = (MutablePicoContainer) tempContainer.getComponent("mpc");

        addChildToParent(newContainer);
        return newContainer;
    }

    private void buildComponentMonitor(DefaultPicoContainer tempContainer) {
        if (componentMonitorClass == null) {
            tempContainer.addComponent(ComponentMonitor.class, componentMonitor);
        } else {
            tempContainer.addComponent(ComponentMonitor.class, componentMonitorClass);
        }
    }

    private void addChildToParent(MutablePicoContainer newContainer) {
        if (addChildToParent) {
            if (parentContainer instanceof MutablePicoContainer) {
                ((MutablePicoContainer)parentContainer).addChildContainer(newContainer);
            } else {
                throw new PicoCompositionException("If using addChildContainer() the parent must be a MutablePicoContainer");
            }
        }
    }

    private void addContainerComponents(DefaultPicoContainer temp) {
        for (Object containerComp : containerComps) {
            temp.addComponent(containerComp);
        }
    }

    private ComponentFactory buildComponentFactory(DefaultPicoContainer container, final ComponentFactory lastCaf, final Stack<Object> clonedBehaviors) {
       
        Object componentFactory = clonedBehaviors.pop();
        DefaultPicoContainer tmpContainer = new TransientPicoContainer(container);
        tmpContainer.addComponent("componentFactory", componentFactory);
        if (lastCaf != null) {
            tmpContainer.addComponent(ComponentFactory.class, lastCaf);
        }
        ComponentFactory newlastCaf = (ComponentFactory) tmpContainer.getComponent("componentFactory");
        if (newlastCaf instanceof BehaviorFactory) {
            ((BehaviorFactory) newlastCaf).wrap(lastCaf);
        }
        return newlastCaf;
    }

    public PicoBuilder withHiddenImplementations() {
        behaviors.push(implementationHiding());
        return this;
    }

    public PicoBuilder withSetterInjection() {
        addInjector(SDI());
        return this;
    }

    public PicoBuilder withAnnotatedMethodInjection(Class<? extends Annotation> injectionAnnotation) {
        addInjector(annotatedMethodDI(injectionAnnotation));
        return this;
    }

    public PicoBuilder withAnnotatedMethodInjection() {
        addInjector(annotatedMethodDI());
        return this;
    }

    public PicoBuilder withAnnotatedFieldInjection(Class<? extends Annotation> injectionAnnotation) {
        addInjector(annotatedFieldDI(injectionAnnotation));
        return this;
    }

    public PicoBuilder withAnnotatedFieldInjection() {
        addInjector(annotatedFieldDI());
        return this;
    }

    public PicoBuilder withTypedFieldInjection() {
        addInjector(typedFieldDI());
        return this;
    }

    public PicoBuilder withConstructorInjection() {
        addInjector(CDI());
        return this;
    }

    public PicoBuilder withNamedMethodInjection() {
        addInjector(namedMethod());
        return this;
    }

    public PicoBuilder withNamedFieldInjection() {
        addInjector(namedField());
        return this;
    }

    public PicoBuilder withCaching() {
        behaviors.push(caching());
        return this;
    }

    public PicoBuilder withComponentFactory(ComponentFactory componentFactory) {
        if (componentFactory == null) {
            throw new NullPointerException("CAF cannot be null");
        }
        behaviors.push(componentFactory);
        return this;
    }

    public PicoBuilder withSynchronizing() {
        behaviors.push(new Synchronizing());
        return this;
    }

    public PicoBuilder withLocking() {
        behaviors.push(new Locking());
        return this;
    }

    public PicoBuilder withBehaviors(BehaviorFactory... factories) {
        for (BehaviorFactory componentFactory : factories) {
            behaviors.push(componentFactory);
        }
        return this;
    }

    public PicoBuilder implementedBy(Class<? extends MutablePicoContainer> containerClass) {
        mpcClass = containerClass;
        return this;
    }

    /**
     * Allows you to specify your very own component monitor to be used by the created
     * picocontainer
     * @param specifiedComponentMonitor
     * @return <em>this</em> to allow for method chaining.
     */
    public PicoBuilder withMonitor(ComponentMonitor specifiedComponentMonitor) {
        this.componentMonitor = specifiedComponentMonitor;
        componentMonitorClass = null;
        return this;
    }

    public PicoBuilder withComponentFactory(Class<? extends ComponentFactory> componentFactoryClass) {
        behaviors.push(componentFactoryClass);
        return this;
    }

    public PicoBuilder withCustomContainerComponent(Object containerDependency) {
        containerComps.add(containerDependency);
        return this;
    }

    public PicoBuilder withPropertyApplier() {
        behaviors.push(new PropertyApplying());
        return this;
    }

    public PicoBuilder withAutomatic() {
        behaviors.push(new Automating());
        return this;
    }

    public PicoBuilder withMethodInjection() {
        addInjector(new MethodInjection());
        return this;
    }

    public PicoBuilder addChildToParent() {
        addChildToParent =  true;
        return this;
    }

    protected void addInjector(InjectionFactory injectionType) {
        injectors.add(injectionType);
    }
}
