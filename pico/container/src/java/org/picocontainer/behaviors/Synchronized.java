/*****************************************************************************
 * Copyright (C) PicoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the LICENSE.txt file.                                                     *
 *                                                                           *
 * Original code by                                                          *
 *****************************************************************************/
package org.picocontainer.behaviors;


import org.picocontainer.ComponentAdapter;
import org.picocontainer.PicoContainer;
import org.picocontainer.PicoCompositionException;
import org.picocontainer.behaviors.AbstractBehavior;

/**
 * Component Adapter that uses java synchronized around getComponentInstance().
 * @author Aslak Helles&oslash;y
 * @author Manish Shah
 */
public class Synchronized<T> extends AbstractBehavior<T> {
    /**
	 * Serialization UUID.
	 */
	private static final long serialVersionUID = -3984071461712339652L;

	public Synchronized(ComponentAdapter<T> delegate) {
        super(delegate);
    }

    public synchronized T getComponentInstance(PicoContainer container) throws PicoCompositionException {
        return super.getComponentInstance(container);
    }

    public String getDescriptor() {
        return "Synchronized"; 
    }

}
