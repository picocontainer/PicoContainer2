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
import dynaop.Invocation;
import dynaop.Pointcuts;
import dynaop.ProxyFactory;
import org.picocontainer.PicoContainer;

/**
 * Creates dynamically generated <code>PicoContainer</code> proxy objects that
 * delegate to a <code>PicoContainer</code> supplied by a
 * <code>ContainerLoader</code>.
 *
 * @author Stephen Molitor
 */
class PicoContainerProxy implements Interceptor {

    private final ContainerLoader containerLoader;

    /**
     * Creates a <code>PicoContainer</code> proxy that delegates to a
     * <code>PicoContainer</code> provided by <code>containerLoader</code>.
     *
     * @param containerLoader the container loader.
     * @return the dynamically generated proxy.
     */
    static PicoContainer create(ContainerLoader containerLoader) {
        Aspects aspects = new Aspects();
        aspects.interceptor(Pointcuts.ALL_CLASSES, Pointcuts.ALL_METHODS, new PicoContainerProxy(containerLoader));
        aspects.interfaces(Pointcuts.ALL_CLASSES, new Class[]{PicoContainer.class});
        return (PicoContainer) new ProxyFactory(aspects).wrap(new Object());
    }

    public Object intercept(Invocation invocation) throws Throwable {
        return invocation.getMethod().invoke(containerLoader.getContainer(), invocation.getArguments());
    }

    private PicoContainerProxy(ContainerLoader containerLoader) {
        this.containerLoader = containerLoader;
    }

}