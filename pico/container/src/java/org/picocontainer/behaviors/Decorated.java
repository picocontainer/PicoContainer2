package org.picocontainer.behaviors;

import org.picocontainer.ComponentAdapter;
import org.picocontainer.PicoContainer;
import org.picocontainer.PicoCompositionException;
import org.picocontainer.behaviors.AbstractBehavior;

import java.lang.reflect.Type;

public class Decorated extends AbstractBehavior {
    private final Decorator decorator;
    private static final long serialVersionUID = 438844637809706077L;

    public Decorated(ComponentAdapter delegate, Decorator decorator) {
        super(delegate);
        this.decorator = decorator;
    }

    public Object getComponentInstance(final PicoContainer container, Type into)
            throws PicoCompositionException {
        Object instance = super.getComponentInstance(container, into);
        decorator.decorate(instance);
        return instance;
    }


    public String getDescriptor() {
        return "FieldDecorated";
    }

    public interface Decorator {

        void decorate(Object instance);


    }

}
