package org.picocontainer.containers;

import java.util.Properties;

import org.picocontainer.DefaultPicoContainer;

import junit.framework.TestCase;

/**
 * test that properties container works properly
 * @author k.pribluda
 */
public class PropertiesPicoContainerTestCase extends TestCase {

	/**
	 * all properties specified in constructor shall be
	 * placed into container as strings
	 *
	 */
	public void testThatAllPropertiesAreAdded() {
		Properties properties = new Properties();
		
		properties.put("foo","bar");
		properties.put("blurge","bang");
		
		
		PropertiesPicoContainer container = new PropertiesPicoContainer(properties);
		assertEquals("bar",container.getComponent("foo"));
		assertEquals("bang",container.getComponent("blurge"));
	}
	
	/**
	 * inquiry shall be delegated to parent container
	 */
	public void testThatParentDelegationWorks() {
		DefaultPicoContainer parent = new DefaultPicoContainer();
		String stored = new String("glam");
		parent.addComponent("glam",stored);
		
		PropertiesPicoContainer contaienr = new PropertiesPicoContainer(new Properties(),parent);
		
		assertSame(stored,contaienr.getComponent("glam"));
	}

}
