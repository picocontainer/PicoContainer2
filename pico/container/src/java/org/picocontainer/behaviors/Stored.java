package org.picocontainer.behaviors;

import org.picocontainer.ComponentAdapter;
import org.picocontainer.LifecycleStrategy;
import org.picocontainer.ObjectReference;
import org.picocontainer.PicoCompositionException;
import org.picocontainer.PicoContainer;

import java.lang.reflect.Type;
import java.io.Serializable;

/**
 * behaviour for all behaviours wishing to store
 * their component in "awkward places" ( object references )
 * @author Konstantin Pribluda
 *
 * @param <T>
 */
@SuppressWarnings("serial")
public class Stored<T> extends AbstractBehavior<T> {

	protected final boolean delegateHasLifecylce;
	protected final ObjectReference<InstHolder<T>> instanceReference;

    public static class InstHolder<T> implements Serializable {
        private T instance;
        protected boolean started;
        protected boolean disposed;
    }

    public Stored(ComponentAdapter<T> delegate, ObjectReference<InstHolder<T>> reference) {
		super(delegate);
		instanceReference = reference;
        this.delegateHasLifecylce = delegate instanceof LifecycleStrategy
        && ((LifecycleStrategy) delegate).hasLifecycle(delegate.getComponentImplementation());

	}

    private void guardInstRef() {
        if (instanceReference.get() == null) {
            instanceReference.set(new InstHolder());
        }
    }

    public boolean componentHasLifecycle() {
	    return delegateHasLifecylce;
	}

	/**
	 * Disposes the cached component instance
	 * {@inheritDoc}
	 */
	public void dispose(PicoContainer container) {
        guardInstRef();
        if ( delegateHasLifecylce ){
            guardInstRef();
	        if (instanceReference.get().disposed) throw new IllegalStateException("'" + getComponentKey() + "' already disposed");
	        dispose(getComponentInstance(container, NOTHING.class));
	        instanceReference.get().disposed = true;
	    }
	}

	/**
	 * Retrieves the stored reference.  May be null if it has
	 * never been set, or possibly if the reference has been
	 * flushed.
	 * @return the stored object or null.
	 */
	public T getStoredObject() {
        guardInstRef();
        return instanceReference.get().instance;
	}

	/**
	 * Flushes the cache.
	 * If the component instance is started is will stop and dispose it before
	 * flushing the cache.
	 */
	public void flush() {
        InstHolder<T> instHolder = instanceReference.get();
        if (instHolder != null) {
            Object instance = instHolder.instance;
	        if ( instance != null && delegateHasLifecylce && instanceReference.get().started ) {
	            stop(instance);
	            dispose(instance);
	        }
	        instanceReference.set(null);
        }
    }

	public T getComponentInstance(PicoContainer container, Type into) throws PicoCompositionException {
        guardInstRef();
        T instance = instanceReference.get().instance;
	    if (instance == null) {
	        instance = super.getComponentInstance(container, into);
            instanceReference.get().instance = instance;
	    }
	    return instance;
	}

    public String getDescriptor() {
        return "Stored";
    }

    /**
	 * Starts the cached component instance
	 * {@inheritDoc}
	 */
	public void start(PicoContainer container) {
        guardInstRef();
        if ( delegateHasLifecylce ){
	        if (instanceReference.get().disposed) throw new IllegalStateException("'" + getComponentKey() + "' already disposed");
	        if (instanceReference.get().started) throw new IllegalStateException("'" + getComponentKey() + "' already started");
	        start(getComponentInstance(container, NOTHING.class));
	        instanceReference.get().started = true;
	    }
	}

	/**
	 * Stops the cached component instance
	 * {@inheritDoc}
	 */
	public void stop(PicoContainer container) {
        guardInstRef();
        if ( delegateHasLifecylce ){
	        if (instanceReference.get().disposed) throw new IllegalStateException("'" + getComponentKey() + "' already disposed");
	        if (!instanceReference.get().started) throw new IllegalStateException("'" + getComponentKey() + "' not started");
	        stop(getComponentInstance(container, NOTHING.class));
	        instanceReference.get().started = false;
	    }
	}

}
