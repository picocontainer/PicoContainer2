/*****************************************************************************
 * Copyright (C) PicoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the LICENSE.txt file.                                                     *
 *****************************************************************************/
package org.picocontainer;

/**
 * A component adapter is responsible for providing a specific component
 * instance of type <T>. An instance of an implementation of this interface is
 * used inside a {@link PicoContainer} for every registered component or
 * instance. Each <code>ComponentAdapter</code> instance has to have a key
 * which is unique within that container. The key itself is either a class type
 * (normally an interface) or an identifier.
 * 
 * @author Jon Tirs&eacute;n
 * @author Paul Hammant
 * @author Aslak Helles&oslash;y
 */
public interface ComponentAdapter<T> {

    /**
     * Retrieve the key associated with the component.
     *
     * @return the component's key. Should either be a class type (normally an interface) or an identifier that is
     *         unique (within the scope of the current PicoContainer).
     */
    Object getComponentKey();

    /**
     * Retrieve the class of the component.
     *
     * @return the component's implementation class. Should normally be a concrete class (ie, a class that can be
     *         instantiated).
     */
    Class<T> getComponentImplementation();

    /**
     * Retrieve the component instance. This method will usually create a new instance each time it is called, but that
     * is not required. For example, {@link org.picocontainer.behaviors.Cached} will always return the
     * same instance.
     *
     * @param container the {@link PicoContainer}, that is used to resolve any possible dependencies of the instance.
     * @return the component instance.
     * @throws PicoCompositionException if the component could not be instantiated.
     * @throws PicoCompositionException  if the component has dependencies which could not be resolved, or
     *                                     instantiation of the component lead to an ambigous situation within the
     *                                     container.
     */
    T getComponentInstance(PicoContainer container) throws PicoCompositionException;

    /**
     * Verify that all dependencies for this adapter can be satisifed. Normally, the adapter should verify this by
     * checking that the associated PicoContainer contains all the needed dependnecies.
     *
     * @param container the {@link PicoContainer}, that is used to resolve any possible dependencies of the instance.
     * @throws PicoCompositionException if one or more dependencies cannot be resolved.
     */
    void verify(PicoContainer container) throws PicoCompositionException;

    /**
     * Accepts a visitor for this ComponentAdapter. The method is normally called by visiting a {@link PicoContainer}, that
     * cascades the visitor also down to all its ComponentAdapter instances.
     *
     * @param visitor the visitor.
     */
    void accept(PicoVisitor visitor);

    ComponentAdapter<T> getDelegate();

    <U extends ComponentAdapter> U findAdapterOfType(Class<U> componentAdapterType);

    String getDescriptor();

}
