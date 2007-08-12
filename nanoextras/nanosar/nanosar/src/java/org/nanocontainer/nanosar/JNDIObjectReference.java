package org.nanocontainer.nanosar;

import javax.naming.InitialContext;
import javax.naming.NamingException;

import org.nanocontainer.integrationkit.PicoCompositionException;
import org.picocontainer.ObjectReference;

/**
 * object reference to store and retrieve objects from JNDI
 * 
 * @author ko5tik
 * 
 */
public class JNDIObjectReference implements ObjectReference {

	String name;

	InitialContext context;

	public JNDIObjectReference(String name, InitialContext context) {
		super();
		this.name = name;
		this.context = context;
	}

	/**
	 * retrieve object from JNDI if possible
	 */
	public Object get() {
		try {
			return context.lookup(name);
		} catch (NamingException e) {
			throw new PicoCompositionException("unable to resolve jndi name:"
					+ name, e);
		}
	}

	/**
	 * store object in JNDI under specified name
	 */
	public void set(Object item) {
		try {
			context.bind(name, item);
		} catch (NamingException e) {
			throw new PicoCompositionException("unable to bind to  jndi name:"
					+ name, e);
		}
	}

}
