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

import static org.junit.Assert.assertEquals;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JMock;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.picocontainer.tck.MockFactory;

import dynaop.Invocation;

/**
 * @author Stephen Molitor
 * @author Mauro Talevi
 */
@RunWith(JMock.class)
public final class MethodInterceptorAdapterTestCase {

	private Mockery mockery = MockFactory.mockeryWithCountingNamingScheme();

    private final MethodInterceptor methodInterceptor = mockery.mock(MethodInterceptor.class);
    private final Invocation invocation = mockery.mock(Invocation.class);

    @Test public void testInvoke() throws Throwable {
    	 mockery.checking(new Expectations(){{
     		one(methodInterceptor).invoke(with(any(MethodInvocation.class)));
     		will(returnValue("result"));
     	}});

        dynaop.Interceptor interceptor = new MethodInterceptorAdapter(methodInterceptor);
        Object result = interceptor.intercept(invocation);
        assertEquals("result", result);
    }

}