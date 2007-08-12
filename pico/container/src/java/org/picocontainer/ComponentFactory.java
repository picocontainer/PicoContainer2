/*****************************************************************************
 * Copyright (C) PicoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the LICENSE.txt file.                                                     *
 *                                                                           *
 * Original code by                                                          *
 *****************************************************************************/
package org.picocontainer;

import org.picocontainer.ComponentAdapter;
import org.picocontainer.Parameter;
import org.picocontainer.PicoCompositionException;
import org.picocontainer.ComponentMonitor;
import org.picocontainer.LifecycleStrategy;

import java.util.Properties;

/**
 * <p/>
 * A component factory is responsible for creating
 * {@link ComponentAdapter} component adapters. The main use of the component factory is
 * inside {@link DefaultPicoContainer#DefaultPicoContainer(ComponentFactory)}, where it can
 * be used to customize the default component adapter that is used when none is specified
 * explicitly.
 * </p>
 *
 * @author Paul Hammant
 * @author Mauro Talevi
 * @author Jon Tirs&eacute;n
 */
public interface ComponentFactory {

    /**
     * Create a new component adapter based on the specified arguments.
     * 
     * @param componentMonitor the component monitor
     * @param lifecycleStrategy te lifecycle strategy
     * @param componentProperties the component properties
     * @param componentKey the key to be associated with this adapter. This
     *            value should be returned from a call to
     *            {@link ComponentAdapter#getComponentKey()} on the created
     *            adapter.
     * @param componentImplementation the implementation class to be associated
     *            with this adapter. This value should be returned from a call
     *            to {@link ComponentAdapter#getComponentImplementation()} on
     *            the created adapter. Should not be null.
     * @param parameters additional parameters to use by the component adapter
     *            in constructing component instances. These may be used, for
     *            example, to make decisions about the arguments passed into the
     *            component constructor. These should be considered hints; they
     *            may be ignored by some implementations. May be null, and may
     *            be of zero length.
     * @return a new component adapter based on the specified arguments. Should
     *         not return null.
     * @throws PicoCompositionException if the creation of the component adapter
     *             results in a {@link PicoCompositionException}.
     * @return The component adapter
     */
    <T> ComponentAdapter<T> createComponentAdapter(ComponentMonitor componentMonitor,
                                            LifecycleStrategy lifecycleStrategy,
                                            Properties componentProperties,
                                            Object componentKey,
                                            Class<T> componentImplementation,
                                            Parameter... parameters) throws PicoCompositionException;


}
