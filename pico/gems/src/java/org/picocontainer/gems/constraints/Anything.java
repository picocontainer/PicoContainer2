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
 * A constraint that matches any component adapter.
 *
 * @author Nick Sieger
 */
public class Anything extends AbstractConstraint {
    /**
	 * 
	 */
	private static final long serialVersionUID = 142557419655786599L;
	public static final Anything ANYTHING = new Anything();

    public Anything() {
    }

    @Override
	public boolean evaluate(final ComponentAdapter adapter) {
        return true;
    }
}
