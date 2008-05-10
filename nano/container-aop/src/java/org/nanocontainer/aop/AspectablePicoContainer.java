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

import org.picocontainer.MutablePicoContainer;

/**
 * A <code>org.picocontainer.MutablePicoContainer</code> that supports the
 * application of aspects to components in the container.
 *
 * @author Stephen Molitor
 * @see <a href='package-summary.html#package_description'>The package
 *      description</a> has a basic overview of how to use the nanoaop package.
 */
public interface AspectablePicoContainer extends AspectsContainer, MutablePicoContainer {

}