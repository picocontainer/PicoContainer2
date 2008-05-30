/*****************************************************************************
 * Copyright (c) PicoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the license.html file.                                                    *
 *****************************************************************************/
package org.picocontainer.persistence.jdbc;

import java.sql.SQLException;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.sql.DataSource;

import org.picocontainer.Startable;
import org.picocontainer.persistence.ExceptionHandler;

/**
 * @author Juze Peleteiro <juze -a-t- intelli -dot- biz>
 */
public class JNDIDataSource extends AbstractDataSource implements Startable {

	private final String name;

	private final Context context;

	private DataSource dataSource;

	/**
	 * @param name JNDI name where the original DataSource is.
	 */
	public JNDIDataSource(final String name) {
		this.name = name;
		this.context = null;
	}

	/**
	 * @param name JNDI name where the original DataSource is.
	 * @param jdbcExceptionHandler The ExceptionHandler component instance.
	 */
	public JNDIDataSource(final String name, final ExceptionHandler jdbcExceptionHandler) {
		super(jdbcExceptionHandler);
		this.name = name;
		this.context = null;
	}

	/**
	 * @param name JNDI name where the original DataSource is.
	 * @param context JNDI context.
	 */
	public JNDIDataSource(final String name, final Context context) {
		this.name = name;
		this.context = context;
	}

	/**
	 * @param name JNDI name where the original DataSource is.
	 * @param context JNDI context.
	 * @param jdbcExceptionHandler The ExceptionHandler component instance.
	 */
	public JNDIDataSource(final String name, final Context context, final ExceptionHandler jdbcExceptionHandler) {
		super(jdbcExceptionHandler);
		this.name = name;
		this.context = context;
	}

	/**
	 * @see org.picocontainer.persistence.jdbc.AbstractDataSource#getDelegatedDataSource()
	 */
	protected DataSource getDelegatedDataSource() throws Exception {
		if (dataSource == null) {
			Context jndiContext;
			if (context == null) {
				jndiContext = new InitialContext();
			} else {
				jndiContext = context;
			}

			dataSource = (DataSource) jndiContext.lookup(name);
		}

		return dataSource;
	}

	/**
	 * @see org.picocontainer.persistence.jdbc.AbstractDataSource#invalidateDelegatedDataSource()
	 */
	protected void invalidateDelegatedDataSource() {
		dataSource = null;
	}

	/**
	 * @see org.picocontainer.Startable#start()
	 */
	public void start() {
		// Do nothing
	}

	/**
	 * @see org.picocontainer.Startable#stop()
	 */
	public void stop() {
		dataSource = null;
	}

	public boolean isWrapperFor(Class<?> arg0) throws SQLException {
		// TODO Auto-generated method stub
		return false;
	}

	public <T> T unwrap(Class<T> arg0) throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

}
