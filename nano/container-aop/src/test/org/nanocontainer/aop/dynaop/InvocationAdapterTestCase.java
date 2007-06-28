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

import dynaop.Invocation;
import dynaop.Proxy;
import dynaop.ProxyContext;
import org.aopalliance.intercept.MethodInvocation;
import org.jmock.Mock;
import org.jmock.MockObjectTestCase;

import java.lang.reflect.Method;

/**
 * @author Stephen Molitor
 */
public final class InvocationAdapterTestCase extends MockObjectTestCase {

    private final Mock mockInvocation = mock(Invocation.class);
    private final MethodInvocation invocationAdapter = new InvocationAdapter((Invocation) mockInvocation.proxy());
    private final Mock mockProxy = mock(Proxy.class);
    private final Mock mockProxyContext = mock(ProxyContext.class);

    public void testProceed() throws Throwable {
        mockInvocation.expects(once()).method("proceed").will(returnValue("result"));
        Object result = invocationAdapter.proceed();
        assertEquals("result", result);
    }

    public void testGetArguments() {
        Object[] args = {"a", "b", "c"};
        mockInvocation.expects(once()).method("getArguments").will(returnValue(args));
        Object[] actualArgs = invocationAdapter.getArguments();
        assertEquals(args, actualArgs);
    }

    public void testGetMethod() throws SecurityException, NoSuchMethodException {
        Method method = String.class.getMethod("length");
        mockInvocation.expects(once()).method("getMethod").will(returnValue(method));
        Method actualMethod = invocationAdapter.getMethod();
        assertEquals(method, actualMethod);
    }

    public void testGetStaticPart() throws SecurityException, NoSuchMethodException {
        Method method = String.class.getMethod("length");
        mockInvocation.expects(once()).method("getMethod").will(returnValue(method));
        Object staticPart = invocationAdapter.getStaticPart();
        assertEquals(method, staticPart);
    }

    public void testGetThis() {
        mockInvocation.expects(once()).method("getProxy").will(returnValue(mockProxy.proxy()));
        mockProxy.expects(once()).method("getProxyContext").will(returnValue(mockProxyContext.proxy()));
        mockProxyContext.expects(once()).method("unwrap").will(returnValue("target"));
        Object target = invocationAdapter.getThis();
        assertEquals("target", target);
    }

}