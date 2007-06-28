/*****************************************************************************
 * Copyright (c) PicoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the license.html file.                                                    *
 *                                                                           *
 * Idea by Rachel Davies, Original code by Aslak Hellesoy and Paul Hammant   *
 *****************************************************************************/
package org.nanocontainer.persistence.hibernate.classic;

import net.sf.hibernate.SessionFactory;
import net.sf.hibernate.HibernateException;

import org.picocontainer.Startable;

/**
 * component organising lifecycle for session factory
 * @author Konstanti Pribluda
 * @version $Revision: 2043 $ 
 */
public final class SessionFactoryLifecycle implements Startable {
	final SessionFactory sessionFactory;
	
	public SessionFactoryLifecycle(SessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
	}
	
	public void start() {
	}
	
	public void stop() {
		try {
		sessionFactory.close();
		} catch(HibernateException ex) {
			//swallow it? not sure what to do with it...
		}
	}
}


