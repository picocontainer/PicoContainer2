/*****************************************************************************
 * Copyright (c) PicoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the license.html file.                                                    *
 *****************************************************************************/

package org.picocontainer.persistence.hibernate;

import org.hibernate.Session;
import org.picocontainer.Startable;

/**
 * Component providing session lifecycle to be registered in container containing session in
 * question.
 * 
 * @author Jose Peleteiro <juzepeleteiro@intelli.biz>
 */
public final class SessionLifecycle implements Startable {

    private final Session session;

    public SessionLifecycle(Session session) {
        this.session = session;
    }

    public void start() {}

    public void stop() {
        if (session != null ){
            session.flush();
            session.close();            
        }
    }

}
