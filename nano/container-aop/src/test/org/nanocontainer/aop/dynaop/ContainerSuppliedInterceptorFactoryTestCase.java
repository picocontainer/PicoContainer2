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

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JMock;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.picocontainer.DefaultPicoContainer;
import org.picocontainer.MutablePicoContainer;
import org.picocontainer.tck.MockFactory;

import dynaop.Interceptor;
import dynaop.InterceptorFactory;

/**
 * @author Stephen Molitor
 * @author Mauro Talevi
 */
@RunWith(JMock.class)
public final class ContainerSuppliedInterceptorFactoryTestCase {

	private Mockery mockery = MockFactory.mockeryWithCountingNamingScheme();

    private final MutablePicoContainer pico = new DefaultPicoContainer();

    @Test public void testCreate() throws Throwable {
    	final MethodInterceptor methodInterceptor = mockery.mock(MethodInterceptor.class);

        pico.addComponent("interceptorComponentKey", methodInterceptor);

        InterceptorFactory interceptorFactory = new ContainerSuppliedInterceptorFactory(pico, "interceptorComponentKey");
        Interceptor interceptor = interceptorFactory.create(null);
        assertNotNull(interceptor);

        // verify that the dynaop interceptor delegates to the MethodInterceptor
        // in the container:
    	mockery.checking(new Expectations(){{
    		one(methodInterceptor).invoke(with(any(MethodInvocation.class)));
    	}});
        interceptor.intercept(null);
    }

    @Test public void testInterceptorNotFoundInContainer() {
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