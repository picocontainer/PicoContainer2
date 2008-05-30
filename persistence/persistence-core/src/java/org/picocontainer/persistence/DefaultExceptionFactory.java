package org.picocontainer.persistence;


/**
 * Default factory for the persistence exceptions 
 */
public class DefaultExceptionFactory implements ExceptionFactory {

	public RuntimeException createPersistenceException(Throwable cause) {
		if (cause instanceof PersistenceException) {
			return (PersistenceException) cause;
		}

		return new PersistenceException(cause);
	}

	public RuntimeException createConcurrencyFailureException(Throwable cause) {
		return new ConcurrencyFailureException(cause);
	}

	public RuntimeException createStaleObjectStateException(Throwable cause, String type, Object id) {
		return new StaleObjectStateException(cause, type, id);
	}

	public RuntimeException createObjectRetrievalFailureException(Throwable cause, String type, Object id) {
		return new ObjectRetrievalFailureException(cause, type, id);
	}

	public RuntimeException createTransactionException(Throwable cause) {
		return new TransactionException(cause);
	}
}
