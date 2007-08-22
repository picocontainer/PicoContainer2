/*****************************************************************************
 * Copyright (C) PicoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the LICENSE.txt file.                                                     *
 *                                                                           *
 * Original code by Paul Hammant                                             *
 *****************************************************************************/

package org.picocontainer;

/**
 * Behaviors modify the components created by a Injector with additional behaviors
 * 
 * @author Paul Hammant
 * @author J&ouml;rg Schaible
 * @author Mauro Talevi
 * @see LifecycleStrategy
 */
public interface Behavior<T> extends ComponentAdapter<T> {

    /**
     * Invoke the "start" method on the component.
     * @param container the container to "start" the component
     */
    void start(PicoContainer container);

    /**
     * Invoke the "stop" method on the component.
     * @param container the container to "stop" the component
     */
    void stop(PicoContainer container);

    /**
     * Invoke the "dispose" method on the component.
     * @param container the container to "dispose" the component
     */
    void dispose(PicoContainer container);

    /**
     * Test if a component honors a lifecycle.
     * @return <code>true</code> if the component has a lifecycle
     */
    boolean componentHasLifecycle();

}
