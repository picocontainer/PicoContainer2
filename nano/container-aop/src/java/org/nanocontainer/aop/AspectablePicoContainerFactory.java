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
import org.picocontainer.ComponentFactory;

/**
 * Produces <code>AspectablePicoContainer</code> objects. Mixes in an
 * <code>AspectsContainer</code> with a
 * <code>org.picocontainer.MutablePicoContainer</code> to produce an
 * <code>AspectablePicoContainer</code>.
 *
 * @author Stephen Molitor
 * @author Mauro Talevi
 */
public interface AspectablePicoContainerFactory {

    /**
     * Creates a new <code>AspectablePicoContainer</code>.
     *
     * @param containerClass          the class of the basic container to delegate to.
     * @param aspectsManager          the aspects manager used to register and apply
     *                                aspects.
     * @param componentFactory the delegate component factory
     *                                used to produce components.
     * @param parent                  the parent container.
     * @return a new <code>AspectablePicoContainer</code>.
     */
    public AspectablePicoContainer createContainer(Class containerClass, AspectsManager aspectsManager,
                                                   ComponentFactory componentFactory, PicoContainer parent);

    /**
     * Creates a new <code>AspectablePicoContainer</code>.
     *
     * @param containerClass          the class of the basic container to delegate to.
     * @param componentFactory the delegate component factory
     *                                used to produce components.
     * @param parent                  the parent container.
     * @return a new <code>AspectablePicoContainer</code>.
     */
    AspectablePicoContainer createContainer(Class containerClass, ComponentFactory componentFactory,
                                            PicoContainer parent);

    /**
     * Creates a new <code>AspectablePicoContainer</code>. Uses
     * <code>org.picocontainer.DefaultPicoContainer</code> as the
     * delegate container.
     *
     * @param componentFactory the delegate component factory
     *                                used to produce components.
     * @param parent                  the parent container.
     * @return a new <code>AspectablePicoContainer</code>.
     */
    AspectablePicoContainer createContainer(ComponentFactory componentFactory, PicoContainer parent);

    /**
     * Creates a new <code>AspectablePicoContainer</code>. Uses
     * <code>org.picocontainer.DefaultPicoContainer</code> as the
     * delegate container.
     *
     * @param componentFactory the delegate component factory
     *                                used to produce components.
     * @return a new <code>AspectablePicoContainer</code>.
     */
    AspectablePicoContainer createContainer(ComponentFactory componentFactory);

    /**
     * Creates a new <code>AspectablePicoContainer</code>. Uses
     * <code>org.picocontainer.DefaultPicoContainer</code> as the
     * delegate container. Uses
     * <code>org.picocontainer.injectors.AdaptingInjection</code>
     * as the delegate component factory.
     *
     * @param parent the parent container.
     * @return a new <code>AspectablePicoContainer</code>.
     */
    AspectablePicoContainer createContainer(PicoContainer parent);

    /**
     * Creates a new <code>AspectablePicoContainer</code>. Uses
     * <code>org.picocontainer.DefaultPicoContainer</code> as the
     * delegate container. Uses
     * <code>org.picocontainer.injectors.AdaptingInjection</code>
     * as the delegate component factory.
     *
     * @return a new <code>AspectablePicoContainer</code>.
     */
    AspectablePicoContainer createContainer();

    /**
     * Make a child <code>AspectablePicoContainer</code> of a given <code>AspectablePicoContainer</code>.
     * The child container will be obtained aspectifying <code>MutablePicoContainer#makeChildContainer()</code>.
     * 
     * @param aspectsManager the aspects manager used to register and apply aspects.
     * @param parent the parent AspectablePicoContainer
     * @return A child AspectablePicoContainer
     */    
    AspectablePicoContainer makeChildContainer(AspectsManager aspectsManager, AspectablePicoContainer parent);

    /**
     * Make a child <code>AspectablePicoContainer</code> of a given <code>AspectablePicoContainer</code>
     * The child container will be obtained aspectifying <code>MutablePicoContainer#makeChildContainer()</code>.
     * 
     * @param parent the parent AspectablePicoContainer
     * @return A child AspectablePicoContainer
     */    
    AspectablePicoContainer makeChildContainer(AspectablePicoContainer parent);

}