/******************************************************************************
 * Copyright (c) PicoContainer Organization. All rights reserved. 
 * ---------------------------------------------------------------------------
 * The software in this package is published under the terms of the BSD style
 * license a copy of which has been included with this distribution in the
 * license.html file. 
 ******************************************************************************/

package org.picocontainer.persistence.hibernate;

import java.io.Serializable;
import java.sql.Connection;

import org.hibernate.CacheMode;
import org.hibernate.Criteria;
import org.hibernate.EntityMode;
import org.hibernate.Filter;
import org.hibernate.FlushMode;
import org.hibernate.HibernateException;
import org.hibernate.LockMode;
import org.hibernate.Query;
import org.hibernate.ReplicationMode;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.stat.SessionStatistics;

/**
 * Abstract base class for session delegators. delegates all calls to session
 * obtained by implementing class. error handling is also there. All methods are
 * just delegations to hibernate session.
 */
public abstract class SessionDelegator implements Session {

	/**
	 * Default constructor.
	 */
	public SessionDelegator() {
		super();
	}

	/**
	 * Obtain Hibernate session.
	 * 
	 * @return constructed Hibernate session.
	 */
	public abstract Session getDelegatedSession();

	/**
	 * Perform actions to dispose &quot;burned&quot; session properly.
	 */
	public abstract void invalidateDelegatedSession();

	/**
	 * Invalidates the session calling {@link #invalidateDelegatedSession()} and
	 * just return the <code>cause</code> back.
	 * 
	 * @return
	 * @param cause
	 */
	protected RuntimeException handleException(final RuntimeException cause) {
		try {
			invalidateDelegatedSession();
		} catch (RuntimeException e) {
			return e;
		}
		return cause;
	}

	/** {@inheritDoc} **/
	public Transaction beginTransaction() {
		try {
			return getDelegatedSession().beginTransaction();
		} catch (HibernateException ex) {
			throw handleException(ex);
		}
	}

	/** {@inheritDoc} **/
	public void cancelQuery() {
		try {
			getDelegatedSession().cancelQuery();
		} catch (HibernateException ex) {
			throw handleException(ex);
		}
	}

	/** {@inheritDoc} **/
	public void clear() {
		try {
			getDelegatedSession().clear();
		} catch (HibernateException ex) {
			throw handleException(ex);
		}
	}

	/** {@inheritDoc} **/
	public Connection close() {
		try {
			return getDelegatedSession().close();
		} catch (HibernateException ex) {
			throw handleException(ex);
		}
	}

	/** {@inheritDoc} **/
	@Deprecated
	public Connection connection() {
		try {
			return getDelegatedSession().connection();
		} catch (HibernateException ex) {
			throw handleException(ex);
		}
	}

	/** {@inheritDoc} **/
	public boolean contains(final Object object) {
		try {
			return getDelegatedSession().contains(object);
		} catch (HibernateException ex) {
			throw handleException(ex);
		}
	}

	/** {@inheritDoc} **/
	@SuppressWarnings("unchecked")
	public Criteria createCriteria(final Class persistentClass) {
		try {
			return getDelegatedSession().createCriteria(persistentClass);
		} catch (HibernateException ex) {
			throw handleException(ex);
		}
	}

	/** {@inheritDoc} **/
	@SuppressWarnings("unchecked")
	public Criteria createCriteria(final Class persistentClass,
			final String alias) {
		try {
			return getDelegatedSession().createCriteria(persistentClass, alias);
		} catch (HibernateException ex) {
			throw handleException(ex);
		}
	}

	/** {@inheritDoc} **/
	public Criteria createCriteria(final String entityName) {
		try {
			return getDelegatedSession().createCriteria(entityName);
		} catch (HibernateException ex) {
			throw handleException(ex);
		}
	}

	/** {@inheritDoc} **/
	public Criteria createCriteria(final String entityName, final String alias) {
		try {
			return getDelegatedSession().createCriteria(entityName, alias);
		} catch (HibernateException ex) {
			throw handleException(ex);
		}
	}

	/** {@inheritDoc} **/
	public Query createFilter(final Object collection, final String queryString) {
		try {
			return getDelegatedSession().createFilter(collection, queryString);
		} catch (HibernateException ex) {
			throw handleException(ex);
		}
	}

	/** {@inheritDoc} **/
	public Query createQuery(final String queryString) {
		try {
			return getDelegatedSession().createQuery(queryString);
		} catch (HibernateException ex) {
			throw handleException(ex);
		}
	}

	/** {@inheritDoc} **/
	public SQLQuery createSQLQuery(final String queryString) {
		try {
			return getDelegatedSession().createSQLQuery(queryString);
		} catch (HibernateException ex) {
			throw handleException(ex);
		}
	}

	/** {@inheritDoc} **/
	public void delete(final Object object) {
		try {
			getDelegatedSession().delete(object);
		} catch (HibernateException ex) {
			throw handleException(ex);
		}
	}

	/** {@inheritDoc} **/
	public void delete(final String entityName, final Object object) {
		try {
			getDelegatedSession().delete(entityName, object);
		} catch (HibernateException ex) {
			throw handleException(ex);
		}
	}

	/** {@inheritDoc} **/
	public void disableFilter(final String filterName) {
		try {
			getDelegatedSession().disableFilter(filterName);
		} catch (HibernateException ex) {
			throw handleException(ex);
		}
	}

	/** {@inheritDoc} **/
	public Connection disconnect() {
		try {
			return getDelegatedSession().disconnect();
		} catch (HibernateException ex) {
			throw handleException(ex);
		}
	}

	/** {@inheritDoc} **/
	public Filter enableFilter(final String filterName) {
		try {
			return getDelegatedSession().enableFilter(filterName);
		} catch (HibernateException ex) {
			throw handleException(ex);
		}
	}

	/** {@inheritDoc} **/
	public void evict(final Object object) {
		try {
			getDelegatedSession().evict(object);
		} catch (HibernateException ex) {
			throw handleException(ex);
		}
	}

	/** {@inheritDoc} **/
	public void flush() {
		try {
			getDelegatedSession().flush();
		} catch (HibernateException ex) {
			throw handleException(ex);
		}
	}

	/** {@inheritDoc} **/
	@SuppressWarnings("unchecked")
	public Object get(final Class clazz, final Serializable id) {
		try {
			return getDelegatedSession().get(clazz, id);
		} catch (HibernateException ex) {
			throw handleException(ex);
		}
	}

	/** {@inheritDoc} **/
	@SuppressWarnings("unchecked")
	public Object get(final Class clazz, final Serializable id,
			final LockMode lockMode) {
		try {
			return getDelegatedSession().get(clazz, id, lockMode);
		} catch (HibernateException ex) {
			throw handleException(ex);
		}
	}

	/** {@inheritDoc} **/
	public Object get(final String entityName, final Serializable id) {
		try {
			return getDelegatedSession().get(entityName, id);
		} catch (HibernateException ex) {
			throw handleException(ex);
		}
	}

	/** {@inheritDoc} **/
	public Object get(final String entityName, final Serializable id,
			final LockMode lockMode) {
		try {
			return getDelegatedSession().get(entityName, id, lockMode);
		} catch (HibernateException ex) {
			throw handleException(ex);
		}
	}

	/** {@inheritDoc} **/
	public CacheMode getCacheMode() {
		try {
			return getDelegatedSession().getCacheMode();
		} catch (HibernateException ex) {
			throw handleException(ex);
		}
	}

	/** {@inheritDoc} **/
	public LockMode getCurrentLockMode(final Object object) {
		try {
			return getDelegatedSession().getCurrentLockMode(object);
		} catch (HibernateException ex) {
			throw handleException(ex);
		}
	}

	/** {@inheritDoc} **/
	public Filter getEnabledFilter(final String filterName) {
		try {
			return getDelegatedSession().getEnabledFilter(filterName);
		} catch (HibernateException ex) {
			throw handleException(ex);
		}
	}

	/** {@inheritDoc} **/
	public EntityMode getEntityMode() {
		try {
			return getDelegatedSession().getEntityMode();
		} catch (HibernateException ex) {
			throw handleException(ex);
		}
	}

	/** {@inheritDoc} **/
	public String getEntityName(final Object object) {
		try {
			return getDelegatedSession().getEntityName(object);
		} catch (HibernateException ex) {
			throw handleException(ex);
		}
	}

	/** {@inheritDoc} **/
	public FlushMode getFlushMode() {
		try {
			return getDelegatedSession().getFlushMode();
		} catch (HibernateException ex) {
			throw handleException(ex);
		}
	}

	/** {@inheritDoc} **/
	public Serializable getIdentifier(final Object object) {
		try {
			return getDelegatedSession().getIdentifier(object);
		} catch (HibernateException ex) {
			throw handleException(ex);
		}
	}

	/** {@inheritDoc} **/
	public Query getNamedQuery(final String queryName) {
		try {
			return getDelegatedSession().getNamedQuery(queryName);
		} catch (HibernateException ex) {
			throw handleException(ex);
		}
	}

	/** {@inheritDoc} **/
	public Session getSession(final EntityMode entityMode) {
		try {
			return getDelegatedSession().getSession(entityMode);
		} catch (HibernateException ex) {
			throw handleException(ex);
		}
	}

	/** {@inheritDoc} **/
	public SessionFactory getSessionFactory() {
		try {
			return getDelegatedSession().getSessionFactory();
		} catch (HibernateException ex) {
			throw handleException(ex);
		}
	}

	/** {@inheritDoc} **/
	public SessionStatistics getStatistics() {
		try {
			return getDelegatedSession().getStatistics();
		} catch (HibernateException ex) {
			throw handleException(ex);
		}
	}

	/** {@inheritDoc} **/
	public Transaction getTransaction() {
		try {
			return getDelegatedSession().getTransaction();
		} catch (HibernateException ex) {
			throw handleException(ex);
		}
	}

	/** {@inheritDoc} **/
	public boolean isConnected() {
		try {
			return getDelegatedSession().isConnected();
		} catch (HibernateException ex) {
			throw handleException(ex);
		}
	}

	/** {@inheritDoc} **/
	public boolean isDirty() {
		try {
			return getDelegatedSession().isDirty();
		} catch (HibernateException ex) {
			throw handleException(ex);
		}
	}

	/** {@inheritDoc} **/
	public boolean isOpen() {
		try {
			return getDelegatedSession().isOpen();
		} catch (HibernateException ex) {
			throw handleException(ex);
		}
	}

	/** {@inheritDoc} **/
	@SuppressWarnings("unchecked")
	public Object load(final Class theClass, final Serializable id) {
		try {
			return getDelegatedSession().load(theClass, id);
		} catch (HibernateException ex) {
			throw handleException(ex);
		}
	}

	/** {@inheritDoc} **/
	@SuppressWarnings("unchecked")
	public Object load(final Class theClass, final Serializable id,
			final LockMode lockMode) {
		try {
			return getDelegatedSession().load(theClass, id, lockMode);
		} catch (HibernateException ex) {
			throw handleException(ex);
		}
	}

	/** {@inheritDoc} **/
	public void load(final Object object, final Serializable id) {
		try {
			getDelegatedSession().load(object, id);
		} catch (HibernateException ex) {
			throw handleException(ex);
		}
	}

	/** {@inheritDoc} **/
	public Object load(final String entityName, final Serializable id) {
		try {
			return getDelegatedSession().load(entityName, id);
		} catch (HibernateException ex) {
			throw handleException(ex);
		}
	}

	/** {@inheritDoc} **/
	public Object load(final String entityName, final Serializable id,
			final LockMode lockMode) {
		try {
			return getDelegatedSession().load(entityName, id, lockMode);
		} catch (HibernateException ex) {
			throw handleException(ex);
		}
	}

	/** {@inheritDoc} **/
	public void lock(final Object object, final LockMode lockMode) {
		try {
			getDelegatedSession().lock(object, lockMode);
		} catch (HibernateException ex) {
			throw handleException(ex);
		}
	}

	/** {@inheritDoc} **/
	public void lock(final String entityEntity, final Object object,
			final LockMode lockMode) {
		try {
			getDelegatedSession().lock(entityEntity, object, lockMode);
		} catch (HibernateException ex) {
			throw handleException(ex);
		}
	}

	/** {@inheritDoc} **/
	public Object merge(final Object object) {
		try {
			return getDelegatedSession().merge(object);
		} catch (HibernateException ex) {
			throw handleException(ex);
		}
	}

	/** {@inheritDoc} **/
	public Object merge(final String entityName, final Object object) {
		try {
			return getDelegatedSession().merge(entityName, object);
		} catch (HibernateException ex) {
			throw handleException(ex);
		}
	}

	/** {@inheritDoc} **/
	public void persist(final Object object) {
		try {
			getDelegatedSession().persist(object);
		} catch (HibernateException ex) {
			throw handleException(ex);
		}
	}

	/** {@inheritDoc} **/
	public void persist(final String entityName, final Object object) {
		try {
			getDelegatedSession().persist(entityName, object);
		} catch (HibernateException ex) {
			throw handleException(ex);
		}
	}

	/** {@inheritDoc} **/
	@Deprecated
	public void reconnect() {
		try {
			getDelegatedSession().reconnect();
		} catch (HibernateException ex) {
			throw handleException(ex);
		}
	}

	/** {@inheritDoc} **/
	public void reconnect(final Connection conn) {
		try {
			getDelegatedSession().reconnect(conn);
		} catch (HibernateException ex) {
			throw handleException(ex);
		}
	}

	/** {@inheritDoc} **/
	public void refresh(final Object object) {
		try {
			getDelegatedSession().refresh(object);
		} catch (HibernateException ex) {
			throw handleException(ex);
		}
	}

	/** {@inheritDoc} **/
	public void refresh(final Object object, final LockMode lockMode) {
		try {
			getDelegatedSession().refresh(object, lockMode);
		} catch (HibernateException ex) {
			throw handleException(ex);
		}
	}

	/** {@inheritDoc} **/
	public void replicate(final Object object,
			final ReplicationMode replicationMode) {
		try {
			getDelegatedSession().replicate(object, replicationMode);
		} catch (HibernateException ex) {
			throw handleException(ex);
		}
	}

	/** {@inheritDoc} **/
	public void replicate(final String entityName, final Object object,
			final ReplicationMode replicationMode) {
		try {
			getDelegatedSession()
					.replicate(entityName, object, replicationMode);
		} catch (HibernateException ex) {
			throw handleException(ex);
		}
	}

	/** {@inheritDoc} **/
	public Serializable save(final Object object) {
		try {
			return getDelegatedSession().save(object);
		} catch (HibernateException ex) {
			throw handleException(ex);
		}
	}

	/** {@inheritDoc} **/
	public Serializable save(final String entityName, final Object object) {
		try {
			return getDelegatedSession().save(entityName, object);
		} catch (HibernateException ex) {
			throw handleException(ex);
		}
	}

	/** {@inheritDoc} **/
	public void saveOrUpdate(final Object object) {
		try {
			getDelegatedSession().saveOrUpdate(object);
		} catch (HibernateException ex) {
			throw handleException(ex);
		}
	}

	/** {@inheritDoc} **/
	public void saveOrUpdate(final String entityName, final Object object) {
		try {
			getDelegatedSession().saveOrUpdate(entityName, object);
		} catch (HibernateException ex) {
			throw handleException(ex);
		}
	}

	/** {@inheritDoc} **/
	public void setCacheMode(final CacheMode cacheMode) {
		try {
			getDelegatedSession().setCacheMode(cacheMode);
		} catch (HibernateException ex) {
			throw handleException(ex);
		}
	}

	/** {@inheritDoc} **/
	public void setReadOnly(final Object entity, final boolean readOnly) {
		try {
			getDelegatedSession().setReadOnly(entity, readOnly);
		} catch (HibernateException ex) {
			throw handleException(ex);
		}
	}

	/** {@inheritDoc} **/
	public void setFlushMode(final FlushMode value) {
		try {
			getDelegatedSession().setFlushMode(value);
		} catch (HibernateException ex) {
			throw handleException(ex);
		}
	}

	/** {@inheritDoc} **/
	public void update(final Object object) {
		try {
			getDelegatedSession().update(object);
		} catch (HibernateException ex) {
			throw handleException(ex);
		}
	}

	/** {@inheritDoc} **/
	public void update(final String entityName, final Object object) {
		try {
			getDelegatedSession().update(entityName, object);
		} catch (HibernateException ex) {
			throw handleException(ex);
		}
	}

}
