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

import java.util.Properties;
import java.sql.SQLException;

import javax.sql.DataSource;

import org.apache.commons.dbcp.BasicDataSource;
import org.apache.commons.dbcp.BasicDataSourceFactory;
import org.nanocontainer.persistence.ExceptionHandler;
import org.picocontainer.Startable;

/**
 * Commons-DBCP DataSource component implementation. It has failover support.
 * 
 * @author Juze Peleteiro <juze -a-t- intelli -dot- biz>
 */
public class DBCPDataSource extends AbstractDataSource implements Startable {

	private BasicDataSource dataSource;

	private final Properties properties;

	/**
	 * @param driver The driver classname.
	 * @param connectionURL The connection url.
	 * @param username The connection username.
	 * @param password The connection password. 
	 */
	public DBCPDataSource(final String driver, final String connectionURL,  final String username, final String password) {
		properties = new Properties();
		properties.put("driverClassName", driver);
		properties.put("url", connectionURL);
		properties.put("username", username);
		properties.put("password", password);
	}

	/**
	 * @param driver The driver classname.
	 * @param connectionURL The connection url.
	 * @param username The connection username.
	 * @param password The connection password.
	 * @param jdbcExceptionHandler The ExceptionHandler component instance.
	 */
	public DBCPDataSource(final String driver, final String connectionURL, final String username, final String password, final ExceptionHandler jdbcExceptionHandler) {
		super(jdbcExceptionHandler);
		properties = new Properties();
		properties.put("driverClassName", driver);
		properties.put("url", connectionURL);
		properties.put("username", username);
		properties.put("password", password);
	}

	/**
	 * @param properties DBCP properties. See at @{link http://jakarta.apache.org/commons/dbcp/configuration.html}
	 */
	public DBCPDataSource(final Properties properties) {
		this.properties = properties;
	}

	/**
	 * @param properties DBCP properties. See at @{link http://jakarta.apache.org/commons/dbcp/configuration.html}
	 * @param jdbcExceptionHandler The ExceptionHandler component instance.
	 */
	public DBCPDataSource(final Properties properties, final ExceptionHandler jdbcExceptionHandler) {
		super(jdbcExceptionHandler);
		this.properties = properties;
	}

	/**
	 * @see org.nanocontainer.persistence.jdbc.AbstractDataSource#getDelegatedDataSource()
	 */
	protected DataSource getDelegatedDataSource() throws Exception {
		if (dataSource == null) {
			dataSource = (BasicDataSource) BasicDataSourceFactory.createDataSource(properties);
		}

		return dataSource;
	}

	/**
	 * @see org.nanocontainer.persistence.jdbc.AbstractDataSource#invalidateDelegatedDataSource()
	 */
	protected void invalidateDelegatedDataSource() throws SQLException {
		dataSource.close();
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
		try {
			dataSource.close();
		} catch (Exception e) {
			// Do nothing?
		}
		dataSource = null;
	}

}
