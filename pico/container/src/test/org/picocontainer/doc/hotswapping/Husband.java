/*****************************************************************************
 * Copyright (C) PicoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the LICENSE.txt file.                                                     *
 *                                                                           *
 * Original code by                                                          *
 *****************************************************************************/
package org.picocontainer.doc.hotswapping;

/**
 * @author Aslak Helles&oslash;y
 */
// START SNIPPET: class
public class Husband implements Man {
    public final Woman woman;

    public Husband(Woman woman) {
        this.woman = woman;
    }

    public int getEndurance() {
        return 10;
    }
}

// START SNIPPET: class
