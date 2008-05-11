package org.picocontainer.injectors;

import org.picocontainer.InjectionFactory;
import org.picocontainer.PicoContainer;
import org.picocontainer.PicoVisitor;

import java.io.Serializable;

public abstract class AbstractInjectionFactory implements InjectionFactory, Serializable {

    private static final long serialVersionUID = 7771364390347050589L;

    public void verify(PicoContainer container) {
    }

    public final void accept(PicoVisitor visitor) {
        visitor.visitComponentFactory(this);
    }

}
