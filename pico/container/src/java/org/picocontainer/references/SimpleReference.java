package org.picocontainer.references;

import java.io.Serializable;

import org.picocontainer.ObjectReference;

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
