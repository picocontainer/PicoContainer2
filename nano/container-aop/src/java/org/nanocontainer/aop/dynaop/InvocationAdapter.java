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
import org.aopalliance.intercept.MethodInvocation;

import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Method;

/**
 * Adapts a <code>dynaop.Invocation</code> object to the
 * <code>org.nanocontainer.nanoaop.MethodInvocation</code> interface.
 *
 * @author Stephen Molitor
 * @version $Revision$
 */
class InvocationAdapter implements MethodInvocation {

    private final Invocation delegate;

    /**
     * Creates a new <code>InvocationAdapter</code> that delegates to the
     * given <code>dynaop.Invocation</code>.
     *
     * @param delegate the <code>dynaop.Invocation</code> object to delegate
     *                 to.
     */
    InvocationAdapter(Invocation delegate) {
        this.delegate = delegate;
    }

    public Method getMethod() {
        return delegate.getMethod();
    }

    public Object[] getArguments() {
        return delegate.getArguments();
    }

    public AccessibleObject getStaticPart() {
        return delegate.getMethod();
    }

    public Object getThis() {
        return delegate.getProxy().getProxyContext().unwrap();
    }

    public Object proceed() throws Throwable {
        return delegate.proceed();
    }

}