package org.picocontainer.gems.jndi;

import javax.naming.InitialContext;
import javax.naming.NamingException;

import org.picocontainer.ComponentAdapter;
import org.picocontainer.behaviors.Stored;

/**
 * exposes component to JNDI basically does same thing as cached,
 * but uses JNDI reference instead. Maybe Cached shall be 
 * refactored? as there is little new functionality. 
 * @author k.pribluda
 *
 */
public class JNDIExposed<T> extends Stored<T> {

	/**
	 * create with provided reference
	 * @param delegate
	 * @param instanceReference
	 */
	public JNDIExposed(ComponentAdapter<T> delegate, JNDIObjectReference<T> instanceReference) {
		super(delegate, instanceReference);
	}


	/**
	 * construct reference itself using vanilla initial context
	 * @param delegate delegate adapter
	 * @param name JNDI name
	 * @throws NamingException 
	 * @throws NamingException
	 */
	public JNDIExposed(ComponentAdapter<T> delegate) throws NamingException{
		super(delegate,new JNDIObjectReference<T>(delegate.getComponentKey().toString(), new InitialContext()));
	}

	  public String toString() {
	        return "JNDI" + super.toString();
	    }
}
