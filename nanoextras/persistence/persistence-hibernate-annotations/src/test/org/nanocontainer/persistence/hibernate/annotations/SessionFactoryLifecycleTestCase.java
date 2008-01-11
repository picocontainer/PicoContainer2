/*****************************************************************************
 * Copyright (c) PicoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the LICENSE.txt file.                                                     *
 *                                                                           *
 * Idea by Rachel Davies, Original code by various                           *
 *****************************************************************************/

package org.nanocontainer.persistence.hibernate.annotations;

import static org.picocontainer.tck.MockFactory.mockeryWithCountingNamingScheme;

import org.hibernate.SessionFactory;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JMock;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * Test that lifecycle closes session factory
 */
@RunWith(JMock.class)
public class SessionFactoryLifecycleTestCase {

	private Mockery mockery = mockeryWithCountingNamingScheme();
	
    @Test public void testThatLifecycleCallsClose() throws Exception {
    	final SessionFactory sessionFactory = mockery.mock(SessionFactory.class);
    	mockery.checking(new Expectations(){{
    		one(sessionFactory).close();
    	}});
    	
		SessionFactoryLifecycle sfl = new SessionFactoryLifecycle(sessionFactory);
		sfl.stop();
	}
}
