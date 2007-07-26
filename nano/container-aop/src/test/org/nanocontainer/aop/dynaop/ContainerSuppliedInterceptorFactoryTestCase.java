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
import org.aopalliance.intercept.MethodInterceptor;
import org.jmock.Mock;
import org.jmock.MockObjectTestCase;
import org.picocontainer.MutablePicoContainer;
import org.picocontainer.DefaultPicoContainer;

/**
 * @author Stephen Molitor
 */
public final class ContainerSuppliedInterceptorFactoryTestCase extends MockObjectTestCase {

    private final MutablePicoContainer pico = new DefaultPicoContainer();

    public void testCreate() throws Throwable {
        Mock methodInterceptorMock = mock(MethodInterceptor.class);

        pico.addComponent("interceptorComponentKey", methodInterceptorMock.proxy());

        InterceptorFactory interceptorFactory = new ContainerSuppliedInterceptorFactory(pico, "interceptorComponentKey");
        Interceptor interceptor = interceptorFactory.create(null);
        assertNotNull(interceptor);

        // verify that the dynaop interceptor delegates to the MethodInterceptor
        // in the container:
        methodInterceptorMock.expects(once()).method("invoke");
        interceptor.intercept(null);
    }

    public void testInterceptorNotFoundInContainer() {
        MutablePicoContainer container = new DefaultPicoContainer();
        InterceptorFactory interceptorFactory = new ContainerSuppliedInterceptorFactory(container,
                "interceptorComponentKey");
        try {
            interceptorFactory.create(null);
            fail("NullPointerException should have been raised");
        } catch (NullPointerException e) {
        }
    }

}