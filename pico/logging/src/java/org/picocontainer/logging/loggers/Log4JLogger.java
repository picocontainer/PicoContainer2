/*
 * Copyright (C) PicoContainer Organization. All rights reserved.
 * --------------------------------------------------------------------------
 * The software in this package is published under the terms of the BSD style
 * license a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.picocontainer.logging.loggers;

import org.apache.log4j.Level;
import org.picocontainer.logging.Logger;

/**
 * Logging facade implmentation for Apache Log4J project. The following lists
 * the mapping between DNA log levels and Log4J log levels.
 * <ul>
 * <li>trace ==&gt; debug</li>
 * <li>debug ==&gt; debug</li>
 * <li>info ==&gt; info</li>
 * <li>warn ==&gt; warn</li>
 * <li>error ==&gt; error</li>
 * </ul>
 */
public class Log4JLogger implements Logger {
    /**
     * The fully qualified name of the current class so Log4J will not include
     * it in traces.
     */
    private static final String FQCN = Log4JLogger.class.getName();

    /**
     * The log4j logger instance.
     */
    private final org.apache.log4j.Logger logger;

    /**
     * Create an instance of Log4J facade.
     * 
     * @param logger the log4j logger
     */
    public Log4JLogger(final org.apache.log4j.Logger logger) {
        if (null == logger) {
            throw new NullPointerException("logger");
        }
        this.logger = logger;
    }

    /**
     * Log a trace message.
     * 
     * @param message the message
     */
    public void trace(final Object message) {
        this.logger.log(FQCN, Level.DEBUG, message, null);
    }

    /**
     * Log a trace message with an associated throwable.
     * 
     * @param message the message
     * @param throwable the throwable
     */
    public void trace(final Object message, final Throwable throwable) {
        this.logger.log(FQCN, Level.DEBUG, message, throwable);
    }

    /**
     * Return true if a trace message will be logged.
     * 
     * @return true if message will be logged
     */
    public boolean isTraceEnabled() {
        return this.logger.isDebugEnabled();
    }

    /**
     * Log a debug message.
     * 
     * @param message the message
     */
    public void debug(final Object message) {
        this.logger.log(FQCN, Level.DEBUG, message, null);
    }

    /**
     * Log a debug message with an associated throwable.
     * 
     * @param message the message
     * @param throwable the throwable
     */
    public void debug(final Object message, final Throwable throwable) {
        this.logger.log(FQCN, Level.DEBUG, message, throwable);
    }

    /**
     * Return true if a debug message will be logged.
     * 
     * @return true if message will be logged
     */
    public boolean isDebugEnabled() {
        return this.logger.isDebugEnabled();
    }

    /**
     * Log a info message.
     * 
     * @param message the message
     */
    public void info(final Object message) {
        this.logger.log(FQCN, Level.INFO, message, null);
    }

    /**
     * Log a info message with an associated throwable.
     * 
     * @param message the message
     * @param throwable the throwable
     */
    public void info(final Object message, final Throwable throwable) {
        this.logger.log(FQCN, Level.INFO, message, throwable);
    }

    /**
     * Return true if an info message will be logged.
     * 
     * @return true if message will be logged
     */
    public boolean isInfoEnabled() {
        return this.logger.isInfoEnabled();
    }

    /**
     * Log a warn message.
     * 
     * @param message the message
     */
    public void warn(final Object message) {
        this.logger.log(FQCN, Level.WARN, message, null);
    }

    /**
     * Log a warn message with an associated throwable.
     * 
     * @param message the message
     * @param throwable the throwable
     */
    public void warn(final Object message, final Throwable throwable) {
        this.logger.log(FQCN, Level.WARN, message, throwable);
    }

    /**
     * Return true if a warn message will be logged.
     * 
     * @return true if message will be logged
     */
    public boolean isWarnEnabled() {
        return this.logger.isEnabledFor(Level.WARN);
    }

    /**
     * Log a error message.
     * 
     * @param message the message
     */
    public void error(final Object message) {
        this.logger.log(FQCN, Level.ERROR, message, null);
    }

    /**
     * Log a error message with an associated throwable.
     * 
     * @param message the message
     * @param throwable the throwable
     */
    public void error(final Object message, final Throwable throwable) {
        this.logger.log(FQCN, Level.ERROR, message, throwable);
    }

    /**
     * Return true if a error message will be logged.
     * 
     * @return true if message will be logged
     */
    public boolean isErrorEnabled() {
        return this.logger.isEnabledFor(Level.ERROR);
    }

    /**
     * Get the child logger with specified name.
     * 
     * @param name the name of child logger
     * @return the child logger
     */
    public Logger getChildLogger(final String name) {
        return new Log4JLogger(org.apache.log4j.Logger.getLogger(this.logger.getName() + "." + name));
    }
}
