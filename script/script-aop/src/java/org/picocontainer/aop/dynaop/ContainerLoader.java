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

import org.picocontainer.PicoContainer;
import org.picocontainer.PicoCompositionException;

/**
 * Loads a <code>PicoContainer</code> 'late'. Used to create a late-loading
 * <code>PicoContainer</code> proxy which is passed to
 * <code>dynaop.MixinFactory</code> and <code>dynaop.InterceptorFactory</code>
 * objects whose mixin or interceptor advice is a component in the container,
 * specified by component key. The container object may be created after the
 * advice factories.
 *
 * @author Stephen Molitor
 */
class ContainerLoader {

    private PicoContainer container;

    /**
     * Gets the Pico container. The <code>setContainer</code> method must have
     * been called prior to calling this method.
     *
     * @return the loaded <code>PicoContainer</code> object.
     * @throws PicoCompositionException if the container has not been set.
     */
    PicoContainer getContainer() {
        if (container == null) {
            throw new PicoCompositionException("Container has not been set");
        }
        return container;
    }

    /**
     * Sets the container.
     *
     * @param container the <code>PicoContainer</code>.
     */
    void setContainer(PicoContainer container) {
        this.container = container;
    }

}