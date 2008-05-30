package org.picocontainer.persistence;

import org.picocontainer.PicoException;

/**
 * Base for all persistence related exceptions.
 */
@SuppressWarnings("serial")
public class PersistenceException extends PicoException {

	public PersistenceException(Throwable cause) {
		super(cause);
	}

}
