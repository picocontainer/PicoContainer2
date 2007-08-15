package org.picocontainer.gems.jndi;

import org.picocontainer.ComponentAdapter;
import org.picocontainer.behaviors.Cached;

/**
 * exposes component to JNDI basically does same thing as cached,
 * but uses JNDI reference instead. Maybe Cached shall be 
 * refactored? as there is little new functionality. 
 * @author k.pribluda
 *
 */
public class JNDIExposed extends Cached {

	/**
	 * 
	 * @param delegate
	 * @param instanceReference
	 */
	public JNDIExposed(ComponentAdapter delegate, JNDIObjectReference instanceReference) {
		super(delegate, instanceReference);
	}



}
