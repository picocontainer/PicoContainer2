package org.picocontainer.containers;

import org.picocontainer.tck.AbstractPicoContainerTestCase;
import org.picocontainer.MutablePicoContainer;
import org.picocontainer.PicoContainer;
import org.picocontainer.DefaultPicoContainer;
import org.picocontainer.Characteristics;

import java.util.Properties;

public class DelegatingMutablePicoContainerTestCase extends AbstractPicoContainerTestCase {

    protected MutablePicoContainer createPicoContainer(PicoContainer parent) {
        return new MyDelegatingMutablePicoContainer(new DefaultPicoContainer());
    }

    protected Properties[] getProperties() {
        return new Properties[] { Characteristics.NO_CACHE, Characteristics.NO_HIDE_IMPL};
    }

    private static class MyDelegatingMutablePicoContainer extends AbstractDelegatingMutablePicoContainer {
        public MyDelegatingMutablePicoContainer(MutablePicoContainer parent) {
            super(parent);
        }

        public MutablePicoContainer makeChildContainer() {
            return new MyDelegatingMutablePicoContainer(this);
        }
    }


    public void testAcceptImplementsBreadthFirstStrategy() {
        // don't run this one.
    }

}
