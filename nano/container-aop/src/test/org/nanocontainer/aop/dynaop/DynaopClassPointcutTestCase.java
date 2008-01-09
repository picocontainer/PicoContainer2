/*****************************************************************************
 * Copyright (c) PicoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the LICENSE.txt file.                                                     *
 *                                                                           *
 * Idea by Rachel Davies, Original code by various                           *
 *****************************************************************************/
package org.nanocontainer.aop.dynaop;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JMock;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.picocontainer.tck.MockFactory;

import dynaop.ClassPointcut;

/**
 * @author Stephen Molitor
 * @author Mauro Talevi
 */
@RunWith(JMock.class)
public final class DynaopClassPointcutTestCase {

	private Mockery mockery = MockFactory.mockeryWithCountingNamingScheme();
	
    private final ClassPointcut classPointcut = mockery.mock(ClassPointcut.class);

    @Test public void testPicks() {
    	 mockery.checking(new Expectations(){{
     		one(classPointcut).picks(with(equal(String.class)));
     		will(returnValue(false));
     		one(classPointcut).picks(with(equal(Integer.class)));    		
     		will(returnValue(true));
     	}});

    	ClassPointcut pointcut = new DynaopClassPointcut(classPointcut);
        assertFalse(pointcut.picks(String.class));
        assertTrue(pointcut.picks(Integer.class));
    }

}