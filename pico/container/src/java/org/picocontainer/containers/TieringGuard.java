package org.picocontainer.containers;

import org.picocontainer.PicoContainer;
import org.picocontainer.ComponentAdapter;
import org.picocontainer.NameBinding;

import java.lang.annotation.Annotation;

public class TieringGuard extends AbstractDelegatingPicoContainer {

    private static final AskingParentForComponent askingParentForComponent = new AskingParentForComponent();

    public TieringGuard(PicoContainer parent) {
        super(parent);
    }

    public <T> T getComponent(Class<T> componentType) {
        boolean iDidIt = false;
        try {
            if (askingParentForComponent.get() == Boolean.FALSE) {
                askingParentForComponent.set(Boolean.TRUE);
                iDidIt = true;
            } else if (askingParentForComponent.get() == Boolean.TRUE) {
                return null;
            }
            return super.getComponent(componentType);
        } finally {
            if (iDidIt) {
                askingParentForComponent.set(Boolean.FALSE);
            }
        }
    }

    public <T> T getComponent(Class<T> componentType, Class<? extends Annotation> binding) {
        boolean iDidIt = false;
        try {
            if (askingParentForComponent.get() == Boolean.FALSE) {
                askingParentForComponent.set(Boolean.TRUE);
                iDidIt = true;
            } else if (askingParentForComponent.get() == Boolean.TRUE) {
                return null;
            }
            return super.getComponent(componentType, binding);
        } finally {
            if (iDidIt) {
                askingParentForComponent.set(Boolean.FALSE);
            }
        }
    }

    public <T> ComponentAdapter<T> getComponentAdapter(Class<T> componentType, NameBinding componentNameBinding) {
        boolean iDidIt = false;
        try {
            if (askingParentForComponent.get() == Boolean.FALSE) {
                askingParentForComponent.set(Boolean.TRUE);
                iDidIt = true;
            } else if (askingParentForComponent.get() == Boolean.TRUE) {
                return null;
            }

        return super.getComponentAdapter(componentType, componentNameBinding);
        } finally {
            if (iDidIt) {
                askingParentForComponent.set(Boolean.FALSE);
            }
        }
    }

    public Object getComponent(Object componentKeyOrType) {
        boolean iDidIt = false;
        try {
            if (askingParentForComponent.get() == Boolean.FALSE) {
                askingParentForComponent.set(Boolean.TRUE);
                iDidIt = true;
            } else if (askingParentForComponent.get() == Boolean.TRUE) {
                return null;
            }
            return super.getComponent(componentKeyOrType);
        } finally {
            if (iDidIt) {
                askingParentForComponent.set(Boolean.FALSE);
            }
        }
    }

    public <T> ComponentAdapter<T> getComponentAdapter(Class<T> componentType, Class<? extends Annotation> binding) {
        boolean iDidIt = false;
        try {
            if (askingParentForComponent.get() == Boolean.FALSE) {
                askingParentForComponent.set(Boolean.TRUE);
                iDidIt = true;
            } else if (askingParentForComponent.get() == Boolean.TRUE) {
                return null;
            }
            return super.getComponentAdapter(componentType, binding);
        } finally {
            if (iDidIt) {
                askingParentForComponent.set(Boolean.FALSE);
            }
        }
    }

    public ComponentAdapter<?> getComponentAdapter(Object componentKey) {
        boolean iDidIt = false;
        try {
            if (askingParentForComponent.get() == Boolean.FALSE) {
                askingParentForComponent.set(Boolean.TRUE);
                iDidIt = true;
            } else if (askingParentForComponent.get() == Boolean.TRUE) {
                return null;
            }
            return super.getComponentAdapter(componentKey);
        } finally {
            if (iDidIt) {
                askingParentForComponent.set(Boolean.FALSE);
            }
        }
    }

    public static class AskingParentForComponent extends ThreadLocal {
        protected Object initialValue() {
            return Boolean.FALSE;
        }
    }

}