/*****************************************************************************
 * Copyright (C) PicoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the LICENSE.txt file.                                                     *
 *****************************************************************************/

package org.picocontainer.gems.constraints;

import org.picocontainer.ComponentAdapter;
import org.picocontainer.PicoVisitor;
import org.picocontainer.parameters.CollectionComponentParameter;

/**
 * Constraint that collects/aggregates dependencies to as many components
 * that satisfy the given constraint.
 *
 * @author Nick Sieger
 * @author J&ouml;rg Schaible
 * @version 1.1
 */
public final class CollectionConstraint extends CollectionComponentParameter implements Constraint {
    protected final Constraint constraint;

    public CollectionConstraint(Constraint constraint) {
        this(constraint, false);
    }

    public CollectionConstraint(Constraint constraint, boolean emptyCollection) {
        super(Object.class, emptyCollection);
        this.constraint = constraint;
    }

    public boolean evaluate(ComponentAdapter adapter) {
        return constraint.evaluate(adapter);
    }

    public void accept(PicoVisitor visitor) {
        super.accept(visitor);
        constraint.accept(visitor);
    }
}
