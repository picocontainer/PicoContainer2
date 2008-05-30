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

import dynaop.MixinFactory;
import dynaop.Proxy;
import org.picocontainer.ComponentAdapter;
import org.picocontainer.PicoContainer;
import org.picocontainer.lifecycle.NullLifecycleStrategy;
import org.picocontainer.monitors.NullComponentMonitor;
import org.picocontainer.injectors.ConstructorInjector;

/**
 * Manufactures mixins from a <code>PicoContainer</code>. Useful when a mixin
 * has dependencies on other components in the <code>PicoContainer</code>.
 *
 * @author Stephen Molitor
 */
class ContainerSuppliedMixinFactory implements MixinFactory {

    private final PicoContainer pico;
    private final Class mixinClass;

    /**
     * Creates a new <code>ContainerSuppliedMixinFactory</code> that will
     * manufacture mixins by retrieving them from the <code>PicoContainer</code>
     * using a given component key.
     *
     * @param pico              the <code>PicoContainer</code> to retrieve the mixin from.
     * @param mixinClass    the mixin class
     */
    ContainerSuppliedMixinFactory(PicoContainer pico, Class mixinClass) {
        this.pico = pico;
        this.mixinClass = mixinClass;
    }

    /**
     * Manufactures a <code>Mixin</code> by retrieving it from the
     * <code>PicoContainer</code>.
     *
     * @param proxy the proxy that the interceptor will wrap.
     * @return the <code>Mixin</code> object.
     * @throws NullPointerException if the mixin can not be found in the pico
     *                              container.
     */
    public Object create(Proxy proxy) throws NullPointerException {
        Object mixin = pico.getComponent(mixinClass);
        if (mixin == null) {
            ComponentAdapter adapter = new ConstructorInjector(mixinClass, mixinClass, null, new NullComponentMonitor(), new NullLifecycleStrategy(), false);
            mixin = adapter.getComponentInstance(pico);
        }
        return mixin;
    }

    public Class getAdviceClass() {
        return this.getClass();
    }

    public String getScope() {
        return "per-instance";  
    }
}