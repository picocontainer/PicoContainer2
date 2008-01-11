/*****************************************************************************
 * Copyright (c) PicoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the license.html file.                                                    *
 *                                                                           *
 * Idea by Rachel Davies, Original code by Aslak Hellesoy and Paul Hammant   *
 *****************************************************************************/
package org.nanocontainer.persistence.jdbc;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.fail;

import java.sql.Connection;
import java.sql.SQLException;

import org.junit.Test;

/**
 * @author Juze Peleteiro <juze -a-t- intelli -dot- biz>
 */
public class DBCPDataSourceTestCase {

	@Test public void testDBCP() throws SQLException {
		DBCPDataSource component = new DBCPDataSource("org.hsqldb.jdbcDriver", "jdbc:hsqldb:mem:test", "sa", "");

		component.start();

		Connection connection = component.getConnection();
		assertFalse(connection.isClosed());
		connection.close();

		try {
			connection = component.getConnection("itDoesNotExist", "soDoI");
			fail("");
		} catch (Exception e) {
			// Do nothing
		}

		component.stop();
	}

}
