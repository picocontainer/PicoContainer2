/*****************************************************************************
 * Copyright (c) PicoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the LICENSE.txt file.                                                     *
 *                                                                           *
 * Idea by Rachel Davies, Original code by various                           *
 *****************************************************************************/
package org.nanocontainer.aop.defaults;

import org.jmock.Mock;
import org.jmock.MockObjectTestCase;
import org.nanocontainer.aop.AspectsApplicator;
import org.picocontainer.ComponentAdapter;
import org.picocontainer.PicoContainer;
import org.picocontainer.DefaultPicoContainer;

/**
 * @author Stephen Molitor
 */
public final class AspectedTestCase extends MockObjectTestCase {

    private final Mock mockApplicator = mock(AspectsApplicator.class);
    private final Mock mockComponentAdapterDelegate = mock(ComponentAdapter.class);
    private final PicoContainer container = new DefaultPicoContainer();

    public void testGetComponentInstance() {
        mockComponentAdapterDelegate.expects(once()).method("getComponentInstance").with(same(container)).will(returnValue("addComponent"));
        mockComponentAdapterDelegate.expects(once()).method("getComponentKey").will(returnValue("componentKey"));

        mockApplicator.expects(once()).method("applyAspects").with(same("componentKey"), same("addComponent"),
                same(container)).will(returnValue("wrappedComponent"));

        ComponentAdapter adapter = new Aspected((AspectsApplicator) mockApplicator.proxy(),
                (ComponentAdapter) mockComponentAdapterDelegate.proxy());
        Object component = adapter.getComponentInstance(container);
        assertEquals("wrappedComponent", component);
    }

}