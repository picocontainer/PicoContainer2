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

// START SNIPPET: boy

public class Boy implements Kissable {
    public void kiss(Object kisser) {
        System.out.println("I was kissed by " + kisser);
    }
}

// END SNIPPET: boy
