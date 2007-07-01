/*****************************************************************************
 * Copyright (C) PicoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the LICENSE.txt file.                                                     *
 *                                                                           *
 * Original code by                                                          *
 *****************************************************************************/
package org.picocontainer.doc.introduction;

import org.picocontainer.Startable;


// START SNIPPET: class

public class Peeler implements Startable {
    private final Peelable peelable;

    public Peeler(Peelable peelable) {
        this.peelable = peelable;
    }

    public void start() {
        peelable.peel();
    }

    public void stop() {

    }
}

// END SNIPPET: class
