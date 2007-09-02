/*****************************************************************************
 * Copyright (C) PicoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the LICENSE.txt file.                                                     *
 *****************************************************************************/

package org.picocontainer.gems.constraints;

import org.picocontainer.MutablePicoContainer;
import org.picocontainer.PicoVisitor;
import org.picocontainer.DefaultPicoContainer;
import org.picocontainer.behaviors.Caching;
import org.picocontainer.injectors.AbstractInjector;
import org.picocontainer.testmodel.AlternativeTouchable;
import org.picocontainer.testmodel.DecoratedTouchable;
import org.picocontainer.testmodel.DependsOnTouchable;
import org.picocontainer.testmodel.SimpleTouchable;
import org.picocontainer.testmodel.Touchable;

import org.jmock.Mock;
import org.jmock.MockObjectTestCase;

/**
 * Test some <code>Constraint</code>s.
 *
 * @author Nick Sieger
 * @author J&ouml;rg Schaible
 */
public class ConstraintsTestCase extends MockObjectTestCase {

    MutablePicoContainer container;

    protected void setUp() throws Exception {
        super.setUp();
        
        container = new DefaultPicoContainer(new Caching());
        container.addComponent(SimpleTouchable.class);
        container.addComponent(DecoratedTouchable.class);
        container.addComponent(AlternativeTouchable.class);
        container.addComponent(DependsOnTouchable.class);
    }

    public void testIsKeyConstraint() {
        Constraint c = new IsKey(SimpleTouchable.class);

        Object object = c.resolveInstance(container, 
                container.getComponentAdapter(DependsOnTouchable.class, null),
                Touchable.class, null, false);
        assertEquals(SimpleTouchable.class, object.getClass());
    }

    public void testIsTypeConstraint() {
        Constraint c = new IsType(AlternativeTouchable.class);

        Object object = c.resolveInstance(container, 
                container.getComponentAdapter(DependsOnTouchable.class, null),
                Touchable.class, null, false);
        assertEquals(AlternativeTouchable.class, object.getClass());
    }

    public void testIsKeyTypeConstraint() {
        container.addComponent("Simple", SimpleTouchable.class);
        container.addComponent(5, SimpleTouchable.class);
        container.addComponent(Boolean.TRUE, SimpleTouchable.class);
        Touchable t = (Touchable) container.getComponent(Boolean.TRUE);
        
        Constraint c = new IsKeyType(Boolean.class);

        assertSame(t, c.resolveInstance(container, 
                container.getComponentAdapter(DependsOnTouchable.class, null),
                Touchable.class, null, false));
    }

    public void testConstraintTooBroadThrowsAmbiguityException() {
        Constraint c = new IsType(Touchable.class);

        try {
            c.resolveInstance(container, 
                    container.getComponentAdapter(DependsOnTouchable.class, null),
                    Touchable.class, null, false);
            fail("did not throw ambiguous resolution exception");
        } catch (AbstractInjector.AmbiguousComponentResolutionException acre) {
            // success
        }
    }

    public void testFindCandidateConstraintsExcludingOneImplementation() {
        Constraint c = 
            new CollectionConstraint(
                new And(new IsType(Touchable.class),
                new Not(new IsType(DecoratedTouchable.class))));
        Touchable[] touchables = (Touchable[]) c.resolveInstance(container, 
                container.getComponentAdapter(DependsOnTouchable.class, null),
                Touchable[].class, null, false);
        assertEquals(2, touchables.length);
        for (Touchable touchable : touchables) {
            assertFalse(touchable instanceof DecoratedTouchable);
        }
    }
    
    public void testCollectionChildIdVisitedBreadthFirst() {
        Mock             mockVisior = mock(PicoVisitor.class);
        PicoVisitor     visitor = (PicoVisitor) mockVisior.proxy();

        Mock       mockC1 = mock(Constraint.class, "constraint 1");
        Constraint c1     = (Constraint) mockC1.proxy();

        Constraint c = new CollectionConstraint(c1);
        
        mockVisior.expects(once()).method("visitParameter")
            .with(same(c)).id("v");
        mockC1.expects(once()).method("accept")
            .with(same(visitor)).after(mockVisior, "v");
        
        c.accept(visitor);
    }
}
