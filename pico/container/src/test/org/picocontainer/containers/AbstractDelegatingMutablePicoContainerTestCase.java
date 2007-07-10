package org.picocontainer.containers;

import org.picocontainer.tck.AbstractPicoContainerTestCase;
import org.picocontainer.MutablePicoContainer;
import org.picocontainer.PicoContainer;
import org.picocontainer.DefaultPicoContainer;

public class AbstractDelegatingMutablePicoContainerTestCase extends AbstractPicoContainerTestCase {


    protected MutablePicoContainer createPicoContainer(PicoContainer parent) {
        return new MyAbstractDelegatingMutablePicoContainer(new DefaultPicoContainer());
    }

    private static class MyAbstractDelegatingMutablePicoContainer extends AbstractDelegatingMutablePicoContainer {
        public MyAbstractDelegatingMutablePicoContainer(MutablePicoContainer parent) {
            super(parent);
        }

        public MutablePicoContainer makeChildContainer() {
            return new MyAbstractDelegatingMutablePicoContainer(this);
        }
    }


    public void testAcceptImplementsBreadthFirstStrategy() {
        // don;t run this one.
    }
}
