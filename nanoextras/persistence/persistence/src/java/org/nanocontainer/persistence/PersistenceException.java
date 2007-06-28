package org.nanocontainer.persistence;

import org.picocontainer.PicoException;

/**
 * Base for all persistence related exceptions.
 * 
 * @version $Revision: $
 */
public class PersistenceException extends PicoException {

	public PersistenceException(Throwable cause) {
		super(cause);
	}

}
