/*****************************************************************************
 * Copyright (c) PicoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the LICENSE.txt file.                                                     *
 *                                                                           *
 * Idea by Rachel Davies, Original code by various                           *
 *****************************************************************************/
package org.picocontainer.aop.dynaop;


import static org.junit.Assert.assertEquals;

import java.lang.reflect.Method;

import org.aopalliance.intercept.MethodInvocation;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JMock;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.picocontainer.aop.dynaop.InvocationAdapter;
import org.picocontainer.tck.MockFactory;

import dynaop.Invocation;
import dynaop.Proxy;
import dynaop.ProxyContext;

/**
 * @author Stephen Molitor
 * @author Mauro Talevi
 */
@RunWith(JMock.class)
public final class InvocationAdapterTestCase {

	private Mockery mockery = MockFactory.mockeryWithCountingNamingScheme();
	
    private final Invocation invocation = mockery.mock(Invocation.class);
    private final MethodInvocation methodInvocation = new InvocationAdapter(invocation);
    private final Proxy proxy = mockery.mock(Proxy.class);
    private final ProxyContext proxyContext = mockery.mock(ProxyContext.class);

    @Test public void testProceed() throws Throwable {
        mockery.checking(new Expectations(){{
    		one(invocation).proceed();
    		will(returnValue("result"));
    	}});
        Object result = methodInvocation.proceed();
        assertEquals("result", result);
    }

    @Test public void testGetArguments() {
        final Object[] args = {"a", "b", "c"};
        mockery.checking(new Expectations(){{
    		one(invocation).getArguments();
    		will(returnValue(args));
    	}});
        Object[] actualArgs = methodInvocation.getArguments();
        assertEquals(args, actualArgs);
    }

    @Test public void testGetMethod() throws SecurityException, NoSuchMethodException {
        final Method method = String.class.getMethod("length");
        mockery.checking(new Expectations(){{
    		one(invocation).getMethod();
    		will(returnValue(method));
    	}});
        Method actualMethod = methodInvocation.getMethod();
        assertEquals(method, actualMethod);
    }

    @Test public void testGetStaticPart() throws SecurityException, NoSuchMethodException {
        final Method method = String.class.getMethod("length");
        mockery.checking(new Expectations(){{
    		one(invocation).getMethod();
    		will(returnValue(method));
    	}});
        Object staticPart = methodInvocation.getStaticPart();
        assertEquals(method, staticPart);
    }

    @Test public void testGetThis() {
        mockery.checking(new Expectations(){{
    		one(invocation).getProxy();
    		will(returnValue(proxy));
       		one(proxy).getProxyContext();
    		will(returnValue(proxyContext));
       		one(proxyContext).unwrap();
    		will(returnValue("target"));
    	}});
        Object target = methodInvocation.getThis();
        assertEquals("target", target);
    }

}