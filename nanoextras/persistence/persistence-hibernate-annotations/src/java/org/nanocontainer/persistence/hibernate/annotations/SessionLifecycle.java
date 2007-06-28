/*****************************************************************************
 * Copyright (c) PicoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the license.html file.                                                    *
 *                                                                           *
 * Idea by Rachel Davies, Original code by Aslak Hellesoy and Paul Hammant   *
 *****************************************************************************/

package org.nanocontainer.persistence.hibernate.annotations;

import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.picocontainer.Startable;

/**
 * Component providing session lifecycle to be registered in container containing session in
 * question.
 * 
 * @author Jose Peleteiro <juzepeleteiro@intelli.biz>
 * @version $Revision: 2043 $
 */
public final class SessionLifecycle implements Startable {

    private final Session session;

    public SessionLifecycle(Session session) {
        this.session = session;
    }

    public void start() {
        //Does nothing to start.
    }

    public void stop() {
        try {
            session.flush();
            session.close();
        } catch (HibernateException ex) {
            // swallow it? not sure what to do with it...
        }
    }

}
