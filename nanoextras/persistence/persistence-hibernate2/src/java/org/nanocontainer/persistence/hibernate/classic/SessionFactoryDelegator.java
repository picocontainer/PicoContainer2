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
import java.util.Map;
import javax.naming.NamingException;
import javax.naming.Reference;
import net.sf.hibernate.cfg.Configuration;
import net.sf.hibernate.exception.SQLExceptionConverter;
import net.sf.hibernate.Databinder;
import net.sf.hibernate.HibernateException;
import net.sf.hibernate.Interceptor;
import net.sf.hibernate.metadata.ClassMetadata;
import net.sf.hibernate.metadata.CollectionMetadata;
import net.sf.hibernate.Session;
import net.sf.hibernate.SessionFactory;
import org.picocontainer.PicoCompositionException;
/** 
 * delegates everything to session factory obtained from confiuration.
 * this class is necessary because component adapters are really ugly when
 * it comes to scripting 
 * 
 * 
 * @author Konstantin Pribluda
 * @version $Revision: 2043 $ 
 */

public class SessionFactoryDelegator implements SessionFactory {
    
    private SessionFactory delegate;
    
    public SessionFactoryDelegator(Configuration configuration) {
        try {
            delegate = configuration.buildSessionFactory();
        } catch(HibernateException ex) {
            throw new PicoCompositionException(ex);
        }
    }
    
    public Session openSession(Connection connection) {
        return delegate.openSession(connection);
    }
    
    public Session openSession(Interceptor interceptor) throws HibernateException {
        return  delegate.openSession(interceptor);
    }
    
    
    public Session openSession(Connection connection, Interceptor interceptor) {
        return  delegate.openSession( connection,interceptor);
    }
    
    public Session openSession() throws HibernateException {
         return  delegate.openSession();
    }
    
    public Databinder openDatabinder() throws HibernateException {
        return delegate.openDatabinder();
    }
    
    public ClassMetadata getClassMetadata(Class persistentClass) throws HibernateException {
        return delegate.getClassMetadata(persistentClass);
    }
    
    public CollectionMetadata getCollectionMetadata(String roleName) throws HibernateException {
        return delegate.getCollectionMetadata(roleName);
    }
    
    public Map getAllClassMetadata() throws HibernateException {
        return delegate.getAllClassMetadata();
    }
    
    public Map getAllCollectionMetadata() throws HibernateException {
        return delegate.getAllCollectionMetadata();
    }
    
    public void close() throws HibernateException {
        try {
            delegate.close();
        } finally {
            delegate = null;
        }
    }
    
    public void evict(Class persistentClass) throws HibernateException {
        delegate.evict(persistentClass);
    }
    
    public void evict(Class persistentClass, Serializable id) throws HibernateException {
         delegate.evict(persistentClass,id);
    }
    
    
    public void evictCollection(String roleName) throws HibernateException {
        delegate.evictCollection(roleName);
    }
    
    public void evictCollection(String roleName, Serializable id) throws HibernateException {
        delegate.evictCollection(roleName,id);
    }
    
    
    public void evictQueries() throws HibernateException {
        delegate.evictQueries();
    }
    
    public void evictQueries(String cacheRegion) throws HibernateException {
        delegate.evictQueries(cacheRegion);
    }
    
    public Reference getReference() throws NamingException {
        return delegate.getReference();
    }

	public SQLExceptionConverter getSQLExceptionConverter() {
		return delegate.getSQLExceptionConverter();
	}
}
