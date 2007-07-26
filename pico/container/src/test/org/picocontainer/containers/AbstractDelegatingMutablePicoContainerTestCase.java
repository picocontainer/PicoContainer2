package org.picocontainer.containers;

import org.picocontainer.tck.AbstractPicoContainerTestCase;
import org.picocontainer.MutablePicoContainer;
import org.picocontainer.PicoContainer;
import org.picocontainer.DefaultPicoContainer;
import org.picocontainer.Characteristics;

import java.util.Properties;

public class AbstractDelegatingMutablePicoContainerTestCase extends AbstractPicoContainerTestCase {


    protected MutablePicoContainer createPicoContainer(PicoContainer parent) {
        return new MyAbstractDelegatingMutablePicoContainer(new DefaultPicoContainer());
    }

    protected Properties[] getProperties() {
        return new Properties[] { Characteristics.NO_CACHE, Characteristics.NO_HIDE_IMPL};
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
