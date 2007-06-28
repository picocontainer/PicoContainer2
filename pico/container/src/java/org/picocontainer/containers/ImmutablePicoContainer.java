package org.picocontainer.containers;

import org.picocontainer.PicoContainer;
import org.picocontainer.ComponentAdapter;
import org.picocontainer.PicoVisitor;

import java.util.List;
import java.util.Collection;
import java.io.Serializable;

/**
 * Empty pico container serving as recoil damper in situations where you
 * do not like to check whether container reference suplpied to you
 * is null or not
 *
 * Typically its used to merk a parent container.
 *
 * @author Konstantin Pribluda
 * @since 1.1
 */
public class ImmutablePicoContainer implements PicoContainer, Serializable {

    private final PicoContainer delegate;

    public ImmutablePicoContainer(PicoContainer delegate) {
        if (delegate == null) {
            throw new NullPointerException();
        }
        this.delegate = delegate;
    }

    public Object getComponent(Object componentKeyOrType) {
        return delegate.getComponent(componentKeyOrType);
    }

    public <T> T getComponent(Class<T> componentType) {
        return delegate.getComponent(componentType);
    }

    public List getComponents() {
        return delegate.getComponents();
    }

    public PicoContainer getParent() {
        return delegate.getParent();
    }

    public ComponentAdapter<?> getComponentAdapter(Object componentKey) {
        return delegate.getComponentAdapter(componentKey);
    }

    public <T> ComponentAdapter<T> getComponentAdapter(Class<T> componentType) {
        return delegate.getComponentAdapter(componentType);
    }

    public Collection<ComponentAdapter<?>> getComponentAdapters() {
        return delegate.getComponentAdapters();
    }

    public <T> List<ComponentAdapter<T>> getComponentAdapters(Class<T> componentType) {
        return delegate.getComponentAdapters(componentType);
    }

    public <T> List<T> getComponents(Class<T> componentType) {
        return delegate.getComponents(componentType);
    }

    public void accept(PicoVisitor visitor) {
        delegate.accept(visitor);
    }

    public boolean equals(Object obj) {
        return obj == this
               || (obj != null && obj == delegate)
               || (obj instanceof ImmutablePicoContainer && ((ImmutablePicoContainer) obj).delegate == delegate)
            ;
    }


    public int hashCode() {
        return delegate.hashCode();
    }
}
