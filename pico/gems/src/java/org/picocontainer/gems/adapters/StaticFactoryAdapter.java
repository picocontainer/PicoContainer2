/*****************************************************************************
 * Copyright (C) PicoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the LICENSE.txt file.                                                     *
 *                                                                           *
 * Original code by Leo Simmons & J&ouml;rg Schaible                         *
 *****************************************************************************/
package org.picocontainer.gems.adapters;

import org.picocontainer.PicoContainer;
import org.picocontainer.PicoCompositionException;
import org.picocontainer.PicoVerificationException;
import org.picocontainer.adapters.AbstractAdapter;


/**
 * Component adapter that wrapps a static factory with the help of {@link StaticFactory}.
 *
 * @author J&ouml;rg Schaible
 * @author Leo Simmons
 * @since 1.1
 */
public final class StaticFactoryAdapter extends AbstractAdapter {
    private final StaticFactory staticFactory;

    /**
     * Construct a ComponentAdapter accessing a static factory creating the component.
     *
     * @param type The type of the created component.
     * @param staticFactory Wrapper instance for the static factory.
     */
    public StaticFactoryAdapter(Class type, StaticFactory staticFactory) {

        this(type, type, staticFactory);
    }

    /**
     * Construct a ComponentAdapter accessing a static factory creating the component using a special key for addComponent
     * registration.
     *
     * @param componentKey The key of the created component.
     * @param type The type of the created component.
     * @param staticFactory Wrapper instance for the static factory.
     */
    public StaticFactoryAdapter(Object componentKey, Class type, StaticFactory staticFactory) {
        super(componentKey, type);
        this.staticFactory = staticFactory;
    }

    /**
     * @return Returns the component created by the static factory.
     * @see org.picocontainer.ComponentAdapter#getComponentInstance(org.picocontainer.PicoContainer)
     */
    public Object getComponentInstance(PicoContainer container) throws
                                                                PicoCompositionException
    {
        return staticFactory.get();
    }

    /**
     * {@inheritDoc}
     *
     * @see org.picocontainer.ComponentAdapter#verify(org.picocontainer.PicoContainer)
     */
    public void verify(PicoContainer container) throws PicoVerificationException {
    }
}