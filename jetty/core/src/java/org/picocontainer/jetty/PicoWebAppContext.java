/*****************************************************************************
 * Copyright (C) NanoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the LICENSE.txt file.                                                     *
 *                                                                           *
 *****************************************************************************/

package org.nanocontainer.webcontainer;

import org.mortbay.jetty.webapp.WebAppContext;
import org.mortbay.jetty.webapp.WebXmlConfiguration;
import org.mortbay.jetty.webapp.Configuration;
import org.mortbay.jetty.servlet.ServletHandler;
import org.picocontainer.PicoContainer;
import org.nanocontainer.webcontainer.PicoServletHandler;

public class PicoWebAppContext extends WebAppContext {
    private final PicoContainer parentContainer;

    public PicoWebAppContext(PicoContainer parentContainer) {
             super(null,null,new PicoServletHandler(parentContainer),null);
        this.parentContainer = parentContainer;
    }

    protected void loadConfigurations() throws Exception {
        super.loadConfigurations();
        Configuration[]  configurations = getConfigurations();
        for (int i = 0; i < configurations.length; i++) {
            if (configurations[i] instanceof WebXmlConfiguration) {
                configurations[i] = new PicoWebXmlConfiguration(parentContainer);
            }
        }
        setConfigurations(configurations);
    }

    /* ------------------------------------------------------------ */
    public ServletHandler getServletHandler() {
        return super.getServletHandler();
    }
}
