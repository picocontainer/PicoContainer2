package org.nanocontainer.persistence;

/**
 * Indicates that a transaction could not be begun, committed or rolled back.
 * 
 * @version $Revision: $
 */
public class TransactionException extends PersistenceException {

	public TransactionException(Throwable cause) {
		super(cause);
	}

}
