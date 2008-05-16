package org.picocontainer.logging.loggers;

import java.util.logging.Level;

import org.picocontainer.logging.Logger;

/**
 * Logging facade implmentation for JDK1.4 logging toolkit. The following lists
 * the mapping between DNA log levels and JDK1.4 log levels.
 * <ul>
 * <li>trace ==&gt; finest</li>
 * <li>debug ==&gt; fine</li>
 * <li>info ==&gt; info</li>
 * <li>warn ==&gt; warning</li>
 * <li>error ==&gt; severe</li>
 * </ul>
 */
public class Jdk14Logger implements Logger {
    /**
     * The JDK1.4 logger.
     */
    private final java.util.logging.Logger m_logger;

    /**
     * Create an instance of JDK14Logger facade.
     * 
     * @param logger the JDK1.4 logger.
     */
    public Jdk14Logger(final java.util.logging.Logger logger) {
        if (null == logger) {
            throw new NullPointerException("logger");
        }
        m_logger = logger;
    }

    /**
     * Log a trace message.
     * 
     * @param message the message
     */
    public void trace(final String message) {
        m_logger.log(Level.FINEST, message);
    }

    /**
     * Log a trace message with an associated throwable.
     * 
     * @param message the message
     * @param throwable the throwable
     */
    public void trace(final String message, final Throwable throwable) {
        m_logger.log(Level.FINEST, message, throwable);
    }

    /**
     * Return true if a trace message will be logged.
     * 
     * @return true if message will be logged
     */
    public boolean isTraceEnabled() {
        return m_logger.isLoggable(Level.FINEST);
    }

    /**
     * Log a debug message.
     * 
     * @param message the message
     */
    public void debug(final String message) {
        m_logger.log(Level.FINE, message);
    }

    /**
     * Log a debug message with an associated throwable.
     * 
     * @param message the message
     * @param throwable the throwable
     */
    public void debug(final String message, final Throwable throwable) {
        m_logger.log(Level.FINE, message, throwable);
    }

    /**
     * Return true if a debug message will be logged.
     * 
     * @return true if message will be logged
     */
    public boolean isDebugEnabled() {
        return m_logger.isLoggable(Level.FINE);
    }

    /**
     * Log a info message.
     * 
     * @param message the message
     */
    public void info(final String message) {
        m_logger.log(Level.INFO, message);
    }

    /**
     * Log a info message with an associated throwable.
     * 
     * @param message the message
     * @param throwable the throwable
     */
    public void info(final String message, final Throwable throwable) {
        m_logger.log(Level.INFO, message, throwable);
    }

    /**
     * Return true if an info message will be logged.
     * 
     * @return true if message will be logged
     */
    public boolean isInfoEnabled() {
        return m_logger.isLoggable(Level.INFO);
    }

    /**
     * Log a warn message.
     * 
     * @param message the message
     */
    public void warn(final String message) {
        m_logger.log(Level.WARNING, message);
    }

    /**
     * Log a warn message with an associated throwable.
     * 
     * @param message the message
     * @param throwable the throwable
     */
    public void warn(final String message, final Throwable throwable) {
        m_logger.log(Level.WARNING, message, throwable);
    }

    /**
     * Return true if a warn message will be logged.
     * 
     * @return true if message will be logged
     */
    public boolean isWarnEnabled() {
        return m_logger.isLoggable(Level.WARNING);
    }

    /**
     * Log a error message.
     * 
     * @param message the message
     */
    public void error(final String message) {
        m_logger.log(Level.SEVERE, message);
    }

    /**
     * Log a error message with an associated throwable.
     * 
     * @param message the message
     * @param throwable the throwable
     */
    public void error(final String message, final Throwable throwable) {
        m_logger.log(Level.SEVERE, message, throwable);
    }

    /**
     * Return true if a error message will be logged.
     * 
     * @return true if message will be logged
     */
    public boolean isErrorEnabled() {
        return m_logger.isLoggable(Level.SEVERE);
    }

    /**
     * Get the child logger with specified name.
     * 
     * @param name the name of child logger
     * @return the child logger
     */
    public Logger getChildLogger(final String name) {
        final String childName = m_logger.getName() + "." + name;
        return new Jdk14Logger(java.util.logging.Logger.getLogger(childName));
    }
}
