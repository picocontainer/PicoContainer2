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

import org.picocontainer.behaviors.ImplementationHiding;
import org.picocontainer.behaviors.Caching;
import org.picocontainer.behaviors.Synchronizing;
import org.picocontainer.BehaviorFactory;
import org.picocontainer.behaviors.PropertyApplying;

public class Behaviors {

    public static ImplementationHiding implHiding() {
        return new ImplementationHiding();
    }

    public static Caching caching() {
        return new Caching();
    }

    public static Synchronizing synchronizing() {
        return new Synchronizing();
    }

    public static PropertyApplying propertyApplying() {
        return new PropertyApplying();
    }

}
