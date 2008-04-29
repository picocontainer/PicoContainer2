package org.picocontainer.containers;

import org.junit.Test;
import static org.junit.Assert.fail;
import org.picocontainer.MutablePicoContainer;
import org.picocontainer.DefaultPicoContainer;
import org.picocontainer.injectors.AbstractInjector;

public class TieringPicoContainerTestCase {
    
    public static class Couch {
    }

    public static class TiredPerson {
        private Couch couchToSitOn;

        public TiredPerson(Couch couchToSitOn) {
            this.couchToSitOn = couchToSitOn;
        }
    }

    @Test
    public void testThatParentTraversalForComponentsCanBeBlocked() {
        MutablePicoContainer grandparent = new TieringPicoContainer();
        MutablePicoContainer parent = (DefaultPicoContainer) grandparent.makeChildContainer();
        MutablePicoContainer child = (DefaultPicoContainer) parent.makeChildContainer();
        grandparent.addComponent(Couch.class);
        child.addComponent(TiredPerson.class);

        TiredPerson tp = null;
        try {
            tp = child.getComponent(TiredPerson.class);
            fail("should have barfed");
        } catch (AbstractInjector.UnsatisfiableDependenciesException e) {
            System.out.println("");
            // expected
        }

    }
}
