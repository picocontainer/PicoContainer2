/*******************************************************************************
 * Copyright (c) PicoContainer Organization. All rights reserved.
 * ---------------------------------------------------------------------------
 * The software in this package is published under the terms of the BSD style
 * license a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 ******************************************************************************/
package org.picocontainer.web.sample.struts2.pwr;

import org.picocontainer.MutablePicoContainer;
import org.picocontainer.Characteristics;
import org.picocontainer.injectors.ProviderAdapter;
import org.picocontainer.web.WebappComposer;
import org.picocontainer.web.struts2.Struts2Composer;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.ServletContext;

public class Struts2DemoComposer extends Struts2Composer {

    public void composeApplication(MutablePicoContainer applicationContainer, ServletContext context) {
        super.composeApplication(applicationContainer, context);
        applicationContainer.addComponent(CheeseDao.class, InMemoryCheeseDao.class);
    }

    public void composeSession(MutablePicoContainer sessionContainer) {
        sessionContainer.addComponent(CheeseService.class, DefaultCheeseService.class);
    }

    public void composeRequest(MutablePicoContainer requestContainer) {
        requestContainer.as(Characteristics.NO_CACHE).addComponent(Brand.class, Brand.FromRequest.class);

    }

}