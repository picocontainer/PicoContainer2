/*****************************************************************************
 * Copyright (c) PicoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the license.html file.                                                    *
 *****************************************************************************/
package org.picocontainer.persistence.jdbc;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.picocontainer.tck.MockFactory.mockeryWithCountingNamingScheme;

import java.sql.Connection;
import java.sql.SQLException;

import javax.sql.DataSource;

import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JMock;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.picocontainer.persistence.jdbc.FailoverDataSourceConnection;

/**
 * @author Juze Peleteiro <juze -a-t- intelli -dot- biz>
 * @author Mauro Talevi
 */
@RunWith(JMock.class)
public class FailoverDataSourceConnectionTestCase {

	private Mockery mockery = mockeryWithCountingNamingScheme();
	
	@Test public void testFailover() throws SQLException {
		final DataSource dataSource = mockery.mock(DataSource.class);
		final Connection connection = mockery.mock(Connection.class);

		mockery.checking(new Expectations(){{
			one(dataSource).getConnection();
			will(returnValue(connection));
			one(connection).createStatement();
			will(throwException(new SQLException()));
			one(connection).rollback();
			one(connection).close();
			one(dataSource).getConnection();
			will(returnValue(connection));
			one(connection).getAutoCommit();
			will(returnValue(true));
			one(connection).close();
		}});

		FailoverDataSourceConnection component = new FailoverDataSourceConnection(dataSource);

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
