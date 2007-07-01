/*****************************************************************************
 * Copyright (C) PicoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the LICENSE.txt file.                                                     *
 *                                                                           *
 * Original code by                                                          *
 *****************************************************************************/
package org.picocontainer.doc.tutorial.blocks;

import org.picocontainer.doc.introduction.Peelable;
import org.picocontainer.doc.introduction.Peeler;

// START SNIPPET: class

public class JuicerBean {
    private Peelable peelable;
    private Peeler peeler;
    protected Peelable getPeelable() {
        return this.peelable;
    }
    protected void setPeelable(Peelable peelable) {
        this.peelable = peelable;
    }
    protected void setPeeler(Peeler peeler) {
        this.peeler = peeler;
    }
    protected Peeler getPeeler() {
        return this.peeler;
    }

}

// END SNIPPET: class
