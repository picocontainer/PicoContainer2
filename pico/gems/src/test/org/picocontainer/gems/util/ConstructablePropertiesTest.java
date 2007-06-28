/*******************************************************************************
 * Copyright (c) PicoContainer Organization. All rights reserved. *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD * style
 * license a copy of which has been included with this distribution in * the
 * license.html file. * * Idea by Rachel Davies, Original code by Aslak Hellesoy
 * and Paul Hammant *
 ******************************************************************************/
package org.picocontainer.gems.util;

import java.util.Properties;

import junit.framework.TestCase;
/**
 * test capabilities of constructable properties 
 * @author Konstantin Pribluda 
 */
public class ConstructablePropertiesTest extends TestCase {

	public void testPropertiesLoading() throws Exception {

		Properties properties = new ConstructableProperties("test.properties");
		assertNotNull(properties);
		assertEquals("bar", properties.getProperty("foo"));
	}
}