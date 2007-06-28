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
import dynaop.InterceptorFactory;
import dynaop.Proxy;
import org.aopalliance.intercept.MethodInterceptor;
import org.picocontainer.PicoContainer;

import java.util.Properties;

/**
 * Manufactures interceptors from a <code>PicoContainer</code>. Useful when
 * an interceptor has dependencies on other components in the
 * <code>PicoContainer</code>.
 *
 * @author Stephen Molitor
 * @version $Revision$
 */
class ContainerSuppliedInterceptorFactory implements InterceptorFactory {

    private final PicoContainer pico;
    private final Object interceptorComponentKey;

    /**
     * Creates a new <code>ContainerSuppliedInterceptorFactory</code> that
     * will manufacture interceptors by retrieving them from the
     * <code>PicoContainer</code> using a given component key.
     *
     * @param pico                    the <code>PicoContainer</code> to retrieve the interceptor
     *                                from.
     * @param interceptorComponentKey the component key that will be used to
     *                                retrieve the interceptor from the pico container.
     */
    ContainerSuppliedInterceptorFactory(PicoContainer pico, Object interceptorComponentKey) {
        this.pico = pico;
        this.interceptorComponentKey = interceptorComponentKey;
    }

    /**
     * Manufactures an <code>Interceptor</code> by retrieving it from the
     * <code>PicoContainer</code>.
     *
     * @param proxy not used.
     * @return the <code>Interceptor</code> object.
     * @throws NullPointerException if the interceptor can not be found in the
     *                              pico container.
     */
    public Interceptor create(Proxy proxy) throws NullPointerException {
        MethodInterceptor methodInterceptor = (MethodInterceptor) pico.getComponent(interceptorComponentKey);
        if (methodInterceptor == null) {
            throw new NullPointerException("Interceptor with component key " + interceptorComponentKey
                    + " + not found in PicoContainer");
        }
        return new MethodInterceptorAdapter(methodInterceptor);
    }

    /**
     * Gets properties. Useful for debugging.
     *
     * @return a <code>Properties</code> object.
     */
    public Properties getProperties() {
        Properties properties = new Properties();
        properties.setProperty("advice", "method interceptor");
        properties.setProperty("scope", "per-instance");
        return properties;
    }

}