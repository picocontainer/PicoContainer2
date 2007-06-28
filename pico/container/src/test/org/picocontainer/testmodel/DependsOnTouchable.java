/*****************************************************************************
 * Copyright (c) PicoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the LICENSE.txt file.                                                     *
 *                                                                           *
 * Idea by Rachel Davies, Original code by Aslak Hellesoy and Paul Hammant   *
 *****************************************************************************/

package org.picocontainer.testmodel;

import junit.framework.Assert;

import java.io.Serializable;

/**
 * @author steve.freeman@m3p.co.uk
 */
public class DependsOnTouchable implements Serializable {
    public final Touchable touchable;

    public DependsOnTouchable(Touchable touchable) {
        Assert.assertNotNull("Touchable cannot be passed in as null", touchable);
        touchable.touch();
        this.touchable = touchable;
    }

    public Touchable getTouchable() {
        return touchable;
    }
}
