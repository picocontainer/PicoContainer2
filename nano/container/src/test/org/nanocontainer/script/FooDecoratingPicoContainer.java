package org.nanocontainer.script;

import org.picocontainer.containers.AbstractDelegatingMutablePicoContainer;
import org.picocontainer.MutablePicoContainer;
import org.picocontainer.Parameter;
import org.picocontainer.PicoCompositionException;

import java.util.HashMap;
import java.util.ArrayList;

import junit.framework.Assert;

public class FooDecoratingPicoContainer extends AbstractDelegatingMutablePicoContainer {
    public FooDecoratingPicoContainer(MutablePicoContainer delegate) {
        super(delegate);
    }
    public MutablePicoContainer makeChildContainer() {
        return null;
    }

    public MutablePicoContainer addComponent(Object componentKey, Object componentImplementationOrInstance, Parameter... parameters) throws PicoCompositionException {
        Assert.assertEquals(HashMap.class, componentImplementationOrInstance);
        return super.addComponent(ArrayList.class, ArrayList.class, parameters);
    }

}
