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

import java.sql.Connection;
import net.sf.hibernate.HibernateException;
import net.sf.hibernate.Interceptor;
import net.sf.hibernate.Session;
import net.sf.hibernate.SessionFactory;
import org.picocontainer.PicoCompositionException;

/**
 * session delegator with failover behaviour in case of hibernate exception. old session is disposed and new one is
 * obtained transparently. session creation is done lazily.
 * 
 * @author Konstantin Pribluda
 * @author Jose Peleteiro
 * @version $Revision: 2043 $
 */
public class FailoverSessionDelegator extends SessionDelegator {

	Interceptor interceptor = null;
	Session session = null;
	SessionFactory sessionFactory;

	/**
	 * @param sessionFactory session factory to obtain session from 
	 */
	public FailoverSessionDelegator(SessionFactory sessionFactory) {
		setSessionFactory(sessionFactory);
	}

	/**
	 * @param sessionFactory session factory to obtain session from 
	 * @param exceptionHandler Exception handler component to use with created session
	 */
	public FailoverSessionDelegator(SessionFactory sessionFactory, HibernateExceptionHandler exceptionHandler) {
		super(exceptionHandler);
		setSessionFactory(sessionFactory);
	}

	/**
	 * @param sessionFactory sessionf actory to obtain session from
	 * @param interceptor interceptor to use with created session
	 */
	public FailoverSessionDelegator(SessionFactory sessionFactory, Interceptor interceptor) {
		this(sessionFactory);
		setInterceptor(interceptor);
	}

	/**
	 * @param sessionFactory sessionf actory to obtain session from
	 * @param interceptor interceptor to use with created session
	 * @param exceptionHandler Exception handler component to use with created session
	 */
	public FailoverSessionDelegator(SessionFactory sessionFactory, Interceptor interceptor, HibernateExceptionHandler exceptionHandler) {
		this(sessionFactory, exceptionHandler);
		setInterceptor(interceptor);
	}

	public Connection close() throws HibernateException {
		Connection retval = null;
		try {
			retval = getDelegatedSession().close();
		} catch (HibernateException ex) {
			session = null;
			throw ex;
		} finally {
			session = null;
		}

		return retval;
	}

	/**
	 * obtain hibernate session in lazy way. use interceptor if configured
	 */
	public Session getDelegatedSession() {
		if (session == null) {
			try {
				session = interceptor == null ? sessionFactory.openSession() : sessionFactory.openSession(interceptor);
			} catch (HibernateException ex) {
				throw new PicoCompositionException(ex);
			}
		}

		return session;
	}

	public Interceptor getInterceptor() {
		return interceptor;
	}

	public SessionFactory getSessionFactory() {
		return sessionFactory;
	}

	public void invalidateDelegatedSession() throws HibernateException {
		if (session != null) {
			try {
				session.clear();
				session.close();
			} catch (HibernateException ex) {
				session = null;
				throw ex;
			} finally {
				session = null;
			}
		}
	}

	public void setInterceptor(Interceptor interceptor) {
		this.interceptor = interceptor;
	}

	public void setSessionFactory(SessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
	}
}
