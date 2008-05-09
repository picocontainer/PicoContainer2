package org.picocontainer.containers;

import org.picocontainer.*;

import java.lang.annotation.Annotation;

public class TieringPicoContainer extends DefaultPicoContainer {

    /**
     * Creates a new container with a custom ComponentFactory, LifecycleStrategy for instance registration,
     * and a parent container.
     * <em>
     * Important note about caching: If you intend the components to be cached, you should pass
     * in a factory that creates {@link Cached} instances, such as for example
     * {@link Caching}. Caching can delegate to other ComponentAdapterFactories.
     * </em>
     *
     * @param componentFactory the factory to use for creation of ComponentAdapters.
     * @param lifecycleStrategy
     *                                the lifecycle strategy chosen for registered
     *                                instance (not implementations!)
     * @param parent                  the parent container (used for component dependency lookups).
     */
    public TieringPicoContainer(final ComponentFactory componentFactory, final LifecycleStrategy lifecycleStrategy,
                                final PicoContainer parent) {
        super(componentFactory, lifecycleStrategy, parent);
    }

    public TieringPicoContainer(final ComponentFactory componentFactory, final LifecycleStrategy lifecycleStrategy,
                                final PicoContainer parent, final ComponentMonitor componentMonitor) {
        super(componentFactory, lifecycleStrategy, parent, componentMonitor);
    }

    /**
     * Creates a new container with the AdaptingInjection using a
     * custom ComponentMonitor
     *
     * @param monitor the ComponentMonitor to use
     * @param parent  the parent container (used for component dependency lookups).
     */
    public TieringPicoContainer(final ComponentMonitor monitor, final PicoContainer parent) {
        super(monitor, parent);
    }

    /**
     * Creates a new container with the AdaptingInjection using a
     * custom ComponentMonitor and lifecycle strategy
     *
     * @param monitor           the ComponentMonitor to use
     * @param lifecycleStrategy the lifecycle strategy to use.
     * @param parent            the parent container (used for component dependency lookups).
     */
    public TieringPicoContainer(final ComponentMonitor monitor, final LifecycleStrategy lifecycleStrategy,
                                final PicoContainer parent) {
        super(monitor, lifecycleStrategy, parent);
    }

    /**
     * Creates a new container with the AdaptingInjection using a
     * custom lifecycle strategy
     *
     * @param lifecycleStrategy the lifecycle strategy to use.
     * @param parent            the parent container (used for component dependency lookups).
     */
    public TieringPicoContainer(final LifecycleStrategy lifecycleStrategy, final PicoContainer parent) {
        super(lifecycleStrategy, parent);
    }


    /**
     * Creates a new container with a custom ComponentFactory and no parent container.
     *
     * @param componentFactory the ComponentFactory to use.
     */
    public TieringPicoContainer(final ComponentFactory componentFactory) {
        super(componentFactory);
    }

    /**
     * Creates a new container with the AdaptingInjection using a
     * custom ComponentMonitor
     *
     * @param monitor the ComponentMonitor to use
     */
    public TieringPicoContainer(final ComponentMonitor monitor) {
        super(monitor);
    }

    /**
     * Creates a new container with a (caching) {@link AdaptingInjection}
     * and a parent container.
     *
     * @param parent the parent container (used for component dependency lookups).
     */
    public TieringPicoContainer(final PicoContainer parent) {
        super(parent);
    }

    /** Creates a new container with a {@link AdaptingBehavior} and no parent container. */
    public TieringPicoContainer() {
        super();
    }

    public PicoContainer getParent() {
        return new TieringGuard(super.getParent());
    }

    public MutablePicoContainer makeChildContainer() {
        return new TieringPicoContainer(super.componentFactory, super.lifecycleStrategy, this, super.componentMonitor);
    }

    private static class TieringGuard extends AbstractDelegatingPicoContainer {

        private static final AskingParentForComponent askingParentForComponent = new AskingParentForComponent();

        public TieringGuard(PicoContainer parent) {
            super(parent);
        }

        public <T> ComponentAdapter<T> getComponentAdapter(Class<T> componentType, NameBinding componentNameBinding) {
            boolean iDidIt = false;
            try {
                if (notYetAskingParentForComponent()) {
                    nowAskingParentForComponent();
                    iDidIt = true;
                    return super.getComponentAdapter(componentType, componentNameBinding);
                } else {
                    return null;
                }
            } finally {
                if (iDidIt) {
                    doneAskingParentForComponent();
                }
            }
        }

        private <T> void nowAskingParentForComponent() {
            askingParentForComponent.set(Boolean.TRUE);
        }

        public <T> ComponentAdapter<T> getComponentAdapter(Class<T> componentType, Class<? extends Annotation> binding) {
            boolean iDidIt = false;
            try {
                if (notYetAskingParentForComponent()) {
                    nowAskingParentForComponent();
                    iDidIt = true;
                    return super.getComponentAdapter(componentType, binding);
                } else {
                    return null;
                }
            } finally {
                if (iDidIt) {
                    doneAskingParentForComponent();
                }
            }
        }

        private <T> void doneAskingParentForComponent() {
            askingParentForComponent.set(Boolean.FALSE);
        }

        private <T> boolean notYetAskingParentForComponent() {
            return askingParentForComponent.get() == Boolean.FALSE;
        }

        public ComponentAdapter<?> getComponentAdapter(Object componentKey) {
            boolean iDidIt = false;
            try {
                if (notYetAskingParentForComponent()) {
                    nowAskingParentForComponent();
                    iDidIt = true;
                    return super.getComponentAdapter(componentKey);
                } else {
                    return null;
                }
            } finally {
                if (iDidIt) {
                    doneAskingParentForComponent();
                }
            }
        }
    }
    private static class AskingParentForComponent extends ThreadLocal {
        protected Object initialValue() {
            return Boolean.FALSE;
        }
    }
}