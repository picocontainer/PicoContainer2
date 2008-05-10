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

import static org.junit.Assert.assertEquals;

import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JMock;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.nanocontainer.aop.AspectsApplicator;
import org.picocontainer.ComponentAdapter;
import org.picocontainer.DefaultPicoContainer;
import org.picocontainer.PicoContainer;
import org.picocontainer.tck.MockFactory;

/**
 * @author Stephen Molitor
 * @author Mauro Talevi
 */
@RunWith(JMock.class)
public final class AspectedTestCase {

	private Mockery mockery = MockFactory.mockeryWithCountingNamingScheme();

    private final AspectsApplicator applicator = mockery.mock(AspectsApplicator.class);
    private final ComponentAdapter componentAdapter = mockery.mock(ComponentAdapter.class);
    private final PicoContainer container = new DefaultPicoContainer();

    @Test public void testGetComponentInstance() {
    	mockery.checking(new Expectations(){{
    		one(componentAdapter).getComponentInstance(with(same(container)), with(same(ComponentAdapter.NOTHING.class)));
    		will(returnValue("addComponent"));
    		one(componentAdapter).getComponentKey();
    		will(returnValue("componentKey"));
    		one(applicator).applyAspects(with(same("componentKey")), with(same("addComponent")), with(same(container)));
    		will(returnValue("wrappedComponent"));
    	}});

    	ComponentAdapter adapter = new Aspected(applicator, componentAdapter);
        Object component = adapter.getComponentInstance(container);
        assertEquals("wrappedComponent", component);
    }

}