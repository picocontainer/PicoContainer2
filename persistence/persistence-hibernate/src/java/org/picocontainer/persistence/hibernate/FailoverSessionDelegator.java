/*****************************************************************************
 * Copyright (c) PicoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the license.html file.                                                    *
 *****************************************************************************/

package org.picocontainer.persistence.hibernate;

import java.sql.Connection;

import org.hibernate.HibernateException;
import org.hibernate.Interceptor;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.picocontainer.Disposable;
import org.picocontainer.Startable;

/**
 * Session delegator with failover behaviour in case of hibernate exception. Old
 * session is disposed and new one is obtained transparently. Session creation
 * is done lazily.
 * <p>This component supports PicoContainer lifecycle events.  If you register
 * this component within either a Request PicoContainer or a Request-Scoped storage
 * object, then this component will close sessions whenever stop() is called by the container, and
 * is reusable until dispose() is called.</p>
 * <p>This allows for the &quot;One Session Per Requst&quot; pattern often used in Hibernate.<p>
 * 
 * @author Jose Peleteiro <juzepeleteiro@intelli.biz>
 * @author Michael Rimov
 * @author Mauro Talevi
 */
@SuppressWarnings("serial")
public final class FailoverSessionDelegator extends SessionDelegator implements Startable, Disposable {

	/**
	 * Session factory that supplies the delegate sessions.
	 */
	private final SessionFactory sessionFactory;

	/**
	 * Current delegate session, lazily created.
	 */
	private Session session = null;

	/**
	 * Session-specific interceptor.
	 */
	private Interceptor interceptor = null;

	/**
	 * Flag indicating object should not be used again.
	 */
	private boolean disposed = false;

	/**
	 * @param sessionFactory
	 *            session factory to obtain session from
	 */
	public FailoverSessionDelegator(final SessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
	}

	/**
	 * @param sessionFactory
	 *            sessionf actory to obtain session from
	 * @param interceptor
	 *            interceptor to use with created session
	 */
	public FailoverSessionDelegator(final SessionFactory sessionFactory,
			final Interceptor interceptor) {
		this(sessionFactory);
		setInterceptor(interceptor);
	}

	/** {@inheritDoc} */
	@Override
	public SessionFactory getSessionFactory() {
		return sessionFactory;
	}

	/**
	 * Obtain hibernate session in lazy way.
	 */
	@Override
	public Session getDelegatedSession() {
		if (disposed) {
			throw new IllegalStateException(
					"Component has already been disposed by parent container.");
		}

		if (session == null) {
			try {
				session = interceptor == null ? sessionFactory.openSession()
						: sessionFactory.openSession(interceptor);
			} catch (RuntimeException ex) {
				throw handleException(ex);
			}
		}

		return session;
	}

	/**
	 *  {@inheritDoc} 
	 * 	<p>Because this implementation decorates a delegate session, it removes the delegate session, but it does allow
	 *  re-referencing once close() has been called.  It simply grabs a new Hibernate session.</p>
	 */
	@Override
	public Connection close() {
		try {
			return getDelegatedSession().close();
		} catch (HibernateException ex) {
			session = null;
			throw handleException(ex);
		} finally {
			session = null;
		}
	}

	@Override
	public void invalidateDelegatedSession() {
		if (session != null) {
			try {
				session.clear();
				session.close();
			} catch (HibernateException ex) {
				session = null;
				throw handleException(ex);
			} finally {
				session = null;
			}
		}
	}

	/**
	 * Retrieves the current Hibernate interceptor.
	 * 
	 * @return
	 */
	public Interceptor getInterceptor() {
		return interceptor;
	}

	/**
	 * Sets a new hibernate session interceptor. This is only applicable if
	 * there is no current session. If this session object has been used, then
	 * please call close() first.
	 * 
	 * @param interceptor
	 *            new interceptor to apply to this session.
	 * @throws IllegalStateException
	 *             if this session has already been utilized after creation.
	 */
	public void setInterceptor(final Interceptor interceptor)
			throws IllegalStateException {
		if (session != null) {
			throw new IllegalStateException(
					"Cannot apply interceptor after session has been utilized");
		}

		this.interceptor = interceptor;
	}

	/**
	 * Add some insurance against potential memory leaks. Make sure that Session
	 * is closed.
	 * <p>
	 * {@inheritDoc}
	 * </p>
	 */
	@Override
	protected void finalize() throws Throwable {
		try {
			if (session != null) {
				session.close();
			}
		} finally {
			super.finalize();
		}
	}

	/** {@inheritDoc} * */
	@Override
	public int hashCode() {
		if (session == null) {
			return 13;
		}

		return session.hashCode();

	}

	/** {@inheritDoc} * */
	@Override
	public String toString() {
		return FailoverSessionDelegator.class.getName()
				+ " With current Hibernate Session of: " + session;
	}

	/**
	 * {@inheritDoc}
	 * <p>
	 * Currently does nothing. Session is lazily created .
	 * </p>
	 */
	public void start() {
		// currently does nothing. Session is lazily created
	}

	/**
	 * {@inheritDoc}
	 * <p>
	 * Closes and invalidates any sessions that are still open.
	 * </p>
	 */
	public void stop() {
		this.close();

	}

	/**
	 * {@inheritDoc}
	 * <p>
	 * Prevents any further utilization once called.
	 * </p>
	 */
	public void dispose() {
		if (this.session != null) {
			close();
		}

		disposed = true;
	}

}
