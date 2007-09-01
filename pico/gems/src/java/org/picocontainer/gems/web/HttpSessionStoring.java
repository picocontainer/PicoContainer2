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
import javax.servlet.http.HttpSession;

/** @author Paul Hammant */
public class HttpSessionStoring {

    private Storing storingBehavior;
    private final String name;

    public HttpSessionStoring(Storing storingBehavior, String name) {
        this.storingBehavior = storingBehavior;
        this.name = name;
    }

    public void retrieveSessionStoreOrCreateNewOne(HttpSession session) {
        Storing.StoreWrapper sr = (Storing.StoreWrapper)session.getAttribute(name);
        if (sr != null) {
            storingBehavior.putCacheForThread(sr);
        } else {
            session.setAttribute(name, storingBehavior.resetCacheForThread());

        }
    }

    public void putStoreInHttpSession(HttpSession session) {
        session.setAttribute(name, storingBehavior.getCacheForThread());
        resetStore();
    }

    public void resetStore() {
        storingBehavior.resetCacheForThread();
    }

    public void invalidateStore() {
        storingBehavior.invalidateCache();
    }

}
