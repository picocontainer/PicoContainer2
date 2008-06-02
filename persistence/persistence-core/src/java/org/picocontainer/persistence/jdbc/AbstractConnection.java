/*****************************************************************************
 * Copyright (c) PicoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the license.html file.                                                    *
 *****************************************************************************/
package org.picocontainer.persistence.jdbc;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.SQLWarning;
import java.sql.Savepoint;
import java.sql.Statement;
import java.sql.Struct;
import java.sql.SQLClientInfoException;
import java.sql.Clob;
import java.sql.SQLXML;
import java.sql.NClob;
import java.sql.Blob;
import java.sql.Array;
import java.util.Map;
import java.util.Properties;

import org.picocontainer.persistence.ExceptionHandler;

/**
 * Base class for Connection components. It delegates all calls to the connection obtained by getDelegatedConnection
 * method. Error handling is also there.
 * 
 * @author Juze Peleteiro
 */
public abstract class AbstractConnection implements Connection {

	private final ExceptionHandler jdbcExceptionHandler;

	protected AbstractConnection(ExceptionHandler jdbcExceptionHandler) {
		this.jdbcExceptionHandler = jdbcExceptionHandler;
	}

	protected AbstractConnection() {
		jdbcExceptionHandler = null;
	}

	protected abstract Connection getDelegatedConnection() throws SQLException;

	protected abstract void invalidateDelegatedConnection();

	/**
	 * Invalidates the connection calling {@link #invalidateDelegatedConnection()} and convert the <code>cause</code>
	 * using a {@link ExceptionHandler}. if it's available otherwise just return the <code>cause</code> back.
     * @throws RuntimeException
     * @param cause
     * @return
     */
	protected SQLException handleSQLException(Exception cause) throws RuntimeException {
		try {
			invalidateDelegatedConnection();
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
	 * @see java.sql.Connection#createStatement()
	 */
	public Statement createStatement() throws SQLException {
		try {
			return getDelegatedConnection().createStatement();
		} catch (Exception e) {
			throw handleSQLException(e);
		}
	}

	/**
	 * @see java.sql.Connection#prepareStatement(java.lang.String)
	 */
	public PreparedStatement prepareStatement(String sql) throws SQLException {
		try {
			return getDelegatedConnection().prepareStatement(sql);
		} catch (Exception e) {
			throw handleSQLException(e);
		}
	}

	/**
	 * @see java.sql.Connection#prepareCall(java.lang.String)
	 */
	public CallableStatement prepareCall(String sql) throws SQLException {
		try {
			return getDelegatedConnection().prepareCall(sql);
		} catch (Exception e) {
			throw handleSQLException(e);
		}
	}

	/**
	 * @see java.sql.Connection#nativeSQL(java.lang.String)
	 */
	public String nativeSQL(String sql) throws SQLException {
		try {
			return getDelegatedConnection().nativeSQL(sql);
		} catch (Exception e) {
			throw handleSQLException(e);
		}
	}

	/**
	 * @see java.sql.Connection#setAutoCommit(boolean)
	 */
	public void setAutoCommit(boolean autoCommit) throws SQLException {
		try {
			getDelegatedConnection().setAutoCommit(autoCommit);
		} catch (Exception e) {
			throw handleSQLException(e);
		}
	}

	/**
	 * @see java.sql.Connection#getAutoCommit()
	 */
	public boolean getAutoCommit() throws SQLException {
		try {
			return getDelegatedConnection().getAutoCommit();
		} catch (Exception e) {
			throw handleSQLException(e);
		}
	}

	/**
	 * @see java.sql.Connection#commit()
	 */
	public void commit() throws SQLException {
		try {
			getDelegatedConnection().commit();
		} catch (Exception e) {
			throw handleSQLException(e);
		}
	}

	/**
	 * @see java.sql.Connection#rollback()
	 */
	public void rollback() throws SQLException {
		try {
			getDelegatedConnection().rollback();
		} catch (Exception e) {
			throw handleSQLException(e);
		}
	}

	/**
	 * @see java.sql.Connection#close()
	 */
	public void close() throws SQLException {
		try {
			getDelegatedConnection().close();
		} catch (Exception e) {
			throw handleSQLException(e);
		}
	}

	/**
	 * @see java.sql.Connection#isClosed()
	 */
	public boolean isClosed() throws SQLException {
		try {
			return getDelegatedConnection().isClosed();
		} catch (Exception e) {
			throw handleSQLException(e);
		}
	}

	/**
	 * @see java.sql.Connection#getMetaData()
	 */
	public DatabaseMetaData getMetaData() throws SQLException {
		try {
			return getDelegatedConnection().getMetaData();
		} catch (Exception e) {
			throw handleSQLException(e);
		}
	}

	/**
	 * @see java.sql.Connection#setReadOnly(boolean)
	 */
	public void setReadOnly(boolean readOnly) throws SQLException {
		try {
			getDelegatedConnection().setReadOnly(readOnly);
		} catch (Exception e) {
			throw handleSQLException(e);
		}
	}

	/**
	 * @see java.sql.Connection#isReadOnly()
	 */
	public boolean isReadOnly() throws SQLException {
		try {
			return getDelegatedConnection().isReadOnly();
		} catch (Exception e) {
			throw handleSQLException(e);
		}
	}

	/**
	 * @see java.sql.Connection#setCatalog(java.lang.String)
	 */
	public void setCatalog(String catalog) throws SQLException {
		try {
			getDelegatedConnection().setCatalog(catalog);
		} catch (Exception e) {
			throw handleSQLException(e);
		}
	}

	/**
	 * @see java.sql.Connection#getCatalog()
	 */
	public String getCatalog() throws SQLException {
		try {

			return getDelegatedConnection().getCatalog();
		} catch (Exception e) {
			throw handleSQLException(e);
		}
	}

	/**
	 * @see java.sql.Connection#setTransactionIsolation(int)
	 */
	public void setTransactionIsolation(int level) throws SQLException {
		try {
			getDelegatedConnection().setTransactionIsolation(level);
		} catch (Exception e) {
			throw handleSQLException(e);
		}
	}

	/**
	 * @see java.sql.Connection#getTransactionIsolation()
	 */
	public int getTransactionIsolation() throws SQLException {
		try {
			return getDelegatedConnection().getTransactionIsolation();
		} catch (Exception e) {
			throw handleSQLException(e);
		}
	}

	/**
	 * @see java.sql.Connection#getWarnings()
	 */
	public SQLWarning getWarnings() throws SQLException {
		try {
			return getDelegatedConnection().getWarnings();
		} catch (Exception e) {
			throw handleSQLException(e);
		}
	}

	/**
	 * @see java.sql.Connection#clearWarnings()
	 */
	public void clearWarnings() throws SQLException {
		try {
			getDelegatedConnection().clearWarnings();
		} catch (Exception e) {
			throw handleSQLException(e);
		}
	}

	/**
	 * @see java.sql.Connection#createStatement(int, int)
	 */
	public Statement createStatement(int resultSetType, int resultSetConcurrency) throws SQLException {
		try {
			return getDelegatedConnection().createStatement(resultSetType, resultSetConcurrency);
		} catch (Exception e) {
			throw handleSQLException(e);
		}
	}

	/**
	 * @see java.sql.Connection#prepareStatement(java.lang.String, int, int)
	 */
	public PreparedStatement prepareStatement(String sql, int resultSetType, int resultSetConcurrency) throws SQLException {
		try {
			return getDelegatedConnection().prepareStatement(sql, resultSetType, resultSetConcurrency);
		} catch (Exception e) {
			throw handleSQLException(e);
		}
	}

	/**
	 * @see java.sql.Connection#prepareCall(java.lang.String, int, int)
	 */
	public CallableStatement prepareCall(String sql, int resultSetType, int resultSetConcurrency) throws SQLException {
		try {
			return getDelegatedConnection().prepareCall(sql, resultSetType, resultSetConcurrency);
		} catch (Exception e) {
			throw handleSQLException(e);
		}
	}

	/**
	 * @see java.sql.Connection#getTypeMap()
	 */
	public Map getTypeMap() throws SQLException {
		try {
			return getDelegatedConnection().getTypeMap();
		} catch (Exception e) {
			throw handleSQLException(e);
		}
	}

	/**
	 * @see java.sql.Connection#setTypeMap(java.util.Map)
	 */
	public void setTypeMap(Map map) throws SQLException {
		try {
			getDelegatedConnection().setTypeMap(map);
		} catch (Exception e) {
			throw handleSQLException(e);
		}
	}

	/**
	 * @see java.sql.Connection#setHoldability(int)
	 */
	public void setHoldability(int holdability) throws SQLException {
		try {
			getDelegatedConnection().setHoldability(holdability);
		} catch (Exception e) {
			throw handleSQLException(e);
		}
	}

	/**
	 * @see java.sql.Connection#getHoldability()
	 */
	public int getHoldability() throws SQLException {
		try {
			return getDelegatedConnection().getHoldability();
		} catch (Exception e) {
			throw handleSQLException(e);
		}
	}

	/**
	 * @see java.sql.Connection#setSavepoint()
	 */
	public Savepoint setSavepoint() throws SQLException {
		try {
			return getDelegatedConnection().setSavepoint();
		} catch (Exception e) {
			throw handleSQLException(e);
		}
	}

	/**
	 * @see java.sql.Connection#setSavepoint(java.lang.String)
	 */
	public Savepoint setSavepoint(String name) throws SQLException {
		try {
			return getDelegatedConnection().setSavepoint(name);
		} catch (Exception e) {
			throw handleSQLException(e);
		}
	}

	/**
	 * @see java.sql.Connection#rollback(java.sql.Savepoint)
	 */
	public void rollback(Savepoint savepoint) throws SQLException {
		try {
			getDelegatedConnection().rollback(savepoint);
		} catch (Exception e) {
			throw handleSQLException(e);
		}
	}

	/**
	 * @see java.sql.Connection#releaseSavepoint(java.sql.Savepoint)
	 */
	public void releaseSavepoint(Savepoint savepoint) throws SQLException {
		try {
			getDelegatedConnection().releaseSavepoint(savepoint);
		} catch (Exception e) {
			throw handleSQLException(e);
		}
	}

	/**
	 * @see java.sql.Connection#createStatement(int, int, int)
	 */
	public Statement createStatement(int resultSetType, int resultSetConcurrency, int resultSetHoldability) throws SQLException {
		try {
			return getDelegatedConnection().createStatement(resultSetType, resultSetConcurrency, resultSetHoldability);
		} catch (Exception e) {
			throw handleSQLException(e);
		}
	}

	/**
	 * @see java.sql.Connection#prepareStatement(java.lang.String, int, int, int)
	 */
	public PreparedStatement prepareStatement(String sql, int resultSetType, int resultSetConcurrency, int resultSetHoldability) throws SQLException {
		try {
			return getDelegatedConnection().prepareStatement(sql, resultSetType, resultSetConcurrency, resultSetHoldability);
		} catch (Exception e) {
			throw handleSQLException(e);
		}
	}

	/**
	 * @see java.sql.Connection#prepareCall(java.lang.String, int, int, int)
	 */
	public CallableStatement prepareCall(String sql, int resultSetType, int resultSetConcurrency, int resultSetHoldability) throws SQLException {
		try {
			return getDelegatedConnection().prepareCall(sql, resultSetType, resultSetConcurrency, resultSetHoldability);
		} catch (Exception e) {
			throw handleSQLException(e);
		}
	}

	/**
	 * @see java.sql.Connection#prepareStatement(java.lang.String, int)
	 */
	public PreparedStatement prepareStatement(String sql, int autoGeneratedKeys) throws SQLException {
		try {
			return getDelegatedConnection().prepareStatement(sql, autoGeneratedKeys);
		} catch (Exception e) {
			throw handleSQLException(e);
		}
	}

	/**
	 * @see java.sql.Connection#prepareStatement(java.lang.String, int[])
	 */
	public PreparedStatement prepareStatement(String sql, int[] columnIndexes) throws SQLException {
		try {
			return getDelegatedConnection().prepareStatement(sql, columnIndexes);
		} catch (Exception e) {
			throw handleSQLException(e);
		}
	}

	/**
	 * @see java.sql.Connection#prepareStatement(java.lang.String, java.lang.String[])
	 */
	public PreparedStatement prepareStatement(String sql, String[] columnNames) throws SQLException {
		try {
			return getDelegatedConnection().prepareStatement(sql, columnNames);
		} catch (Exception e) {
			throw handleSQLException(e);
		}
	}

    // Java 6 methods ....

    public Struct createStruct(String sql, Object[] objects) throws SQLException {
        try {
            return getDelegatedConnection().createStruct(sql, objects);
        } catch (Exception e) {
            throw handleSQLException(e);
        }
    }

    public Array createArrayOf(String sql, Object[] objects) throws SQLException {
        try {
            return getDelegatedConnection().createArrayOf(sql, objects);
        } catch (Exception e) {
            throw handleSQLException(e);
        }
    }

    public void setClientInfo(String sql, String s1) throws SQLClientInfoException {
        try {
            getDelegatedConnection().setClientInfo(sql, s1);
        } catch (Exception e) {
            throw handleSQLClientInfoException(e);
        }

    }

    public void setClientInfo(Properties properties) throws SQLClientInfoException {
        try {
            getDelegatedConnection().setClientInfo(properties);
        } catch (Exception e) {
            throw handleSQLClientInfoException(e);
        }
    }

    public String getClientInfo(String sql) throws SQLException {
        try {
            return getDelegatedConnection().getClientInfo(sql);
        } catch (Exception e) {
            throw handleSQLException(e);
        }
    }

    public Properties getClientInfo() throws SQLException {
        try {
            return getDelegatedConnection().getClientInfo();
        } catch (Exception e) {
            throw handleSQLException(e);
        }
    }

    public Clob createClob() throws SQLException {
        try {
            return getDelegatedConnection().createClob();
        } catch (Exception e) {
            throw handleSQLException(e);
        }
    }

    public Blob createBlob() throws SQLException {
        try {
            return getDelegatedConnection().createBlob();
        } catch (Exception e) {
            throw handleSQLException(e);
        }
    }

    public NClob createNClob() throws SQLException {
        try {
            return getDelegatedConnection().createNClob();
        } catch (Exception e) {
            throw handleSQLException(e);
        }
    }

    public SQLXML createSQLXML() throws SQLException {
        try {
            return getDelegatedConnection().createSQLXML();
        } catch (Exception e) {
            throw handleSQLException(e);
        }
    }

    public boolean isValid(int i) throws SQLException {
        try {
            return getDelegatedConnection().isValid(i);
        } catch (Exception e) {
            throw handleSQLException(e);
        }
    }

    public <T> T unwrap(Class<T> tClass) throws SQLException {
        try {
            return getDelegatedConnection().unwrap(tClass);
        } catch (Exception e) {
            throw handleSQLException(e);
        }
    }

    public boolean isWrapperFor(Class<?> aClass) throws SQLException {
        try {
            return getDelegatedConnection().isWrapperFor(aClass);
        } catch (Exception e) {
            throw handleSQLException(e);
        }
    }

     /**
     * Invalidates the connection calling {@link #invalidateDelegatedConnection()} and convert the <code>cause</code>
     * using a {@link ExceptionHandler}. if it's available otherwise just return the <code>cause</code> back.
     * @throws RuntimeException
     * @param cause
     * @return
     */
    protected SQLClientInfoException handleSQLClientInfoException(Exception cause) throws RuntimeException {
        try {
            invalidateDelegatedConnection();
        } catch (Exception e) {
            // Do nothing, only the original exception should be reported.
        }

        if (jdbcExceptionHandler == null) {
            if (cause instanceof SQLException) {
                return (SQLClientInfoException) cause;
            }

            throw (RuntimeException) cause;
        }

        throw jdbcExceptionHandler.handle(cause);
    }


}
