package org.nanocontainer.persistence;

/**
 * Thrown when optimistic locking or failure to acquire lock occurs.
 * 
 * @version $Revision: $
 */
public class ConcurrencyFailureException extends PersistenceException {

    public ConcurrencyFailureException(Throwable cause) {
        super(cause);
    }
    
}
