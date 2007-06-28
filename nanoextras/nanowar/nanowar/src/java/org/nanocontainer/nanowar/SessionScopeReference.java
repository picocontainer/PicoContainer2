/*****************************************************************************
 * Copyright (C) NanoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the LICENSE.txt file.                                                     *
 *                                                                           *
 *****************************************************************************/
package org.nanocontainer.nanowar;

import org.picocontainer.ObjectReference;

import javax.servlet.http.HttpSession;
import java.io.Serializable;

/**
 * References an object that lives as an attribute of the
 * HttpSession.
 *
 * @author Joe Walnes
 */
public final class SessionScopeReference implements ObjectReference, Serializable {

    //The only reason this class is Serializable and the 'session' field is transient
    //is so that if this class is used as a key in a PicoContainer (as it is in the
    //nanocontainer servlet framework), it won't break serializability of the
    //container. The deserialized class won't be reused for its actual purpose, but
    //discarded. As such, there is no need to resurrect the transient session field
    private final transient HttpSession session;
    private final String key;

    public SessionScopeReference(HttpSession session, String key) {
        this.session = session;
        this.key = key;
    }

    public void set(Object item) {
        session.setAttribute(key, item);
    }

    public Object get() {
        return session.getAttribute(key);
    }

}
