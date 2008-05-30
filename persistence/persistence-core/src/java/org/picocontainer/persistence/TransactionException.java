package org.picocontainer.persistence;

/**
 * Indicates that a transaction could not be started, committed or rolled back.
 */
@SuppressWarnings("serial")
public class TransactionException extends PersistenceException {

	public TransactionException(Throwable cause) {
		super(cause);
	}

}
