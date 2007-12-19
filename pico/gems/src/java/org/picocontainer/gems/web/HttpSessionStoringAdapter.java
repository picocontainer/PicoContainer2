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

import javax.servlet.http.HttpSession;

/** @author Paul Hammant */
public class HttpSessionStoringAdapter {

    private Storing storingBehavior;
    private final String name;

    public HttpSessionStoringAdapter(Storing storingBehavior, String name) {
        this.storingBehavior = storingBehavior;
        this.name = name;
    }

    public synchronized void retrieveOrCreateStore(HttpSession session) {
        Storing.StoreWrapper sr = (Storing.StoreWrapper)session.getAttribute(name);
        if (sr != null) {
            storingBehavior.putCacheForThread(sr);
        } else {
            session.setAttribute(name, storingBehavior.resetCacheForThread());

        }
    }

    public void resetStore() {
        storingBehavior.resetCacheForThread();
    }

    public void invalidateStore() {
        storingBehavior.invalidateCacheForThread();
    }

}
