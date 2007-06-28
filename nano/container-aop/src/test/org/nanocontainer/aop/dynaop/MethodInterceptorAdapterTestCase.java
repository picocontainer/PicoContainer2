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
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.jmock.Mock;
import org.jmock.MockObjectTestCase;

/**
 * @author Stephen Molitor
 */
public final class MethodInterceptorAdapterTestCase extends MockObjectTestCase {

    private final Mock mockMethodInterceptor = mock(MethodInterceptor.class);
    private final Mock mockInvocation = mock(Invocation.class);

    public void testInvoke() throws Throwable {
        mockMethodInterceptor.expects(once()).method("invoke").with(isA(MethodInvocation.class)).will(returnValue("result"));

        dynaop.Interceptor interceptor = new MethodInterceptorAdapter((MethodInterceptor) mockMethodInterceptor.proxy());
        Object result = interceptor.intercept((Invocation) mockInvocation.proxy());
        assertEquals("result", result);
    }

}