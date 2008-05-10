/*****************************************************************************
 * Copyright (c) PicoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the LICENSE.txt file.                                                     *
 *                                                                           *
 * Idea by Rachel Davies, Original code by various                           *
 *****************************************************************************/
package org.nanocontainer.aop;

import org.picocontainer.PicoContainer;

/**
 * Applies aspects to a component. Intended for use by component adapters that
 * need to inject aspects into a component.
 *
 * @author Stephen Molitor
 */
public interface AspectsApplicator {

    public Object applyAspects(Object componentKey, Object component, PicoContainer container);

}