/*****************************************************************************
 * Copyright (C) PicoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the LICENSE.txt file.                                                     *
 *****************************************************************************/

package org.picocontainer.gems.constraints;

import org.jmock.Mock;
import org.jmock.MockObjectTestCase;
import org.picocontainer.ComponentAdapter;
import org.picocontainer.PicoVisitor;
import org.picocontainer.gems.constraints.And;
import org.picocontainer.gems.constraints.Constraint;
import org.picocontainer.gems.constraints.Not;
import org.picocontainer.gems.constraints.Or;

/**
 * Test the <code>And, Or, Not</code> constraints.
 *
 * @author Nick Sieger
 * @author J&ouml;rg Schaible
 * @version 1.1
 */
public final class AndOrNotTestCase extends MockObjectTestCase {

    final Mock             mockAdapter = mock(ComponentAdapter.class);
    final ComponentAdapter adapter     = (ComponentAdapter) mockAdapter.proxy();
    final Mock             mockVisior = mock(PicoVisitor.class);
    final PicoVisitor     visitor = (PicoVisitor) mockVisior.proxy();

    final Mock       mockC1 = mock(Constraint.class, "constraint 1");
    final Mock       mockC2 = mock(Constraint.class, "constraint 2");
    final Mock       mockC3 = mock(Constraint.class, "constraint 3");
    final Constraint c1     = (Constraint) mockC1.proxy();
    final Constraint c2     = (Constraint) mockC2.proxy();
    final Constraint c3     = (Constraint) mockC3.proxy();

    public void testAndAllChildrenConstraintsTrueGivesTrue() {
        Constraint c = new And(c1, c2, c3);

        mockC1.expects(once()).method("evaluate")
            .with(same(adapter))
            .will(returnValue(Boolean.TRUE)).id("c1");
        mockC2.expects(once()).method("evaluate")
            .with(same(adapter)).after(mockC1, "c1")
            .will(returnValue(Boolean.TRUE)).id("c2");
        mockC3.expects(once()).method("evaluate")
            .with(same(adapter)).after(mockC2, "c2")
            .will(returnValue(Boolean.TRUE));

        assertTrue(c.evaluate(adapter));
    }
    
    public void testAndAllChildrenAreVisitedBreadthFirst() {
        Constraint c = new And(c1, c2, c3);
        
        mockVisior.expects(once()).method("visitParameter")
            .with(same(c)).id("v");
        mockC1.expects(once()).method("accept")
            .with(same(visitor)).after(mockVisior, "v");
        mockC2.expects(once()).method("accept")
            .with(same(visitor)).after(mockVisior, "v");
        mockC3.expects(once()).method("accept")
            .with(same(visitor)).after(mockVisior, "v");
        
        c.accept(visitor);
    }

    public void testAndAllChildrenConstraintsTrueGivesTrueUsingAlternateConstructor() {
        Constraint c = new And(new Constraint[] {c1, c2, c3});

        mockC1.expects(once()).method("evaluate")
            .with(same(adapter))
            .will(returnValue(Boolean.TRUE)).id("c1");
        mockC2.expects(once()).method("evaluate")
            .with(same(adapter)).after(mockC1, "c1")
            .will(returnValue(Boolean.TRUE)).id("c2");
        mockC3.expects(once()).method("evaluate")
            .with(same(adapter)).after(mockC2, "c2")
            .will(returnValue(Boolean.TRUE));

        assertTrue(c.evaluate(adapter));
    }

    public void testAndShortCircuitGivesFalse() {
        Constraint c = new And(c1, c2, c3);

        mockC1.expects(once()).method("evaluate")
            .with(same(adapter))
            .will(returnValue(Boolean.TRUE)).id("c1");
        mockC2.expects(once()).method("evaluate")
            .with(same(adapter)).after(mockC1, "c1")
            .will(returnValue(Boolean.FALSE));
        mockC3.expects(never()).method("evaluate");

        assertFalse(c.evaluate(adapter));
    }

    public void testOrAllChildrenConstraintsFalseGivesFalse() {
        Constraint c = new Or(c1, c2, c3);

        mockC1.expects(once()).method("evaluate")
            .with(same(adapter))
            .will(returnValue(Boolean.FALSE)).id("c1");
        mockC2.expects(once()).method("evaluate")
            .with(same(adapter)).after(mockC1, "c1")
            .will(returnValue(Boolean.FALSE)).id("c2");
        mockC3.expects(once()).method("evaluate")
            .with(same(adapter)).after(mockC2, "c2")
            .will(returnValue(Boolean.FALSE));

        assertFalse(c.evaluate(adapter));
    }
    
    public void testOrAllChildrenAreVisitedBreadthFirst() {
        Constraint c = new Or(c1, c2, c3);
        
        mockVisior.expects(once()).method("visitParameter")
            .with(same(c)).id("v");
        mockC1.expects(once()).method("accept")
            .with(same(visitor)).after(mockVisior, "v");
        mockC2.expects(once()).method("accept")
            .with(same(visitor)).after(mockVisior, "v");
        mockC3.expects(once()).method("accept")
            .with(same(visitor)).after(mockVisior, "v");
        
        c.accept(visitor);
    }

    public void testMixingOrAndNot() {
        Constraint c = new Or(c1, new Not(c2), c3);

        mockC1.expects(once()).method("evaluate")
            .with(same(adapter))
            .will(returnValue(Boolean.FALSE)).id("c1");
        mockC2.expects(once()).method("evaluate")
            .with(same(adapter)).after(mockC1, "c1")
            .will(returnValue(Boolean.FALSE));
        mockC3.expects(never()).method("evaluate");

        assertTrue(c.evaluate(adapter));
    }
    
    public void testNotChildIdVisitedBreadthFirst() {
        Constraint c = new Not(c1);
        
        mockVisior.expects(once()).method("visitParameter")
            .with(same(c)).id("v");
        mockC1.expects(once()).method("accept")
            .with(same(visitor)).after(mockVisior, "v");
        
        c.accept(visitor);
    }
}
