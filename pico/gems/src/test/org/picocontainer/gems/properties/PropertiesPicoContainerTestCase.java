package org.picocontainer.gems.properties;

import java.util.Properties;

import org.jmock.Mock;
import org.jmock.MockObjectTestCase;
import org.picocontainer.PicoContainer;



/**
 * test capabilities of properties based pico container
 * 
 * @author k.pribluda
 * 
 */
public class PropertiesPicoContainerTestCase extends MockObjectTestCase {
	Properties config;

	public PropertiesPicoContainerTestCase() {
		super();
		config = new Properties();

		config.put("stringProperty", "grump");
		config.put("integerProperty", "239");
	}

	/**
	 * retrieval by key other than string shall return null
	 */
	public void testThatRetrievalByNotStringReturnsNull() {
		PropertiesPicoContainer container = new PropertiesPicoContainer(
				new Properties());
		assertNull(container.getComponent(Properties.class));
	}

	/**
	 * retrieval by key shall delegate to parent when nothing acceptable 
	 * found
	 */
	public void testThatContainerDelegatesToParent() {
		final Mock picoMock = mock(PicoContainer.class);
		
		String unavailableKey = "unavailable";
		
		picoMock.expects(once()).method("getComponent").with(same(unavailableKey)).will(returnValue(null));

		PicoContainer parent = (PicoContainer) picoMock.proxy();
		
		PropertiesPicoContainer container = new PropertiesPicoContainer(
				new Properties(),parent);
		
		container.getComponent(unavailableKey);
		
		picoMock.verify();
	}

}
