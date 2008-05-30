/*****************************************************************************
 * Copyright (c) PicoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the license.html file.                                                    *
 *****************************************************************************/
package org.picocontainer.persistence.jdbc;

import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.SQLException;

import javax.sql.DataSource;

import org.picocontainer.persistence.ExceptionHandler;

/**
 * Base classe for DataSource components. It delegates all calls to the datasource obtained by getDelegatedDataSource
 * method. Error handling is also there.
 * 
 * @author Juze Peleteiro
 */
public abstract class AbstractDataSource implements DataSource {

	private final ExceptionHandler jdbcExceptionHandler;

	protected AbstractDataSource(final ExceptionHandler jdbcExceptionHandler) {
		this.jdbcExceptionHandler = jdbcExceptionHandler;
	}

	protected AbstractDataSource() {
		jdbcExceptionHandler = null;
	}

	protected abstract DataSource getDelegatedDataSource() throws Exception;

	protected abstract void invalidateDelegatedDataSource() throws SQLException;

	/**
	 * Invalidates the connection calling {@link #invalidateDelegatedDataSource()} and convert the <code>cause</code>
	 * using a {@link ExceptionHandler}. if it's available otherwise just return the <code>cause</code> back.
     * @throws RuntimeException
     * @return
     * @param cause
     */
	protected SQLException handleException(final Exception cause) throws RuntimeException {
		try {
			invalidateDelegatedDataSource();
		} catch (Exception e) {
			// Do nothing, only the original exception should be reported.
		}

		if (jdbcExceptionHandler == null) {
			if (cause instanceof SQLException) {
				return (SQLException) cause;
			}

			throw (RuntimeException) cause;
		}

		throw jdbcExceptionHandler.handle(cause);
	}

	/**
	 * @see javax.sql.DataSource#getConnection()
	 */
	public Connection getConnection() throws SQLException {
		try {
			return getDelegatedDataSource().getConnection();
		} catch (Exception e) {
			throw handleException(e);
		}
	}

	/**
	 * @see javax.sql.DataSource#getConnection(java.lang.String, java.lang.String)
	 */
	public Connection getConnection(final String username, final String password) throws SQLException {
		try {
			return getDelegatedDataSource().getConnection(username, password);
		} catch (Exception e) {
			throw handleException(e);
		}
	}

	/**
	 * @see javax.sql.DataSource#getLogWriter()
	 */
	public PrintWriter getLogWriter() throws SQLException {
		try {
			return getDelegatedDataSource().getLogWriter();
		} catch (Exception e) {
			throw handleException(e);
		}
	}

	/**
	 * @see javax.sql.DataSource#setLogWriter(java.io.PrintWriter)
	 */
	public void setLogWriter(final PrintWriter out) throws SQLException {
		try {
			getDelegatedDataSource().setLogWriter(out);
		} catch (Exception e) {
			throw handleException(e);
		}
	}

	/**
	 * @see javax.sql.DataSource#setLoginTimeout(int)
	 */
	public void setLoginTimeout(final int seconds) throws SQLException {
		try {
			getDelegatedDataSource().setLoginTimeout(seconds);
		} catch (Exception e) {
			throw handleException(e);
		}
	}

	/**
	 * @see javax.sql.DataSource#getLoginTimeout()
	 */
	public int getLoginTimeout() throws SQLException {
		try {
			return getDelegatedDataSource().getLoginTimeout();
		} catch (Exception e) {
			throw handleException(e);
		}
	}

}
