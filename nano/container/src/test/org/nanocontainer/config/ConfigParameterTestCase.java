package org.nanocontainer.config;

import java.util.List;

import org.picocontainer.DefaultPicoContainer;
import org.picocontainer.MutablePicoContainer;
import org.picocontainer.ParameterName;
import org.picocontainer.PicoContainer;

import junit.framework.TestCase;

/**
 * test that config parameter does the right job
 * @author k.pribluda
 *
 */
public class ConfigParameterTestCase extends TestCase {
	
	// defaultparameter name, just for convenience
	ParameterName paramName = new ParameterName() {
		public String getName() {
			return "gloo.blum";
		}
		
	};
	
	public void testThatNoEntryIsWorkingProperly() throws Exception {
		PicoContainer container = new DefaultPicoContainer();
		ConfigParameter parameter = new ConfigParameter("gloo.blum");
		
		// shall be not resolvable
		assertFalse(parameter.isResolvable(container,null,String.class,paramName,false));
		
		// shall resolve instance as null
		assertNull(parameter.resolveInstance(container,null,String.class,paramName,false));
	}
	
	
	public void testThatNotStringEntryIsNotResolved() throws Exception {
		MutablePicoContainer container = new DefaultPicoContainer();		
		container.addComponent("gloo.blum",new Integer(239));
		
		ConfigParameter parameter = new ConfigParameter("gloo.blum");
		
		// shall be not resolvable
		assertFalse(parameter.isResolvable(container,null,String.class,paramName,false));
		
		// shall resolve instance as null
		assertNull(parameter.resolveInstance(container,null,String.class,paramName,false));
	
	}
	/**
	 * shall resolve as ddifferent classes
	 * @throws Exception
	 */
	public void testThatResolvedSuccessfully() throws Exception {
		MutablePicoContainer container = new DefaultPicoContainer();		
		container.addComponent("gloo.blum","239");

		ConfigParameter parameter = new ConfigParameter("gloo.blum");
		
		assertEquals(new Integer(239),parameter.resolveInstance(container,null,Integer.class,paramName,false));
		assertEquals("239",parameter.resolveInstance(container,null,String.class,paramName,false));
	}
	
	/**
	 * shall bomb properly if no suitable converter found
	 *
	 */
	public void testThatUnavailableConverterProducesCorrectException() {
		MutablePicoContainer container = new DefaultPicoContainer();		
		container.addComponent("gloo.blum","239");

		ConfigParameter parameter = new ConfigParameter("gloo.blum");
		
		try {
			parameter.resolveInstance(container,null,List.class,paramName,false);
			fail("failed to bomb on unavailable converter");
		} catch(ConfigParameter.NoConverterAvailableException ex) {
			// that's naticipated
		}
		
	}
}
