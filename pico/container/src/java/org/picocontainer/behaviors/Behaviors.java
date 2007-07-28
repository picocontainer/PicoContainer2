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

import org.picocontainer.behaviors.ImplementationHidingBehaviorFactory;
import org.picocontainer.behaviors.Caching;
import org.picocontainer.behaviors.SynchronizedBehaviorFactory;
import org.picocontainer.BehaviorFactory;
import org.picocontainer.behaviors.PropertyApplyingBehaviorFactory;

public class Behaviors {

    public static BehaviorFactory implHiding() {
        return new ImplementationHidingBehaviorFactory();
    }

    public static BehaviorFactory caching() {
        return new Caching();
    }

    public static BehaviorFactory threadSafe() {
        return new SynchronizedBehaviorFactory();
    }

    public static BehaviorFactory propertyApplier() {
        return new PropertyApplyingBehaviorFactory();
    }

}
