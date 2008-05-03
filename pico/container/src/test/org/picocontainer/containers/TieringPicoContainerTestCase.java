package org.picocontainer.containers;

import org.junit.Test;
import static org.junit.Assert.fail;
import static org.junit.Assert.assertNotNull;
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
    public void testThatGrandparentTraversalForComponentsCanBeBlocked() {
        MutablePicoContainer grandparent = new TieringPicoContainer();
        MutablePicoContainer parent = grandparent.makeChildContainer();
        MutablePicoContainer child = parent.makeChildContainer();
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

    @Test
    public void testThatParentTraversalIsOkForTiering() {
        MutablePicoContainer parent = new TieringPicoContainer();
        MutablePicoContainer  child = parent.makeChildContainer();
        parent.addComponent(Couch.class);
        child.addComponent(TiredPerson.class);

        TiredPerson tp = child.getComponent(TiredPerson.class);
        assertNotNull(tp);
        assertNotNull(tp.couchToSitOn);

    }
}
