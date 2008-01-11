/*****************************************************************************
 * Copyright (C) NanoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the LICENSE.txt file.                                                     *
 *                                                                           *
 * Original code by                                                          *
 *****************************************************************************/
package org.nanocontainer.tools.ant;

import junit.framework.Assert;

/**
 *
 * @author Aslak Helles&oslash;y
 * @version $Revision$
 */
public class Ping {
    private String prop;
    boolean wasExecuted = false;

    public void setSomeprop(String prop) {
        this.prop = prop;
    }

    public void execute() {
        Assert.assertEquals("The property should be set", "HELLO", prop);
        wasExecuted = true;
    }
}
