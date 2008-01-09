package org.nanocontainer.nanosar;

import static org.junit.Assert.fail;

import org.junit.Test;

/**
 * test capabilities of container deployer
 * 
 * @author k.pribluda
 */
public class ContainerDeployerTestCase {
	
	/**
	 * test that script name is required to create pico
	 * 
	 */
	@Test public void testScriptIsRequired() {
		ContainerDeployer deployer = new ContainerDeployer();

		try {
			deployer.start();
			fail("script name is required");
		} catch (Exception ex) {
			// that's ok
		}

	}
}
