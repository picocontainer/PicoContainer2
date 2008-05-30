/*
 * Copyright (C) PicoContainer Organization. All rights reserved.
 * --------------------------------------------------------------------------
 * The software in this package is published under the terms of the BSD style
 * license a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.picocontainer.logging.loggers;

import static java.lang.String.valueOf;

import java.util.logging.Level;

import org.picocontainer.logging.Logger;

/**
 * Logger implementation that delegates to JDK logger, using the following
 * mapping to the JDK log levels:
 * <ul>
 * <li>trace ==&gt; finest</li>
 * <li>debug ==&gt; fine</li>
 * <li>info ==&gt; info</li>
 * <li>warn ==&gt; warning</li>
 * <li>error ==&gt; severe</li>
 * <li>fatal ==&gt; severe</li>
 * </ul>
 */
public class JdkLogger implements Logger {
    /**
     * The JDK logger.
     */
    private final java.util.logging.Logger logger;

    /**
     * Create JdkLogger with a given JDK logger
     * 
     * @param logger the JDK logger.
     */
    public JdkLogger(final java.util.logging.Logger logger) {
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
        this.logger.log(Level.FINEST, valueOf(message));
    }

    /**
     * Log a trace message with an associated throwable.
     * 
     * @param message the message
     * @param throwable the throwable
     */
    public void trace(final Object message, final Throwable throwable) {
        this.logger.log(Level.FINEST, valueOf(message), throwable);
    }

    /**
     * Return true if a trace message will be logged.
     * 
     * @return true if message will be logged
     */
    public boolean isTraceEnabled() {
        return this.logger.isLoggable(Level.FINEST);
    }

    /**
     * Log a debug message.
     * 
     * @param message the message
     */
    public void debug(final Object message) {
        this.logger.log(Level.FINE, valueOf(message));
    }

    /**
     * Log a debug message with an associated throwable.
     * 
     * @param message the message
     * @param throwable the throwable
     */
    public void debug(final Object message, final Throwable throwable) {
        this.logger.log(Level.FINE, valueOf(message), throwable);
    }

    /**
     * Return true if a debug message will be logged.
     * 
     * @return true if message will be logged
     */
    public boolean isDebugEnabled() {
        return this.logger.isLoggable(Level.FINE);
    }

    /**
     * Log a info message.
     * 
     * @param message the message
     */
    public void info(final Object message) {
        this.logger.log(Level.INFO, valueOf(message));
    }

    /**
     * Log a info message with an associated throwable.
     * 
     * @param message the message
     * @param throwable the throwable
     */
    public void info(final Object message, final Throwable throwable) {
        this.logger.log(Level.INFO, valueOf(message), throwable);
    }

    /**
     * Return true if an info message will be logged.
     * 
     * @return true if message will be logged
     */
    public boolean isInfoEnabled() {
        return this.logger.isLoggable(Level.INFO);
    }

    /**
     * Log a warn message.
     * 
     * @param message the message
     */
    public void warn(final Object message) {
        this.logger.log(Level.WARNING, valueOf(message));
    }

    /**
     * Log a warn message with an associated throwable.
     * 
     * @param message the message
     * @param throwable the throwable
     */
    public void warn(final Object message, final Throwable throwable) {
        this.logger.log(Level.WARNING, valueOf(message), throwable);
    }

    /**
     * Return true if a warn message will be logged.
     * 
     * @return true if message will be logged
     */
    public boolean isWarnEnabled() {
        return this.logger.isLoggable(Level.WARNING);
    }

    /**
     * Log a error message.
     * 
     * @param message the message
     */
    public void error(final Object message) {
        this.logger.log(Level.SEVERE, valueOf(message));
    }

    /**
     * Log a error message with an associated throwable.
     * 
     * @param message the message
     * @param throwable the throwable
     */
    public void error(final Object message, final Throwable throwable) {
        this.logger.log(Level.SEVERE, valueOf(message), throwable);
    }

    /**
     * Return true if a error message will be logged.
     * 
     * @return true if message will be logged
     */
    public boolean isErrorEnabled() {
        return this.logger.isLoggable(Level.SEVERE);
    }

    /**
     * Log a fatal message.
     * 
     * @param message the message
     */
    public void fatal(final Object message) {
        this.logger.log(Level.SEVERE, valueOf(message));
    }

    /**
     * Log a fatal message with an associated throwable.
     * 
     * @param message the message
     * @param throwable the throwable
     */
    public void fatal(final Object message, final Throwable throwable) {
        this.logger.log(Level.SEVERE, valueOf(message), throwable);
    }

    /**
     * Return true if a fatal message will be logged.
     * 
     * @return true if message will be logged
     */
    public boolean isFatalEnabled() {
        return this.logger.isLoggable(Level.SEVERE);
    }

    /**
     * Get the child logger with specified name.
     * 
     * @param name the name of child logger
     * @return the child logger
     */
    public Logger getChildLogger(final String name) {
        final String childName = this.logger.getName() + "." + name;
        return new JdkLogger(java.util.logging.Logger.getLogger(childName));
    }
}
