/*****************************************************************************
 * Copyright (c) PicoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the license.html file.                                                    *
 *****************************************************************************/

package org.picocontainer.persistence.hibernate;

import java.io.Serializable;
import java.sql.Connection;
import java.util.Map;
import java.util.Set;

import javax.naming.NamingException;
import javax.naming.Reference;

import org.hibernate.HibernateException;
import org.hibernate.Interceptor;
import org.hibernate.SessionFactory;
import org.hibernate.StatelessSession;
import org.hibernate.cfg.Configuration;
import org.hibernate.classic.Session;
import org.hibernate.engine.FilterDefinition;
import org.hibernate.metadata.ClassMetadata;
import org.hibernate.metadata.CollectionMetadata;
import org.hibernate.stat.Statistics;
import org.picocontainer.PicoCompositionException;

/**
 * Session factory implementation that uses a delegate session factory 
 * created from configuration.
 * 
 * @author Jose Peleteiro
 * @author Mauro Talevi
 */
@SuppressWarnings("serial")
public final class ConfigurableSessionFactory implements SessionFactory {

    private final SessionFactory delegate;

    public ConfigurableSessionFactory(Configuration configuration) {
        try {
            delegate = configuration.buildSessionFactory();
        } catch (HibernateException e) {
            throw new PicoCompositionException(e);
        }
    }

    public SessionFactory getDelegate() {
        return delegate;
    }

    public void close() {
        delegate.close();
    }

    public void evict(Class persistentClass) {
        delegate.evict(persistentClass);
    }

    public void evict(Class persistentClass, Serializable id) {
        delegate.evict(persistentClass, id);
    }

    public void evictCollection(String roleName) {
        delegate.evictCollection(roleName);
    }

    public void evictCollection(String roleName, Serializable id) {
        delegate.evictCollection(roleName, id);
    }

    public void evictEntity(String entityName) {
        delegate.evictEntity(entityName);
    }

    public void evictEntity(String entityName, Serializable id) {
        delegate.evictEntity(entityName, id);
    }

    public void evictQueries() {
        delegate.evictQueries();
    }

    public void evictQueries(String cacheRegion) {
        delegate.evictQueries(cacheRegion);
    }

    public Map getAllClassMetadata() {
        return delegate.getAllClassMetadata();
    }

    public Map getAllCollectionMetadata() {
        return delegate.getAllCollectionMetadata();
    }

    public ClassMetadata getClassMetadata(Class persistentClass) {
        return delegate.getClassMetadata(persistentClass);
    }

    public ClassMetadata getClassMetadata(String entityName) {
        return delegate.getClassMetadata(entityName);
    }

    public CollectionMetadata getCollectionMetadata(String roleName) {
        return delegate.getCollectionMetadata(roleName);
    }

	public Session getCurrentSession() {
		return delegate.getCurrentSession();
	}

    public Set getDefinedFilterNames() {
        return delegate.getDefinedFilterNames();
    }

    public FilterDefinition getFilterDefinition(String filterName) throws HibernateException {
        return delegate.getFilterDefinition(filterName);
    }

    public Reference getReference() throws NamingException {
        return delegate.getReference();
    }

    public Statistics getStatistics() {
        return delegate.getStatistics();
    }

	public boolean isClosed() {
		return delegate.isClosed();
	}

    public Session openSession() {
        return delegate.openSession();
    }

    public Session openSession(Connection connection) {
        return delegate.openSession(connection);
    }

    public Session openSession(Connection connection, Interceptor interceptor) {
        return delegate.openSession(connection, interceptor);
    }

    public Session openSession(Interceptor interceptor) {
        return delegate.openSession(interceptor);
    }

    public StatelessSession openStatelessSession() {
        return delegate.openStatelessSession();
    }

    public StatelessSession openStatelessSession(Connection connection) {
        return delegate.openStatelessSession(connection);
    }

}
