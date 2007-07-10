/*****************************************************************************
 * Copyright (C) PicoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the LICENSE.txt file.                                                     *
 *                                                                           *
 * Original code by Paul Hammant                                             *
 *****************************************************************************/

package org.picocontainer.containers;

import org.picocontainer.ComponentAdapter;
import org.picocontainer.PicoContainer;
import org.picocontainer.PicoVisitor;
import org.picocontainer.DefaultPicoContainer;

import org.jmock.Mock;
import org.jmock.MockObjectTestCase;


/**
 * @author Paul Hammant
 * @author J&ouml;rg Schaible
 */
public class ImmutablePicoContainerTestCase extends MockObjectTestCase {

    public void testImmutingOfNullBarfs() {
        try {
            new ImmutablePicoContainer(null);
            fail("Should have barfed");
        } catch (NullPointerException e) {
            // expected
        }
    }

    public void testVisitingOfImmutableContainerWorks() {
        DefaultPicoContainer pico = new DefaultPicoContainer();
        Object foo = new Object();
        ComponentAdapter componentAdapter = pico.addComponent(foo).getComponentAdapter(foo.getClass(), null);

        Mock fooVisitor = new Mock(PicoVisitor.class);
        fooVisitor.expects(once()).method("visitContainer").with(same(pico));
        fooVisitor.expects(once()).method("visitComponentAdapter").with(same(componentAdapter));

        PicoContainer ipc = new ImmutablePicoContainer(pico);
        ipc.accept((PicoVisitor)fooVisitor.proxy());
    }

    public void testProxyEquals() throws Exception {
        DefaultPicoContainer pico = new DefaultPicoContainer();
        PicoContainer ipc = new ImmutablePicoContainer(pico);
        assertEquals(ipc, ipc);
        assertEquals(ipc, new ImmutablePicoContainer(pico));
    }

    public void testHashCodeIsSame() throws Exception {
        DefaultPicoContainer pico = new DefaultPicoContainer();
        assertEquals(pico.hashCode(), new ImmutablePicoContainer(pico).hashCode());
    }
    
    public void testDoesNotEqualsToNull() {
        DefaultPicoContainer pico = new DefaultPicoContainer();
        PicoContainer ipc = new ImmutablePicoContainer(pico);
        assertFalse(ipc.equals(null));
    }
}
