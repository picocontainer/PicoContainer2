package org.picocontainer.containers;

import static org.junit.Assert.assertSame;

import org.junit.Test;

/**
 * test capabilities of system properties providing container. 
 * @author k.pribluda
 *
 */
public class SystemPropertiesPicoContainerTestCase {

	
	/**
	 * all the content of system properties shall be made available
	 *  through this contaienr. 
	 */
	@Test public void testThatAllSystemPropertiesAreCopied() {
		SystemPropertiesPicoContainer container = new SystemPropertiesPicoContainer();		
		for(Object key: System.getProperties().keySet()) {
			assertSame(System.getProperties().get(key),container.getComponent(key));
		}
	}
}
