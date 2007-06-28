/*****************************************************************************
 * Copyright (c) PicoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the license.html file.                                                    *
 *                                                                           *
 * Idea by Rachel Davies, Original code by Aslak Hellesoy and Paul Hammant   *
 *****************************************************************************/

package org.nanocontainer.persistence.hibernate;

import java.io.Serializable;
import java.sql.Connection;
import java.util.Map;

import javax.naming.NamingException;
import javax.naming.Reference;

import org.hibernate.HibernateException;
import org.hibernate.Interceptor;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.hibernate.classic.Session;
import org.hibernate.metadata.ClassMetadata;
import org.hibernate.metadata.CollectionMetadata;
import org.hibernate.stat.Statistics;
import org.picocontainer.PicoCompositionException;

/**
 * Delegates everything to session factory obtained from confiuration. this class is necessary
 * because component adapters are really ugly when it comes to scripting.
 * 
 * @version $Id: SessionFactoryDelegator.java 2158 2005-07-08 02:13:36Z juze $
 */
public final class SessionFactoryDelegator implements SessionFactory {

    private final SessionFactory sessionFactory;

    public SessionFactoryDelegator(Configuration configuration) {
        try {
            this.sessionFactory = configuration.buildSessionFactory();
        } catch (HibernateException ex) {
            throw new PicoCompositionException(ex);
        }
    }

    public SessionFactory getDelegatedSessionFactory() {
        return this.sessionFactory;
    }

    public void close() {
        this.getDelegatedSessionFactory().close();
    }

    public void evict(Class persistentClass) {
        this.getDelegatedSessionFactory().evict(persistentClass);
    }

    public void evict(Class persistentClass, Serializable id) {
        this.getDelegatedSessionFactory().evict(persistentClass, id);
    }

    public void evictCollection(String roleName) {
        this.getDelegatedSessionFactory().evictCollection(roleName);
    }

    public void evictCollection(String roleName, Serializable id) {
        this.getDelegatedSessionFactory().evictCollection(roleName, id);
    }

    public void evictEntity(String entityName) {
        this.getDelegatedSessionFactory().evictEntity(entityName);
    }

    public void evictEntity(String entityName, Serializable id) {
        this.getDelegatedSessionFactory().evictEntity(entityName, id);
    }

    public void evictQueries() {
        this.getDelegatedSessionFactory().evictQueries();
    }

    public void evictQueries(String cacheRegion) {
        this.getDelegatedSessionFactory().evictQueries(cacheRegion);
    }

    public Map getAllClassMetadata() {
        return this.getDelegatedSessionFactory().getAllClassMetadata();
    }

    public Map getAllCollectionMetadata() {
        return this.getDelegatedSessionFactory().getAllCollectionMetadata();
    }

    public ClassMetadata getClassMetadata(Class persistentClass) {
        return this.getDelegatedSessionFactory().getClassMetadata(persistentClass);
    }

    public ClassMetadata getClassMetadata(String entityName) {
        return this.getDelegatedSessionFactory().getClassMetadata(entityName);
    }

    public CollectionMetadata getCollectionMetadata(String roleName) {
        return this.getDelegatedSessionFactory().getCollectionMetadata(roleName);
    }

	public Session getCurrentSession() {
		return this.getDelegatedSessionFactory().getCurrentSession();
	}

    public Reference getReference() throws NamingException {
        return this.getDelegatedSessionFactory().getReference();
    }

    public Statistics getStatistics() {
        return this.getDelegatedSessionFactory().getStatistics();
    }

	public boolean isClosed() {
		return this.getDelegatedSessionFactory().isClosed();
	}

    public Session openSession() {
        return this.getDelegatedSessionFactory().openSession();
    }

    public Session openSession(Connection connection) {
        return this.getDelegatedSessionFactory().openSession(connection);
    }

    public Session openSession(Connection connection, Interceptor interceptor) {
        return this.getDelegatedSessionFactory().openSession(connection, interceptor);
    }

    public Session openSession(Interceptor interceptor) {
        return this.getDelegatedSessionFactory().openSession(interceptor);
    }

}
