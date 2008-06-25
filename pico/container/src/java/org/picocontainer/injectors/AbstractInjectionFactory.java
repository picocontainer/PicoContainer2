package org.picocontainer.injectors;

import org.picocontainer.InjectionFactory;
import org.picocontainer.PicoContainer;
import org.picocontainer.PicoVisitor;

import java.io.Serializable;

public abstract class AbstractInjectionFactory implements InjectionFactory, Serializable {

    public void verify(PicoContainer container) {
    }

    public final void accept(PicoVisitor visitor) {
        visitor.visitComponentFactory(this);
    }

}
