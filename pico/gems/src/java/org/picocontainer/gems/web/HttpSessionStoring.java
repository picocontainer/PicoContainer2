/*****************************************************************************
 * Copyright (C) NanoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the LICENSE.txt file.                                                     *
 *                                                                           *
 *****************************************************************************/

package org.picocontainer.gems.web;

import org.picocontainer.behaviors.Storing;

import javax.servlet.http.HttpServletRequest;

/** @author Paul Hammant */
public class HttpSessionStoring {

    private Storing storingBehavior;
    private final String name;

    public HttpSessionStoring(Storing storingBehavior, String name) {
        this.storingBehavior = storingBehavior;
        this.name = name;
    }

    public void retrieveStoreFromHttpSession(HttpServletRequest req) {
        Storing.StoreWrapper sr = (Storing.StoreWrapper)req.getSession().getAttribute(name);
        if (sr != null) {
            storingBehavior.putCacheForThread(sr);
        }
    }

    public void putStoreInHttpSession(HttpServletRequest req) {
        req.getSession().setAttribute(name, storingBehavior.getCacheForThread());
        flushStore();
    }

    public void flushStore() {
        storingBehavior.flushCacheForThread();
    }

}
