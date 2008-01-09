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

import java.lang.reflect.Method;

import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JMock;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.picocontainer.tck.MockFactory;

import dynaop.MethodPointcut;

/**
 * @author Stephen Molitor
 * @author Mauro Talevi
 */
@RunWith(JMock.class)
public final class DynaopMethodPointcutTestCase {

	private Mockery mockery = MockFactory.mockeryWithCountingNamingScheme();

    private final MethodPointcut methodPointcut = mockery.mock(MethodPointcut.class);

    @Test public void testPicks() throws SecurityException, NoSuchMethodException {
        final Method method1 = String.class.getMethod("length");
        final Method method2 = String.class.getMethod("hashCode");

        mockery.checking(new Expectations(){{
    		one(methodPointcut).picks(with(equal(method1)));
    		will(returnValue(false));
    		one(methodPointcut).picks(with(equal(method2)));    		
    		will(returnValue(true));
    	}});
        
        dynaop.MethodPointcut pointcut = new DynaopMethodPointcut(methodPointcut);
        assertFalse(pointcut.picks(method1));
        assertTrue(pointcut.picks(method2));
    }

}
