/*****************************************************************************
 * Copyright (C) PicoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the LICENSE.txt file.                                                     *
 *****************************************************************************/
package org.picocontainer;

import org.picocontainer.MutablePicoContainer;
import org.picocontainer.PicoContainer;
import org.picocontainer.PicoCompositionException;
import org.picocontainer.PicoVisitor;
import org.picocontainer.DefaultPicoContainer;
import org.picocontainer.visitors.VerifyingVisitor;
import org.picocontainer.visitors.AbstractPicoVisitor;

import org.jmock.Mock;
import org.jmock.MockObjectTestCase;


/**
 * Test general PicoVisitor behaviour.
 * @author J&ouml;rg Schaible
 */
public class PicoVisitorTestCase
        extends MockObjectTestCase {

    public void testVisitorThatMustBeInvokedUsingTraverse() {
        MutablePicoContainer pico = new DefaultPicoContainer();
        try {
            pico.accept(new VerifyingVisitor());
            fail("PicoVisitorTraversalException expected");
        } catch (AbstractPicoVisitor.PicoVisitorTraversalException e) {
            assertTrue(e.getMessage().indexOf(VerifyingVisitor.class.getName()) >= 0);
        }
    }

    public static class UnusualNode {
        boolean visited;

        public void accept(PicoVisitor visit) {
            visited = true;
        }
    }

    public void testUnusualTraverseNode() {
        UnusualNode node = new UnusualNode();
        new VerifyingVisitor().traverse(node);
        assertTrue(node.visited);
    }

    public void testIllegalTraverseNode() {
        try {
            new VerifyingVisitor().traverse("Gosh!");
            fail("IllegalArgumentException expected");
        } catch (IllegalArgumentException e) {
            assertTrue(e.getMessage().indexOf(String.class.getName()) >= 0);
        }
    }

    public void testThrownRuntimeExceptionIsUnwrapped() {
        Mock mockPico = mock(PicoContainer.class);
        PicoVisitor visitor = new VerifyingVisitor();
        Error exception = new Error("junit");
        mockPico.expects(once()).method("accept").with(same(visitor)).will(
                throwException(new PicoCompositionException("message", exception)));
        try {
            visitor.traverse(mockPico.proxy());
            fail("PicoCompositionException expected");
        } catch (RuntimeException e) {
            assertEquals("message", e.getMessage());
            assertSame(exception, e.getCause());
        }
    }

    public void testThrownErrorIsUnwrapped() {
        Mock mockPico = mock(PicoContainer.class);
        PicoVisitor visitor = new VerifyingVisitor();
        Error error = new InternalError("junit");
        mockPico.expects(once()).method("accept").with(same(visitor)).id("1");
        mockPico.expects(once()).method("accept").with(same(visitor)).after("1").will(throwException(error));
        visitor.traverse(mockPico.proxy());
        try {
            visitor.traverse(mockPico.proxy());
            fail("UndeclaredThrowableException expected");
        } catch(InternalError e) {
            assertEquals("junit", e.getMessage());
        }
    }
}
