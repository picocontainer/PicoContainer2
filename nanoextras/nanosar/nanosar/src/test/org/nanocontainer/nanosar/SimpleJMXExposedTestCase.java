package org.nanocontainer.nanosar;

import javax.management.InstanceNotFoundException;
import javax.management.MBeanServer;
import javax.management.MBeanServerFactory;
import javax.management.ObjectName;

import org.picocontainer.DefaultPicoContainer;
import org.picocontainer.MutablePicoContainer;
import org.picocontainer.adapters.InstanceAdapter;

import junit.framework.TestCase;

/**
 * test capabilities of JMX exposition
 * @author k.pribluda
 *
 */
public class SimpleJMXExposedTestCase extends TestCase {

	/**
	 * if no object name is set, shall expose under base name
	 * with key as property
	 */
	public void testExposingWithImplicitName() throws Exception  {	
		SimpleComponent toRegister = new SimpleComponent();
		MutablePicoContainer container = new DefaultPicoContainer();
		SimpleJMXExposed<SimpleComponent> jmxExposed = new SimpleJMXExposed<SimpleComponent>(new InstanceAdapter<SimpleComponent>("blam", toRegister));
		
		container.addAdapter(jmxExposed);
		
		MBeanServer server = MBeanServerFactory.createMBeanServer();
		
		jmxExposed.register(container,server,new ObjectName("default.pico:container=bla"));

		assertEquals("foo",server.getAttribute(new ObjectName("default.pico:container=bla,key=blam"),"Foo"));
		
		assertTrue(jmxExposed.registered);
	}
	
	
	public void testExposingWithExplicitName() throws Exception {
		SimpleComponent toRegister = new SimpleComponent();
		MutablePicoContainer container = new DefaultPicoContainer();
		SimpleJMXExposed<SimpleComponent> jmxExposed = new SimpleJMXExposed<SimpleComponent>(new InstanceAdapter<SimpleComponent>("blam", toRegister), new ObjectName("glum:glam=glem"));
		
		container.addAdapter(jmxExposed);
		
		MBeanServer server = MBeanServerFactory.createMBeanServer();
		
		jmxExposed.register(container,server,new ObjectName("default.pico:container=bla"));
		
		assertEquals("foo",server.getAttribute(new ObjectName("glum:glam=glem"),"Foo"));
		
		assertTrue(jmxExposed.registered);
		
		
		// and test deregistering componen
		jmxExposed.deregister();
		try {
			server.getAttribute(new ObjectName("glum:glam=glem"),"Foo");
			fail("failed to deregister component");
		} catch(InstanceNotFoundException ex) {
			// anticipated
		}
		
	}
}
