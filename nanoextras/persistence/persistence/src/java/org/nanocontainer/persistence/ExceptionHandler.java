package org.nanocontainer.persistence;

public interface ExceptionHandler {

	public RuntimeException handle(Throwable ex);

}
