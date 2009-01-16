/*******************************************************************************
 * Copyright (c) PicoContainer Organization. All rights reserved.
 * ---------------------------------------------------------------------------
 * The software in this package is published under the terms of the BSD style
 * license a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 ******************************************************************************/
package org.picocontainer.web.sample;

import org.picocontainer.MutablePicoContainer;
import org.picocontainer.Characteristics;
import org.picocontainer.injectors.ProviderAdapter;
import org.picocontainer.web.WebappComposer;
import org.picocontainer.web.sample.dao.CheeseDao;
import org.picocontainer.web.sample.dao.simple.InMemoryCheeseDao;
import org.picocontainer.web.sample.service.defaults.DefaultCheeseService;
import org.picocontainer.web.sample.service.CheeseService;
import org.picocontainer.web.sample.model.Brand;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.ServletContext;

public class ExampleWebappComposer implements WebappComposer {

    public void composeApplication(MutablePicoContainer applicationContainer, ServletContext context) {
        applicationContainer.addComponent(CheeseDao.class, InMemoryCheeseDao.class);
    }

    public void composeSession(MutablePicoContainer sessionContainer) {
        sessionContainer.addComponent(CheeseService.class, DefaultCheeseService.class);
    }

    public void composeRequest(MutablePicoContainer requestContainer) {
        requestContainer.as(Characteristics.NO_CACHE).addComponent(Brand.class, Brand.FromRequest.class);

    }
    
}
