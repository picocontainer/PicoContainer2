package org.picocontainer.persistence;

/**
 * Thrown when optimistic locking or failure to acquire lock occurs.
 */
@SuppressWarnings("serial")
public class ConcurrencyFailureException extends PersistenceException {

    public ConcurrencyFailureException(Throwable cause) {
        super(cause);
    }
    
}
