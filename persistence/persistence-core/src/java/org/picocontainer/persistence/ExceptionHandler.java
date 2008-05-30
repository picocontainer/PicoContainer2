package org.picocontainer.persistence;

public interface ExceptionHandler {

	public RuntimeException handle(Throwable ex);

}
