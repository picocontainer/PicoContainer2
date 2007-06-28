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
import org.hibernate.SessionFactory;
import org.picocontainer.Startable;

/**
 * Component organising lifecycle for session factory.
 * 
 * @author Jose Peleteiro <juzepeleteiro@intelli.biz>
 * @version $Revision: 2043 $
 */
public final class SessionFactoryLifecycle implements Startable {

    private final SessionFactory sessionFactory;

    public SessionFactoryLifecycle(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    public void start() {
        //Does nothing to start.
        
    }

    public void stop() {
        try {
				sessionFactory.close();
        } catch (HibernateException ex) {
            // swallow it? not sure what to do with it...
        }
    }
}
