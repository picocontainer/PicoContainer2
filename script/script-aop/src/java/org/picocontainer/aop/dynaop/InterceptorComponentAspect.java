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

import dynaop.Aspects;
import dynaop.Interceptor;
import dynaop.InterceptorFactory;
import dynaop.MethodPointcut;
import dynaop.Pointcuts;

import org.picocontainer.aop.ComponentPointcut;

/**
 * Interceptor aspect that is applied to the components that match a addComponent
 * pointcut.
 *
 * @author Stephen Molitor
 */
final class InterceptorComponentAspect extends ComponentAspect {

    private final MethodPointcut methodPointcut;
    private Interceptor interceptor;
    private InterceptorFactory interceptorFactory;

    /**
     * Creates a new <code>InterceptorComponentAspect</code> from a given
     * interceptor advice object.
     *
     * @param componentPointcut the components to apply the interceptor to.
     * @param methodPointcut    the methods to intercept.
     * @param interceptor       the interceptor advice object to apply.
     */
    InterceptorComponentAspect(ComponentPointcut componentPointcut, MethodPointcut methodPointcut,
                               Interceptor interceptor) {
        super(componentPointcut);
        this.methodPointcut = methodPointcut;
        this.interceptor = interceptor;
    }

    /**
     * Creates a new <code>InterceptorComponentAspect</code> from a given
     * interceptor factory.
     *
     * @param componentPointcut  the components to apply the interceptor to.
     * @param methodPointcut     the methods to intercept.
     * @param interceptorFactory produces the interceptor advice object.
     */
    InterceptorComponentAspect(ComponentPointcut componentPointcut, MethodPointcut methodPointcut,
                               InterceptorFactory interceptorFactory) {
        super(componentPointcut);
        this.methodPointcut = methodPointcut;
        this.interceptorFactory = interceptorFactory;
    }

    void doRegisterAspect(Object componentKey, Aspects aspects) {
        if (interceptor != null) {
            aspects.interceptor(Pointcuts.ALL_CLASSES, methodPointcut, interceptor);
        } else {
            aspects.interceptor(Pointcuts.ALL_CLASSES, methodPointcut, interceptorFactory);
        }
    }

}