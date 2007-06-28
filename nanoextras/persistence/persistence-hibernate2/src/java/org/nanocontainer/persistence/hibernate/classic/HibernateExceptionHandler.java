package org.nanocontainer.persistence.hibernate.classic;

import net.sf.hibernate.StaleObjectStateException;
import net.sf.hibernate.TransactionException;
import net.sf.hibernate.UnresolvableObjectException;
import net.sf.hibernate.WrongClassException;

import net.sf.hibernate.exception.LockAcquisitionException;
import org.nanocontainer.persistence.ExceptionFactory;


/**
 * ExceptionHandler version for Hibernate 2.
 * 
 * @version $Revision: $
 * @see org.nanocontainer.persistence.ExceptionHandler
 */
public final class HibernateExceptionHandler {

    private final ExceptionFactory exceptionFactory;

    public HibernateExceptionHandler(ExceptionFactory exceptionFactory) {
        this.exceptionFactory = exceptionFactory;
    }

    public RuntimeException handle(Throwable ex) {
        
        // Optimistic locking cases.
        if (ex instanceof StaleObjectStateException) {
            StaleObjectStateException e = (StaleObjectStateException) ex;
            return exceptionFactory.createStaleObjectStateException(e, e.getPersistentClass().getName(), e.getIdentifier());
        }

        if (ex instanceof LockAcquisitionException) {
        	LockAcquisitionException e = (LockAcquisitionException) ex;
            return exceptionFactory.createConcurrencyFailureException(e);
        }
        
        // Object retrieval failure cases.
        if (ex instanceof UnresolvableObjectException) {
            UnresolvableObjectException e = (UnresolvableObjectException) ex;
            return exceptionFactory.createObjectRetrievalFailureException(e, e.getPersistentClass().getName(), e.getIdentifier());
        }

        if (ex instanceof WrongClassException) {
            WrongClassException e = (WrongClassException) ex;
            return exceptionFactory.createObjectRetrievalFailureException(e, e.getPersistentClass().getName(), e.getIdentifier());
        }

        // Transaction
        if (ex instanceof TransactionException) {
        	return exceptionFactory.createTransactionException(ex); 
        }

        // Otherwise, return a generic persistence exception
        return exceptionFactory.createPersistenceException(ex);
    }

}