package org.picocontainer.gems.adapters;

/*****************************************************************************
 * Copyright (C) PicoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the LICENSE.txt file.                                                     *
 *                                                                           *
 * Original code by Leo Simmons & Jörg Schaible                              *
 *****************************************************************************/
/**
 * Interface for a static factory wrapper used by the {@link StaticFactoryAdapter}.
 * 
 * @author J&ouml;rg Schaible
 * @author Leo Simmons
 * @since 1.1
 */
public interface StaticFactory {
    /**
     * @return Returns the instance created by the factory.
     */
    public Object get();
}