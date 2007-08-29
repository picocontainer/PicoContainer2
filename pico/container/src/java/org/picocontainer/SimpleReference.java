package org.picocontainer;

import java.io.Serializable;

/**
 * Simple instance implementation of ObjectReference. 
 * 
 * @author Aslak Helles&oslash;y
 * @author Konstantin Pribluda
 */
@SuppressWarnings("serial")
public class SimpleReference<T> implements ObjectReference<T>,
		Serializable {
	private T instance;

	public SimpleReference() {
	    // no-op
	}

	public T get() {
		return instance;
	}

	public void set(T item) {
		this.instance = item;
	}
}
