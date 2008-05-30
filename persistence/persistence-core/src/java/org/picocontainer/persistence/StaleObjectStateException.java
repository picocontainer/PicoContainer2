package org.picocontainer.persistence;

/**
 * A specialization of {@link org.picocontainer.persistence.ConcurrencyFailureException ConcurrencyFailureException}
 * which is thrown when a version number or timestamp check failed or try delete or update a row that does not exist
 * anymore.
 */
@SuppressWarnings("serial")
public final class StaleObjectStateException extends ConcurrencyFailureException {

	private final String entityName;
	private final Object objectId;

	public StaleObjectStateException(Throwable cause, String entityName, Object objectId) {
		super(cause);
		this.entityName = entityName;
		this.objectId = objectId;
	}

	public String getEntityName() {
		return entityName;
	}

	public Object getObjectId() {
		return objectId;
	}

}
