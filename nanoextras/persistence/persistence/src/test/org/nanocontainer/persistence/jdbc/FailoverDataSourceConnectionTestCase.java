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

import java.sql.Connection;
import java.sql.SQLException;

import javax.sql.DataSource;

import org.jmock.Mock;
import org.jmock.MockObjectTestCase;

/**
 * @author Juze Peleteiro <juze -a-t- intelli -dot- biz>
 */
public class FailoverDataSourceConnectionTestCase extends MockObjectTestCase {

	public void testFailover() throws SQLException {
		Mock dataSource = mock(DataSource.class);
		Mock connection = mock(Connection.class);

		dataSource.expects(once()).method("getConnection").will(returnValue(connection.proxy()));
		connection.expects(once()).method("createStatement").will(throwException(new SQLException()));
		connection.expects(once()).method("rollback");
		connection.expects(once()).method("close");
		dataSource.expects(once()).method("getConnection").will(returnValue(connection.proxy()));
		connection.expects(once()).method("getAutoCommit").will(returnValue(true));
		connection.expects(once()).method("close");

		FailoverDataSourceConnection component = new FailoverDataSourceConnection((DataSource) dataSource.proxy());

		component.start();
		
		try {
			component.createStatement();
			fail("It was suppose to throw a SQLException.");
		} catch (SQLException e) {
			// Do nothing
		}

		assertTrue(component.getAutoCommit());

		component.stop();
	}
}
