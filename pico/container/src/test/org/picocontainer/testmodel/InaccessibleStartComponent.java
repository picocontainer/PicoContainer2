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

import java.util.List;

/**
 * @author Steve.Freeman@m3p.co.uk
 */
public final class InaccessibleStartComponent {
    private final List messages;

    public InaccessibleStartComponent(List messages) {
        this.messages = messages;
    }

    private final void start() {
        messages.add("started");
    }
}
