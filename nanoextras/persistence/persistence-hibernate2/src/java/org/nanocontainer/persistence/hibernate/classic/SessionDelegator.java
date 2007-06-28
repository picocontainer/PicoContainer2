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

import java.io.Serializable;
import java.sql.Connection;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.nanocontainer.persistence.hibernate.classic.HibernateExceptionHandler;
import org.nanocontainer.persistence.ExceptionHandler;

import net.sf.hibernate.Criteria;
import net.sf.hibernate.FlushMode;
import net.sf.hibernate.HibernateException;
import net.sf.hibernate.LockMode;
import net.sf.hibernate.Query;
import net.sf.hibernate.ReplicationMode;
import net.sf.hibernate.Session;
import net.sf.hibernate.SessionFactory;
import net.sf.hibernate.Transaction;
import net.sf.hibernate.type.Type;

/**
 * Abstract base class for session delegators, which delegates all calls to 
 * session obtained by implementing class. 
 * Also does error handling. All methods are just delegations to a Hibernate session.
 * 
 * @author Konstantin Pribluda
 * @author Jose Peleteiro
 * @version $Version: $
 */
public abstract class SessionDelegator implements Session {

	private HibernateExceptionHandler exceptionHandler;

	public SessionDelegator() {
		exceptionHandler = null;
	}

	/**
	 * @param exceptionHandler Exception handler component to use with created session
	 */
	public SessionDelegator(HibernateExceptionHandler exceptionHandler) {
		this.exceptionHandler = exceptionHandler;
	}

	/**
	 * obtain hibernate session.
     * @return
     */
	protected abstract Session getDelegatedSession();

	/**
	 * perform actions to dispose "burned" session properly
     * @throws HibernateException
     */
	protected abstract void invalidateDelegatedSession() throws HibernateException;

	/**
	 * Invalidates the session calling {@link #invalidateDelegatedSession()} and convert the <code>cause</code> using
	 * a {@link ExceptionHandler} if it's available otherwise throws the <code>cause</code> back.
     * @param cause
     * @return
     * @throws HibernateException
     */
	protected RuntimeException handleException(HibernateException cause) throws HibernateException {
		try {
			invalidateDelegatedSession();
		} catch (HibernateException e) {
			// Do nothing, the only original cause should be reported.
		}

		if (exceptionHandler == null) {
			throw cause;
		}

		return exceptionHandler.handle(cause);
	}

	/**
	 * Invalidates the session calling {@link #invalidateDelegatedSession()} and convert the <code>cause</code> using
	 * a {@link ExceptionHandler} if it's available otherwise just return the <code>cause</code> back.
     * @return
     * @param cause
     */
	protected RuntimeException handleException(RuntimeException cause) {
		try {
			invalidateDelegatedSession();
		} catch (HibernateException e) {
			// Do nothing, the only original cause should be reported.
		}

		if (exceptionHandler == null) {
			return cause;
		}

		return exceptionHandler.handle(cause);
	}

	public void flush() throws HibernateException {
		try {
			getDelegatedSession().flush();
		} catch (HibernateException ex) {
			throw handleException(ex);
		} catch (RuntimeException ex) {
			throw handleException(ex);
		}
	}

	public void setFlushMode(FlushMode flushMode) {
		try {
			getDelegatedSession().setFlushMode(flushMode);
		} catch (RuntimeException ex) {
			throw handleException(ex);
		}
	}

	public FlushMode getFlushMode() {
		try {
			return getDelegatedSession().getFlushMode();
		} catch (RuntimeException ex) {
			throw handleException(ex);
		}
	}

	public SessionFactory getSessionFactory() {
		try {
			return getDelegatedSession().getSessionFactory();
		} catch (RuntimeException ex) {
			throw handleException(ex);
		}
	}

	public Connection connection() throws HibernateException {
		try {
			return getDelegatedSession().connection();
		} catch (HibernateException ex) {
			throw handleException(ex);
		} catch (RuntimeException ex) {
			throw handleException(ex);
		}
	}

	public Connection disconnect() throws HibernateException {
		try {
			return getDelegatedSession().disconnect();
		} catch (HibernateException ex) {
			throw handleException(ex);
		} catch (RuntimeException ex) {
			throw handleException(ex);
		}
	}

	public void reconnect() throws HibernateException {
		try {
			getDelegatedSession().reconnect();
		} catch (HibernateException ex) {
			throw handleException(ex);
		} catch (RuntimeException ex) {
			throw handleException(ex);
		}
	}

	public void reconnect(Connection connection) throws HibernateException {
		try {
			getDelegatedSession().reconnect(connection);
		} catch (HibernateException ex) {
			throw handleException(ex);
		} catch (RuntimeException ex) {
			throw handleException(ex);
		}
	}

	public Connection close() throws HibernateException {
		try {
			return getDelegatedSession().close();
		} catch (HibernateException ex) {
			throw handleException(ex);
		} catch (RuntimeException ex) {
			throw handleException(ex);
		}
	}

	public void cancelQuery() throws HibernateException {
		try {
			getDelegatedSession().cancelQuery();
		} catch (HibernateException ex) {
			throw handleException(ex);
		} catch (RuntimeException ex) {
			throw handleException(ex);
		}
	}

	public boolean isOpen() {
		try {
			return getDelegatedSession().isOpen();
		} catch (RuntimeException ex) {
			throw handleException(ex);
		}
	}

	public boolean isConnected() {
		try {
			return getDelegatedSession().isConnected();
		} catch (RuntimeException ex) {
			throw handleException(ex);
		}
	}

	public boolean isDirty() throws HibernateException {
		try {
			return getDelegatedSession().isDirty();
		} catch (HibernateException ex) {
			throw handleException(ex);
		} catch (RuntimeException ex) {
			throw handleException(ex);
		}
	}

	public Serializable getIdentifier(Object object) throws HibernateException {
		try {
			return getDelegatedSession().getIdentifier(object);
		} catch (HibernateException ex) {
			throw handleException(ex);
		} catch (RuntimeException ex) {
			throw handleException(ex);
		}
	}

	public boolean contains(Object object) {
		try {
			return getDelegatedSession().contains(object);
		} catch (RuntimeException ex) {
			throw handleException(ex);
		}
	}

	public void evict(Object object) throws HibernateException {
		try {
			getDelegatedSession().evict(object);
		} catch (HibernateException ex) {
			throw handleException(ex);
		} catch (RuntimeException ex) {
			throw handleException(ex);
		}
	}

	public Object load(Class theClass, Serializable id, LockMode lockMode) throws HibernateException {
		try {
			return getDelegatedSession().load(theClass, id, lockMode);
		} catch (HibernateException ex) {
			throw handleException(ex);
		} catch (RuntimeException ex) {
			throw handleException(ex);
		}
	}

	public Object load(Class theClass, Serializable id) throws HibernateException {
		try {
			return getDelegatedSession().load(theClass, id);
		} catch (HibernateException ex) {
			throw handleException(ex);
		} catch (RuntimeException ex) {
			throw handleException(ex);
		}
	}

	public void load(Object object, Serializable id) throws HibernateException {
		try {
			getDelegatedSession().load(object, id);
		} catch (HibernateException ex) {
			throw handleException(ex);
		} catch (RuntimeException ex) {
			throw handleException(ex);
		}
	}

	public void replicate(Object object, ReplicationMode replicationMode) throws HibernateException {
		try {
			getDelegatedSession().replicate(object, replicationMode);
		} catch (HibernateException ex) {
			throw handleException(ex);
		} catch (RuntimeException ex) {
			throw handleException(ex);
		}
	}

	public Serializable save(Object object) throws HibernateException {
		try {

			return getDelegatedSession().save(object);
		} catch (HibernateException ex) {
			throw handleException(ex);
		} catch (RuntimeException ex) {
			throw handleException(ex);
		}
	}

	public void save(Object object, Serializable id) throws HibernateException {
		try {
			getDelegatedSession().save(object, id);
		} catch (HibernateException ex) {
			throw handleException(ex);
		} catch (RuntimeException ex) {
			throw handleException(ex);
		}
	}

	public void saveOrUpdate(Object object) throws HibernateException {
		try {
			getDelegatedSession().saveOrUpdate(object);
		} catch (HibernateException ex) {
			throw handleException(ex);
		} catch (RuntimeException ex) {
			throw handleException(ex);
		}
	}

	public void update(Object object) throws HibernateException {
		try {
			getDelegatedSession().update(object);
		} catch (HibernateException ex) {
			throw handleException(ex);
		} catch (RuntimeException ex) {
			throw handleException(ex);
		}
	}

	public void update(Object object, Serializable id) throws HibernateException {
		try {
			getDelegatedSession().update(object, id);
		} catch (HibernateException ex) {
			throw handleException(ex);
		} catch (RuntimeException ex) {
			throw handleException(ex);
		}
	}

	public Object saveOrUpdateCopy(Object object) throws HibernateException {
		try {
			return getDelegatedSession().saveOrUpdateCopy(object);
		} catch (HibernateException ex) {
			throw handleException(ex);
		} catch (RuntimeException ex) {
			throw handleException(ex);
		}
	}

	public Object saveOrUpdateCopy(Object object, Serializable id) throws HibernateException {
		try {
			return getDelegatedSession().saveOrUpdateCopy(object, id);
		} catch (HibernateException ex) {
			throw handleException(ex);
		} catch (RuntimeException ex) {
			throw handleException(ex);
		}
	}

	public void delete(Object object) throws HibernateException {
		try {
			getDelegatedSession().delete(object);
		} catch (HibernateException ex) {
			throw handleException(ex);
		} catch (RuntimeException ex) {
			throw handleException(ex);
		}
	}

	public List find(String query) throws HibernateException {
		try {
			return getDelegatedSession().find(query);
		} catch (HibernateException ex) {
			throw handleException(ex);
		} catch (RuntimeException ex) {
			throw handleException(ex);
		}
	}

	public List find(String query, Object value, Type type) throws HibernateException {
		try {
			return getDelegatedSession().find(query, value, type);
		} catch (HibernateException ex) {
			throw handleException(ex);
		} catch (RuntimeException ex) {
			throw handleException(ex);
		}
	}

	public List find(String query, Object[] values, Type[] types) throws HibernateException {
		try {
			return getDelegatedSession().find(query, values, types);
		} catch (HibernateException ex) {
			throw handleException(ex);
		} catch (RuntimeException ex) {
			throw handleException(ex);
		}
	}

	public Iterator iterate(String query) throws HibernateException {
		try {
			return getDelegatedSession().iterate(query);
		} catch (HibernateException ex) {
			throw handleException(ex);
		} catch (RuntimeException ex) {
			throw handleException(ex);
		}
	}

	public Iterator iterate(String query, Object value, Type type) throws HibernateException {
		try {
			return getDelegatedSession().iterate(query, value, type);
		} catch (HibernateException ex) {
			throw handleException(ex);
		} catch (RuntimeException ex) {
			throw handleException(ex);
		}
	}

	public Iterator iterate(String query, Object[] values, Type[] types) throws HibernateException {
		try {
			return getDelegatedSession().iterate(query, values, types);
		} catch (HibernateException ex) {
			throw handleException(ex);
		} catch (RuntimeException ex) {
			throw handleException(ex);
		}
	}

	public Collection filter(Object collection, String filter) throws HibernateException {
		try {
			return getDelegatedSession().filter(collection, filter);
		} catch (HibernateException ex) {
			throw handleException(ex);
		} catch (RuntimeException ex) {
			throw handleException(ex);
		}
	}

	public Collection filter(Object collection, String filter, Object value, Type type) throws HibernateException {
		try {
			return getDelegatedSession().filter(collection, filter, value, type);
		} catch (HibernateException ex) {
			throw handleException(ex);
		} catch (RuntimeException ex) {
			throw handleException(ex);
		}
	}

	public Collection filter(Object collection, String filter, Object[] values, Type[] types) throws HibernateException {
		try {
			return getDelegatedSession().filter(collection, filter, values, types);
		} catch (HibernateException ex) {
			throw handleException(ex);
		} catch (RuntimeException ex) {
			throw handleException(ex);
		}
	}

	public int delete(String query) throws HibernateException {
		try {
			return getDelegatedSession().delete(query);
		} catch (HibernateException ex) {
			throw handleException(ex);
		} catch (RuntimeException ex) {
			throw handleException(ex);
		}
	}

	public int delete(String query, Object value, Type type) throws HibernateException {
		try {
			return getDelegatedSession().delete(query, value, type);
		} catch (HibernateException ex) {
			throw handleException(ex);
		} catch (RuntimeException ex) {
			throw handleException(ex);
		}

	}

	public int delete(String query, Object[] values, Type[] types) throws HibernateException {
		try {
			return getDelegatedSession().delete(query, values, types);
		} catch (HibernateException ex) {
			throw handleException(ex);
		} catch (RuntimeException ex) {
			throw handleException(ex);
		}
	}

	public void lock(Object object, LockMode lockMode) throws HibernateException {
		try {
			getDelegatedSession().lock(object, lockMode);
		} catch (HibernateException ex) {
			throw handleException(ex);
		} catch (RuntimeException ex) {
			throw handleException(ex);
		}
	}

	public void refresh(Object object) throws HibernateException {
		try {
			getDelegatedSession().refresh(object);
		} catch (HibernateException ex) {
			throw handleException(ex);
		} catch (RuntimeException ex) {
			throw handleException(ex);
		}
	}

	public void refresh(Object object, LockMode lockMode) throws HibernateException {
		try {
			getDelegatedSession().refresh(object, lockMode);
		} catch (HibernateException ex) {
			throw handleException(ex);
		} catch (RuntimeException ex) {
			throw handleException(ex);
		}
	}

	public LockMode getCurrentLockMode(Object object) throws HibernateException {
		try {
			return getDelegatedSession().getCurrentLockMode(object);
		} catch (HibernateException ex) {
			throw handleException(ex);
		} catch (RuntimeException ex) {
			throw handleException(ex);
		}
	}

	public Transaction beginTransaction() throws HibernateException {
		try {
			return getDelegatedSession().beginTransaction();
		} catch (HibernateException ex) {
			throw handleException(ex);
		} catch (RuntimeException ex) {
			throw handleException(ex);
		}
	}

	public Criteria createCriteria(Class persistentClass) {
		try {
			return getDelegatedSession().createCriteria(persistentClass);
		} catch (RuntimeException ex) {
			throw handleException(ex);
		}
	}

	public Query createQuery(String queryString) throws HibernateException {
		try {
			return getDelegatedSession().createQuery(queryString);
		} catch (HibernateException ex) {
			throw handleException(ex);
		} catch (RuntimeException ex) {
			throw handleException(ex);
		}
	}

	public Query createFilter(Object collection, String queryString) throws HibernateException {
		try {
			return getDelegatedSession().createFilter(collection, queryString);
		} catch (HibernateException ex) {
			throw handleException(ex);
		} catch (RuntimeException ex) {
			throw handleException(ex);
		}
	}

	public Query getNamedQuery(String queryName) throws HibernateException {
		try {
			return getDelegatedSession().getNamedQuery(queryName);
		} catch (HibernateException ex) {
			throw handleException(ex);
		} catch (RuntimeException ex) {
			throw handleException(ex);
		}
	}

	public Query createSQLQuery(String sql, String returnAlias, Class returnClass) {
		try {
			return getDelegatedSession().createSQLQuery(sql, returnAlias, returnClass);
		} catch (RuntimeException ex) {
			throw handleException(ex);
		}
	}

	public Query createSQLQuery(String sql, String[] returnAliases, Class[] returnClasses) {
		try {
			return getDelegatedSession().createSQLQuery(sql, returnAliases, returnClasses);
		} catch (RuntimeException ex) {
			throw handleException(ex);
		}
	}

	public void clear() {
		try {
			getDelegatedSession().clear();
		} catch (RuntimeException ex) {
			throw handleException(ex);
		}
	}

	public Object get(Class clazz, Serializable id) throws HibernateException {
		try {
			return getDelegatedSession().get(clazz, id);
		} catch (HibernateException ex) {
			throw handleException(ex);
		} catch (RuntimeException ex) {
			throw handleException(ex);
		}
	}

	public Object get(Class clazz, Serializable id, LockMode lockMode) throws HibernateException {
		try {
			return getDelegatedSession().get(clazz, id, lockMode);
		} catch (HibernateException ex) {
			throw handleException(ex);
		} catch (RuntimeException ex) {
			throw handleException(ex);
		}
	}
}
