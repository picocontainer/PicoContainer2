package org.picocontainer.injectors;

import org.picocontainer.ComponentAdapter;
import org.picocontainer.ComponentMonitor;
import org.picocontainer.ComponentMonitorStrategy;
import org.picocontainer.InjectionFactory;
import org.picocontainer.Injector;
import org.picocontainer.LifecycleStrategy;
import org.picocontainer.PicoCompositionException;
import org.picocontainer.PicoContainer;
import org.picocontainer.PicoVisitor;
import org.picocontainer.lifecycle.NullLifecycleStrategy;

import java.io.Serializable;
import java.lang.reflect.Type;

public abstract class AbstractInjectionFactory implements InjectionFactory, Serializable {

    public void verify(PicoContainer container) {
    }

    public final void accept(PicoVisitor visitor) {
        visitor.visitComponentFactory(this);
    }

    protected ComponentAdapter wrapLifeCycle(final Injector injector, LifecycleStrategy lifecycleStrategy) {
        if (lifecycleStrategy instanceof NullLifecycleStrategy) {
            return injector;
        } else {
            return new LifecycleAdapter(injector, lifecycleStrategy);
        }
    }

    private static class LifecycleAdapter implements Injector, LifecycleStrategy, ComponentMonitorStrategy, Serializable {
        private final Injector delegate;
        private final LifecycleStrategy lifecycleStrategy;

        public LifecycleAdapter(Injector delegate, LifecycleStrategy lifecycleStrategy) {
            this.delegate = delegate;
            this.lifecycleStrategy = lifecycleStrategy;
        }

        public Object getComponentKey() {
            return delegate.getComponentKey();
        }

        public Class getComponentImplementation() {
            return delegate.getComponentImplementation();
        }

        public Object getComponentInstance(PicoContainer container) throws PicoCompositionException {
            return delegate.getComponentInstance(container);
        }

        public Object getComponentInstance(PicoContainer container, Type into) throws PicoCompositionException {
            return delegate.getComponentInstance(container, into);
        }

        public void verify(PicoContainer container) throws PicoCompositionException {
            delegate.verify(container);
        }

        public void accept(PicoVisitor visitor) {
            delegate.accept(visitor);
        }

        public ComponentAdapter getDelegate() {
            return delegate;
        }

        public ComponentAdapter findAdapterOfType(Class adapterType) {
            return delegate.findAdapterOfType(adapterType);
        }

        public String getDescriptor() {
            return "LifecycleAdapter";
        }

        public String toString() {
            return getDescriptor() + ":" + delegate.toString();
        }

        public void start(Object component) {
            lifecycleStrategy.start(component);
        }

        public void stop(Object component) {
            lifecycleStrategy.stop(component);
        }

        public void dispose(Object component) {
            lifecycleStrategy.dispose(component);
        }

        public boolean hasLifecycle(Class<?> type) {
            return lifecycleStrategy.hasLifecycle(type);
        }

        public boolean isLazy(ComponentAdapter<?> adapter) {
            return lifecycleStrategy.isLazy(adapter);
        }

        public void changeMonitor(ComponentMonitor monitor) {
            if (delegate instanceof ComponentMonitorStrategy) {
                ((ComponentMonitorStrategy) delegate).changeMonitor(monitor);
            }
        }

        public ComponentMonitor currentMonitor() {
            if (delegate instanceof ComponentMonitorStrategy) {
                return ((ComponentMonitorStrategy) delegate).currentMonitor();
            }
            return null;
        }

        public Object decorateComponentInstance(PicoContainer container, Type into, Object instance) {
            return delegate.decorateComponentInstance(container, into, instance);
        }
    }
}
