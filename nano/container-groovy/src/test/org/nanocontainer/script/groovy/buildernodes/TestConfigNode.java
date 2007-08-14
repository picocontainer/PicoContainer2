package org.nanocontainer.script.groovy.buildernodes;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.nanocontainer.script.NanoContainerMarkupException;
import org.picocontainer.DefaultPicoContainer;

import junit.framework.TestCase;

/**
 * test capabilities of config node
 * @author k.pribluda
 *
 */
public class TestConfigNode extends TestCase {

	ConfigNode node;
	
	public void setUp() throws Exception {
		super.setUp();
		node = new ConfigNode();
	}
	/**
	 * node shall accept only predefined parameters
	 *
	 */
	public void testThatNoParametersAreRefused() {
		// no parameters are bombed
		try {
			node.validateScriptedAttributes(Collections.EMPTY_MAP);
			fail("accepted empty map");
		} catch(NanoContainerMarkupException ex) {
			// that's anticipated
		}
		
		
	}
	
	public void testThatWrongParametersAreRefused() {
		// no parameters are bombed
		Map map = new HashMap();
		map.put("glum","glam");
		map.put("glim","glarch");
		try {
			node.validateScriptedAttributes(map);
			fail("accepted wrong params");
		} catch(NanoContainerMarkupException ex) {
			// that's anticipated
		}
	}
	
	public void testThatCorrectParametersAreAcepted() {
		// no parameters are bombed
		Map map = new HashMap();
		map.put("key","glam");
		map.put("value","glarch");
		node.validateScriptedAttributes(map);
	}
	
	
	public void testThatAttributesAreDelegatedProperly() {
		Map map = new HashMap();
		map.put("key","glam");
		map.put("value","glarch");	
		DefaultPicoContainer container = new DefaultPicoContainer();	
		node.createNewNode(container,map);
		
		assertEquals("glarch",container.getComponent("glam"));

	}
}
