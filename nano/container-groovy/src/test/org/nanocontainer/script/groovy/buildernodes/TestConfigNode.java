package org.nanocontainer.script.groovy.buildernodes;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.nanocontainer.script.NanoContainerMarkupException;
import org.picocontainer.DefaultPicoContainer;

/**
 * test capabilities of config node
 * @author k.pribluda
 *
 */
public class TestConfigNode {

	ConfigNode node;
	
	@Before public void setUp() throws Exception {
		node = new ConfigNode();
	}
	
	/**
	 * node shall accept only predefined parameters
	 *
	 */
	@Test public void testThatNoParametersAreRefused() {
		// no parameters are bombed
		try {
			node.validateScriptedAttributes(Collections.EMPTY_MAP);
			fail("accepted empty map");
		} catch(NanoContainerMarkupException ex) {
			// that's anticipated
		}
		
		
	}
	
	@Test public void testThatWrongParametersAreRefused() {
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
	
	@Test public void testThatCorrectParametersAreAcepted() {
		// no parameters are bombed
		Map map = new HashMap();
		map.put("key","glam");
		map.put("value","glarch");
		node.validateScriptedAttributes(map);
	}
	
	
	@Test public void testThatAttributesAreDelegatedProperly() {
		Map map = new HashMap();
		map.put("key","glam");
		map.put("value","glarch");	
		DefaultPicoContainer container = new DefaultPicoContainer();	
		node.createNewNode(container,map);
		
		assertEquals("glarch",container.getComponent("glam"));

	}
}
