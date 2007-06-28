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

import net.sf.hibernate.Session;
import net.sf.hibernate.HibernateException;

import org.picocontainer.Startable;

/**
 * component providing session lifecycle to be registered in container containing session
 * in question
 * @author Konstantin Pribluda
 * @version $Revision: 2043 $ 
 */
public final class SessionLifecycle implements Startable {
	final Session session;
	
	public SessionLifecycle(Session session) {
		this.session = session;
	}
	
	public void start() {
	}
	
	public void stop() {
		try {
			session.flush();
			session.close();
		} catch(HibernateException ex) {
			//swallow it? not sure what to do with it...
		}
	}
}


