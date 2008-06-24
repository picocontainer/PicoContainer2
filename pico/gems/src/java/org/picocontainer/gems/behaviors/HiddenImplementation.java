/**
 * 
 */
package org.picocontainer.gems.behaviors;

import org.picocontainer.ComponentAdapter;

/**
 * Backwards Compatibility stub for the renamed AsmHiddenImplementation.
 * @author Michael Rimov
 * @deprecated  Use AsmHiddenImplementation instead.
 * @since PicoContainer 2.4
 */
@Deprecated
public class HiddenImplementation<T> extends AsmHiddenImplementation<T> {

	/**
	 * Serialization UUID.
	 */
	private static final long serialVersionUID = 7407361288803019330L;

	/**
	 * @param delegate
	 */
	public HiddenImplementation(ComponentAdapter<T> delegate) {
		super(delegate);
	}



}
