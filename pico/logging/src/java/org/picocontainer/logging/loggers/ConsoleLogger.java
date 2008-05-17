/*
 * Copyright (C) PicoContainer Organization. All rights reserved.            
 * ------------------------------------------------------------------------- 
 * The software in this package is published under the terms of the BSD      
 * style license a copy of which has been included with this distribution in 
 * the LICENSE.txt file.                                                     
 */ 
package org.picocontainer.logging.loggers;

import java.io.PrintStream;

import org.picocontainer.logging.Logger;

/**
 * A simple logger facade that simply writes to the Console.
 * 
 */
public class ConsoleLogger implements Logger {
    /**
     * Constant to indicate that the logger must log all levels.
     */
    public static final int LEVEL_ALL = 0;

    /**
     * Constant to indicate that the logger must log all levels TRACE and above.
     */
    public static final int LEVEL_TRACE = 1;

    /**
     * Constant to indicate that the logger must log all levels DEBUG and above.
     */
    public static final int LEVEL_DEBUG = 2;

    /**
     * Constant to indicate that the logger must log all levels INFO and above.
     */
    public static final int LEVEL_INFO = 3;

    /**
     * Constant to indicate that the logger must log all levels WARN and above.
     */
    public static final int LEVEL_WARN = 4;

    /**
     * Constant to indicate that the logger must log all levels ERROR and above.
     */
    public static final int LEVEL_ERROR = 5;

    /**
     * Constant to indicate that the logger must not log any messages.
     */
    public static final int LEVEL_NONE = 6;

    /**
     * String constant used to output TRACE messages.
     */
    private static final String LEVEL_TRACE_STR = "TRACE";

    /**
     * String constant used to output DEBUG messages.
     */
    private static final String LEVEL_DEBUG_STR = "DEBUG";

    /**
     * String constant used to output INFO messages.
     */
    private static final String LEVEL_INFO_STR = "INFO";

    /**
     * String constant used to output WARN messages.
     */
    private static final String LEVEL_WARN_STR = "WARN";

    /**
     * String constant used to output ERROR messages.
     */
    private static final String LEVEL_ERROR_STR = "ERROR";

    /**
     * The log level.
     */
    private final int m_level;

    /**
     * The output location.
     */
    private final PrintStream m_output;

    /**
     * Create a Console Logger that logs all messages.
     */
    public ConsoleLogger() {
        this(LEVEL_ALL);
    }

    /**
     * Create a Console Logger that logs at specified level.
     * 
     * @param level one of the LEVEL_* constants
     */
    public ConsoleLogger(final int level) {
        this(level, System.out);
    }

    /**
     * Create a Console Logger that logs at specified level.
     * 
     * @param level one of the LEVEL_* constants
     * @param output the stream to output to
     */
    public ConsoleLogger(final int level, final PrintStream output) {
        if (null == output) {
            throw new NullPointerException("output");
        }
        m_level = level;
        m_output = output;
    }

    /**
     * Log a trace message.
     * 
     * @param message the message
     */
    public void trace(final Object message) {
        trace(message, null);
    }

    /**
     * Log a trace message with an associated throwable.
     * 
     * @param message the message
     * @param throwable the throwable
     */
    public void trace(final Object message, final Throwable throwable) {
        output(LEVEL_TRACE, LEVEL_TRACE_STR, message, throwable);
    }

    /**
     * Return true if a trace message will be logged.
     * 
     * @return true if message will be logged
     */
    public boolean isTraceEnabled() {
        return m_level <= LEVEL_TRACE;
    }

    /**
     * Log a debug message.
     * 
     * @param message the message
     */
    public void debug(final Object message) {
        debug(message, null);
    }

    /**
     * Log a debug message with an associated throwable.
     * 
     * @param message the message
     * @param throwable the throwable
     */
    public void debug(final Object message, final Throwable throwable) {
        output(LEVEL_DEBUG, LEVEL_DEBUG_STR, message, throwable);
    }

    /**
     * Return true if a debug message will be logged.
     * 
     * @return true if message will be logged
     */
    public boolean isDebugEnabled() {
        return m_level <= LEVEL_DEBUG;
    }

    /**
     * Log a info message.
     * 
     * @param message the message
     */
    public void info(final Object message) {
        info(message, null);
    }

    /**
     * Log a info message with an associated throwable.
     * 
     * @param message the message
     * @param throwable the throwable
     */
    public void info(final Object message, final Throwable throwable) {
        output(LEVEL_INFO, LEVEL_INFO_STR, message, throwable);
    }

    /**
     * Return true if an info message will be logged.
     * 
     * @return true if message will be logged
     */
    public boolean isInfoEnabled() {
        return m_level <= LEVEL_INFO;
    }

    /**
     * Log a warn message.
     * 
     * @param message the message
     */
    public void warn(final Object message) {
        warn(message, null);
    }

    /**
     * Log a warn message with an associated throwable.
     * 
     * @param message the message
     * @param throwable the throwable
     */
    public void warn(final Object message, final Throwable throwable) {
        output(LEVEL_WARN, LEVEL_WARN_STR, message, throwable);
    }

    /**
     * Return true if a warn message will be logged.
     * 
     * @return true if message will be logged
     */
    public boolean isWarnEnabled() {
        return m_level <= LEVEL_WARN;
    }

    /**
     * Log a error message.
     * 
     * @param message the message
     */
    public void error(final Object message) {
        error(message, null);
    }

    /**
     * Log a error message with an associated throwable.
     * 
     * @param message the message
     * @param throwable the throwable
     */
    public void error(final Object message, final Throwable throwable) {
        output(LEVEL_ERROR, LEVEL_ERROR_STR, message, throwable);
    }

    /**
     * Return true if a error message will be logged.
     * 
     * @return true if message will be logged
     */
    public boolean isErrorEnabled() {
        return m_level <= LEVEL_ERROR;
    }

    /**
     * Get the child logger with specified name.
     * 
     * @param name the name of child logger
     * @return the child logger
     */
    public Logger getChildLogger(final String name) {
        return this;
    }

    /**
     * Utility method that logs output if level is enabled.
     * 
     * @param level the log level
     * @param type the type string
     * @param message the message
     * @param throwable the throwable, may be null
     */
    private void output(final int level, final String type, final Object message, final Throwable throwable) {
        if (m_level <= level) {
            doOutput(type, message, throwable);
        }
    }

    /**
     * Utility method to actually output message to console.
     * 
     * @param type the type string
     * @param message the message
     * @param throwable the throwable, may be null
     */
    void doOutput(final String type, final Object message, final Throwable throwable) {
        synchronized (System.out) {
            m_output.println("[" + type + "] " + message);
            if (null != throwable) {
                throwable.printStackTrace(m_output);
            }
        }
    }

    /**
     * Utility method so that subclasses can access log level.
     * 
     * @return log level of logger
     */
    final int getLevel() {
        return m_level;
    }

}
