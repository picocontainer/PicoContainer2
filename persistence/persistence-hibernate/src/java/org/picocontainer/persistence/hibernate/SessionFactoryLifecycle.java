/*******************************************************************************
 * Copyright (c) PicoContainer Organization. All rights reserved.
 * ---------------------------------------------------------------------------
 * The software in this package is published under the terms of the BSD style
 * license a copy of which has been included with this distribution in the
 * license.html file.
 ******************************************************************************/

package org.picocontainer.persistence.hibernate;

import org.hibernate.SessionFactory;
import org.picocontainer.Startable;

/**
 * Component organising lifecycle for session factory.
 * 
 * @author Jose Peleteiro <juzepeleteiro@intelli.biz>
 * @author Mauro Talevi
 */
public final class SessionFactoryLifecycle implements Startable {

    private final SessionFactory sessionFactory;

    public SessionFactoryLifecycle(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    public void start() {
    }

    public void stop() {
        if ( sessionFactory != null ){
            sessionFactory.close();            
        }
    }
}
