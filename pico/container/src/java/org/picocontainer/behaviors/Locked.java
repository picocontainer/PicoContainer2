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

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @author Aslak Helles&oslash;y
 * @author Manish Shah
 * @version $Revision$
 */
public class Locked extends AbstractBehavior {
    private Lock lock = new ReentrantLock();

    public Locked(ComponentAdapter delegate) {
        super(delegate);
    }

    public Object getComponentInstance(PicoContainer container) throws PicoCompositionException {
        Object retVal = null;
        lock.lock();
        try {
          retVal = super.getComponentInstance(container);
        }
        finally {
          lock.unlock();
        }
        return retVal;
    }
    public String toString() {
        return "Locked:" + super.toString();
    }

}