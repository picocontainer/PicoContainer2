package org.picocontainer.containers;

import junit.framework.TestCase;

/**
 * test capabilities of system properties providing container. 
 * @author k.pribluda
 *
 */
public class SystemPropertiesPicoContainerTestCase extends TestCase {

	
	/**
	 * all the content of system properties shall be made available
	 *  through this contaienr. 
	 */
	public void testThatAllSystemPropertiesAreCopied() {
		SystemPropertiesPicoContainer container = new SystemPropertiesPicoContainer();		
		for(Object key: System.getProperties().keySet()) {
			assertSame(System.getProperties().get(key),container.getComponent(key));
		}
	}
}
