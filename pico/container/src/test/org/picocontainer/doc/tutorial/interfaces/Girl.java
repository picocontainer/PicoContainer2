/*****************************************************************************
 * Copyright (C) PicoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the LICENSE.txt file.                                                     *
 *                                                                           *
 * Original code by                                                          *
 *****************************************************************************/
package org.picocontainer.doc.tutorial.interfaces;

// START SNIPPET: girl

public final class Girl {
    final Kissable kissable;

    public Girl(Kissable kissable) {
        this.kissable = kissable;
    }

    public void kissSomeone() {
        kissable.kiss(this);
    }
}

// END SNIPPET: girl
