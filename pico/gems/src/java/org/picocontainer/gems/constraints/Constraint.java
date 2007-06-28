/*****************************************************************************
 * Copyright (C) PicoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the LICENSE.txt file.                                                     *
 *****************************************************************************/

package org.picocontainer.gems.constraints;

import org.picocontainer.ComponentAdapter;
import org.picocontainer.Parameter;

/**
 * Extension to {@link org.picocontainer.Parameter} that allows for
 * constraint-based configuration of component parameters.
 *
 * @author Nick Sieger
 * @version 1.0
 */
public interface Constraint extends Parameter {
    /**
     * Evaluate whether the given component adapter matches this constraint.
     *
     * @param adapter a <code>ComponentAdapter</code> value
     * @return true if the adapter matches the constraint
     */
    boolean evaluate(ComponentAdapter adapter);
}
