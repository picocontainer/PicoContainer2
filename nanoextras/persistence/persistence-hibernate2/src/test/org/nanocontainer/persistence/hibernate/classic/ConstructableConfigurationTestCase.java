/*****************************************************************************
 * Copyright (c) PicoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the LICENSE.txt file.                                                     *
 *                                                                           *
 * Idea by Rachel Davies, Original code by various                           *
 *****************************************************************************/
package org.nanocontainer.persistence.hibernate.classic;

import static org.junit.Assert.assertNotNull;

import org.junit.Test;
import org.nanocontainer.persistence.hibernate.classic.ConstructableConfiguration;

/**
 * @author Konstantin Pribluda
 * @version $Revision: 2043 $
 */
public class ConstructableConfigurationTestCase {
	
	@Test public void testDefaultConstruction() throws Exception {
		ConstructableConfiguration config = new ConstructableConfiguration();
		assertNotNull(config);
	}
	
	
	@Test public void testResourceConstruction()  throws Exception {
		ConstructableConfiguration config = new ConstructableConfiguration("/hibernate.cfg.xml");
		assertNotNull(config);
	}
}
	

