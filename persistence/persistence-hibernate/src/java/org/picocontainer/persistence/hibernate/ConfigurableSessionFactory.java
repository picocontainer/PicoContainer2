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

    private final SessionFactory sessionFactory;

    public ConfigurableSessionFactory(Configuration configuration) {
        try {
            sessionFactory = configuration.buildSessionFactory();
        } catch (HibernateException e) {
            throw new PicoCompositionException(e);
        }
    }

    public SessionFactory getDelegate() {
        return sessionFactory;
    }

    public void close() {
        sessionFactory.close();
    }

    public void evict(Class persistentClass) {
        sessionFactory.evict(persistentClass);
    }

    public void evict(Class persistentClass, Serializable id) {
        sessionFactory.evict(persistentClass, id);
    }

    public void evictCollection(String roleName) {
        sessionFactory.evictCollection(roleName);
    }

    public void evictCollection(String roleName, Serializable id) {
        sessionFactory.evictCollection(roleName, id);
    }

    public void evictEntity(String entityName) {
        sessionFactory.evictEntity(entityName);
    }

    public void evictEntity(String entityName, Serializable id) {
        sessionFactory.evictEntity(entityName, id);
    }

    public void evictQueries() {
        sessionFactory.evictQueries();
    }

    public void evictQueries(String cacheRegion) {
        sessionFactory.evictQueries(cacheRegion);
    }

    public Map getAllClassMetadata() {
        return sessionFactory.getAllClassMetadata();
    }

    public Map getAllCollectionMetadata() {
        return sessionFactory.getAllCollectionMetadata();
    }

    public ClassMetadata getClassMetadata(Class persistentClass) {
        return sessionFactory.getClassMetadata(persistentClass);
    }

    public ClassMetadata getClassMetadata(String entityName) {
        return sessionFactory.getClassMetadata(entityName);
    }

    public CollectionMetadata getCollectionMetadata(String roleName) {
        return sessionFactory.getCollectionMetadata(roleName);
    }

	public Session getCurrentSession() {
		return sessionFactory.getCurrentSession();
	}

    public Set getDefinedFilterNames() {
        return sessionFactory.getDefinedFilterNames();
    }

    public FilterDefinition getFilterDefinition(String filterName) throws HibernateException {
        return sessionFactory.getFilterDefinition(filterName);
    }

    public Reference getReference() throws NamingException {
        return sessionFactory.getReference();
    }

    public Statistics getStatistics() {
        return sessionFactory.getStatistics();
    }

	public boolean isClosed() {
		return sessionFactory.isClosed();
	}

    public Session openSession() {
        return sessionFactory.openSession();
    }

    public Session openSession(Connection connection) {
        return sessionFactory.openSession(connection);
    }

    public Session openSession(Connection connection, Interceptor interceptor) {
        return sessionFactory.openSession(connection, interceptor);
    }

    public Session openSession(Interceptor interceptor) {
        return sessionFactory.openSession(interceptor);
    }

    public StatelessSession openStatelessSession() {
        return sessionFactory.openStatelessSession();
    }

    public StatelessSession openStatelessSession(Connection connection) {
        return sessionFactory.openStatelessSession(connection);
    }

}
