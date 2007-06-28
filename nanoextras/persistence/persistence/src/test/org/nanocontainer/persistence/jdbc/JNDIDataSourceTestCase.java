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

import java.sql.SQLException;

import javax.naming.Context;
import javax.sql.DataSource;

import org.jmock.Mock;
import org.jmock.MockObjectTestCase;

/**
 * @author Juze Peleteiro <juze -a-t- intelli -dot- biz>
 */
public class JNDIDataSourceTestCase extends MockObjectTestCase {

	public void testDataSource() throws SQLException {
		final String NAME = "aeiou";

		Mock context = mock(Context.class);
		Mock dataSource = mock(DataSource.class);

		context.expects(once()).method("lookup").with(eq(NAME)).will(returnValue(dataSource.proxy()));
		dataSource.expects(once()).method("setLoginTimeout").will(throwException(new SQLException()));
		context.expects(once()).method("lookup").with(eq(NAME)).will(returnValue(dataSource.proxy()));
		dataSource.expects(once()).method("getLoginTimeout").will(returnValue(123));

		JNDIDataSource component = new JNDIDataSource(NAME, (Context) context.proxy());

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
