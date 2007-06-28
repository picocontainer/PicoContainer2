/*****************************************************************************
 * Copyright (C) NanoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the LICENSE.txt file.                                                     *
 *                                                                           *
 *                                                                           *
 *****************************************************************************/

package org.nanocontainer.webcontainer.groovy;

import org.picocontainer.MutablePicoContainer;
import org.nanocontainer.webcontainer.PicoJettyServer;
import org.nanocontainer.script.groovy.buildernodes.AbstractBuilderNode;
import org.nanocontainer.NanoContainer;

import java.util.Map;

public class WebContainerBuilder extends AbstractBuilderNode {


    public WebContainerBuilder() {
        super("webContainer");
    }

    public Object createNewNode(Object current, Map map) {
        int port = 0;
        if (map.containsKey("port")) {
            port = (Integer)map.remove("port");
        }
        String host;
        if (map.containsKey("host")) {
            host = (String) map.remove("host");
        } else {
            host = "localhost";
        }

        MutablePicoContainer parentContainer = (NanoContainer) current;

        PicoJettyServer server;
        if (port != 0) {
            server = new PicoJettyServer(host, port, parentContainer);
        } else {
            server = new PicoJettyServer(parentContainer);
        }
        parentContainer.addChildContainer(server);
        return new ServerBuilder(server, parentContainer);
    }


}


