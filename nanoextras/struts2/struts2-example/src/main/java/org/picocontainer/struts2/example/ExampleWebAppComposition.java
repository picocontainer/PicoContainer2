/*****************************************************************************
 * Copyright (c) PicoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the LICENSE.txt file.                                                     *
 *                                                                           *
 *****************************************************************************/

package org.picocontainer.struts2.example;

import org.picocontainer.struts2.WebAppComposition;
import org.picocontainer.MutablePicoContainer;
import org.picocontainer.PicoContainer;
import org.picocontainer.DefaultPicoContainer;
import org.picocontainer.behaviors.Caching;
import org.picocontainer.behaviors.Storing;

public class ExampleWebAppComposition implements WebAppComposition {

    private MutablePicoContainer appContainer;
    private MutablePicoContainer sessionContainer;
    private MutablePicoContainer requestContainer;

    public PicoContainer compose(MutablePicoContainer rootContainer, Storing sessionStoring, Storing requestStoring) {
        appContainer = new DefaultPicoContainer(new Caching(), rootContainer);
        sessionContainer = new DefaultPicoContainer(sessionStoring, appContainer);
        requestContainer = new DefaultPicoContainer(requestStoring, sessionContainer);

        addWebAppComponents();

        return requestContainer;
    }

    private void addWebAppComponents() {
        appContainer.addComponent(StockQuoteService.class);

        sessionContainer.addComponent(RecentQuotes.class);

        requestContainer.addComponent(GetQuote.class);
    }

}
