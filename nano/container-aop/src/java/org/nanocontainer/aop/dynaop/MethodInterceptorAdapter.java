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

import dynaop.Interceptor;
import dynaop.Invocation;
import org.aopalliance.intercept.MethodInterceptor;

/**
 * Adapts a <code>org.aopalliance.intercept.MethodInterceptor</code> to the
 * <code>dynaop.Interceptor</code> interface.
 *
 * @author Stephen Molitor
 * @version $Revision$
 */
class MethodInterceptorAdapter implements Interceptor {

    private final MethodInterceptor delegate;

    /**
     * Creates a new <code>MethodInterceptorAdapter</code> that will delegate
     * to the given <code>org.aopalliance.intercept.MethodInterceptor</code>.
     *
     * @param delegate the
     *                 <code>org.aopalliance.intercept.MethodInterceptor</code> to
     *                 delegate to.
     */
    MethodInterceptorAdapter(MethodInterceptor delegate) {
        this.delegate = delegate;
    }

    public Object intercept(Invocation invocation) throws Throwable {
        return delegate.invoke(new InvocationAdapter(invocation));
    }

}