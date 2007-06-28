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
 * @author Aslak Helles&oslash;y
 * @author Manish Shah
 * @version $Revision$
 */
public class SynchronizedBehavior extends AbstractBehavior {
    public SynchronizedBehavior(ComponentAdapter delegate) {
        super(delegate);
    }

    public synchronized Object getComponentInstance(PicoContainer container) throws
                                                                             PicoCompositionException
    {
        return super.getComponentInstance(container);
    }
}
