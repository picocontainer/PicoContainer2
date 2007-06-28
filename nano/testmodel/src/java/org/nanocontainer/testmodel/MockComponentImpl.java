/*****************************************************************************
 * Copyright (C) NanoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the LICENSE.txt file.                                                     *
 *                                                                           *
 * Original code by Mike Hogan                                               *
 *****************************************************************************/

package org.nanocontainer.testmodel;

import java.util.ArrayList;
import java.util.Collection;

public final class MockComponentImpl implements MockComponent {
    private int port = 0;
    private String server = null;
    private final Collection registers = new ArrayList();

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public String getServer() {
        return server;
    }

    public void setServer(String server) {
        this.server = server;
    }

    public void addRegister(Integer i) {
        registers.add(i);
    }

    public int getNumRegisters() {
        return registers.size();
    }

    public boolean hasRegister(int i) {
        return registers.contains(i);
    }
}
