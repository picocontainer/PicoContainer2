package org.nanocontainer.nanosar;

import junit.framework.TestCase;

/**
 * test capabilities of container deployer
 * 
 * @author k.pribluda
 */
public class ContainerDeployerTestCase extends TestCase {

	/**
	 * test that script name is required to create pico
	 * 
	 */
	public void testScriptIsRequired() {
		ContainerDeployer deployer = new ContainerDeployer();

		try {
			deployer.start();
			fail("script name is required");
		} catch (Exception ex) {
			// that's ok
		}

	}
}
