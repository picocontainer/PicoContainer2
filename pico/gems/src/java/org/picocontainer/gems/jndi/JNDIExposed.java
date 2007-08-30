/*****************************************************************************
 * Copyright (c) PicoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the LICENSE.txt file.                                                     *
 *                                                                           *
 *****************************************************************************/
package org.picocontainer.gems.jndi;

import javax.naming.InitialContext;
import javax.naming.NamingException;

import org.picocontainer.ComponentAdapter;
import org.picocontainer.behaviors.Stored;

import sun.reflect.ReflectionFactory.GetReflectionFactoryAction;

/**
 * exposes component to JNDI basically does same thing as cached, but uses JNDI
 * reference instead. Maybe Cached shall be refactored? as there is little new
 * functionality.
 * 
 * @author k.pribluda
 * 
 */
public class JNDIExposed<T> extends Stored<T> {

	/**
	 * construct reference itself using vanilla initial context.
	 * JNDI name is stringified component key
	 * @param delegate
	 *            delegate adapter

	 * @throws NamingException
	 */
	public JNDIExposed(ComponentAdapter<T> delegate) throws NamingException {
		super(delegate, new JNDIObjectReference<T>(delegate.getComponentKey()
				.toString(), new InitialContext()));
	}

	/**
	 * create with provided reference
	 * 
	 * @param delegate
	 * @param instanceReference
	 */
	public JNDIExposed(ComponentAdapter<T> delegate,
			JNDIObjectReference<T> instanceReference) {
		super(delegate, instanceReference);
	}

	/**
	 * create adapter with desired name
	 * @param delegate
	 * @param name
	 * @throws NamingException
	 */
	public JNDIExposed(ComponentAdapter<T> delegate, String name) throws NamingException {
		super(delegate, new JNDIObjectReference<T>(name, new InitialContext()));
	}
	
	public String toString() {
		return "JNDI" + instanceReference.toString() +  super.toString();
	}
}
