/*****************************************************************************
 * Copyright (C) PicoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the LICENSE.txt file.                                                     *
 *****************************************************************************/

package org.picocontainer.gems.constraints;

import org.picocontainer.ComponentAdapter;

/**
 * Constraint that accepts an adapter of a specific key.
 *
 * @author Nick Sieger
 * @version 1.1
 */
public final class IsKey extends AbstractConstraint {

    private final Object key;

    /**
     * Creates a new <code>IsKey</code> instance.
     *
     * @param key the key to match
     */
    public IsKey(Object key) {
        this.key = key;
    }

    public boolean evaluate(ComponentAdapter adapter) {
        return key.equals(adapter.getComponentKey());
    }

}
