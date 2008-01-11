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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import static org.picocontainer.tck.MockFactory.mockeryWithCountingNamingScheme;

import java.sql.SQLException;

import javax.naming.Context;
import javax.naming.NamingException;
import javax.sql.DataSource;

import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JMock;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.nanocontainer.persistence.jdbc.JNDIDataSource;

/**
 * @author Juze Peleteiro <juze -a-t- intelli -dot- biz>
 * @author Mauro Talevi
 */
@RunWith(JMock.class)
public class JNDIDataSourceTestCase  {

	private Mockery mockery = mockeryWithCountingNamingScheme();
	
	@Test public void testDataSource() throws SQLException, NamingException {
		final String NAME = "aeiou";

		final Context context = mockery.mock(Context.class);
		final DataSource dataSource = mockery.mock(DataSource.class);
		mockery.checking(new Expectations(){{
			one(context).lookup(with(equal(NAME)));
			will(returnValue(dataSource));
			one(dataSource).setLoginTimeout(with(any(Integer.class)));
			will(throwException(new SQLException()));
			one(context).lookup(with(equal(NAME)));
			will(returnValue(dataSource));
			one(dataSource).getLoginTimeout();
			will(returnValue(123));
		}});

		JNDIDataSource component = new JNDIDataSource(NAME, context);

		component.start();

		try {
			component.setLoginTimeout(987);
			fail("It was suppose to throw a SQLException.");
		} catch (SQLException e) {
		}

		assertEquals(123, component.getLoginTimeout());

		component.stop();
	}

}
