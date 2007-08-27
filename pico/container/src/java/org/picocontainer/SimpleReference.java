package org.picocontainer;

import java.io.Serializable;

/**
 * simple memopry reference. I (KP)  extracted this out of 
 * Cahced (where it was static inner class) because 
 * it is needed elsewhere. 
 * 
 * @author Aslak Helles&oslash;y
 * @author Konstantin Pribluda
 */
public class SimpleReference<T> implements ObjectReference<T>,
		Serializable {
	private T instance;

	public SimpleReference() {
	}

	public T get() {
		return instance;
	}

	public void set(T item) {
		this.instance = item;
	}
}
