package org.picocontainer.logging;

/**
 * This interface was a facade for different Logger subsystems.
 */
public interface Logger {
    /**
     * Log a trace message.
     * 
     * @param message the message
     */
    void trace(String message);

    /**
     * Log a trace message with an associated throwable.
     * 
     * @param message the message
     * @param throwable the throwable
     */
    void trace(String message, Throwable throwable);

    /**
     * Return true if a trace message will be logged.
     * 
     * @return true if message will be logged
     */
    boolean isTraceEnabled();

    /**
     * Log a debug message.
     * 
     * @param message the message
     */
    void debug(String message);

    /**
     * Log a debug message with an associated throwable.
     * 
     * @param message the message
     * @param throwable the throwable
     */
    void debug(String message, Throwable throwable);

    /**
     * Return true if a debug message will be logged.
     * 
     * @return true if message will be logged
     */
    boolean isDebugEnabled();

    /**
     * Log a info message.
     * 
     * @param message the message
     */
    void info(String message);

    /**
     * Log a info message with an associated throwable.
     * 
     * @param message the message
     * @param throwable the throwable
     */
    void info(String message, Throwable throwable);

    /**
     * Return true if an info message will be logged.
     * 
     * @return true if message will be logged
     */
    boolean isInfoEnabled();

    /**
     * Log a warn message.
     * 
     * @param message the message
     */
    void warn(String message);

    /**
     * Log a warn message with an associated throwable.
     * 
     * @param message the message
     * @param throwable the throwable
     */
    void warn(String message, Throwable throwable);

    /**
     * Return true if a warn message will be logged.
     * 
     * @return true if message will be logged
     */
    boolean isWarnEnabled();

    /**
     * Log a error message.
     * 
     * @param message the message
     */
    void error(String message);

    /**
     * Log a error message with an associated throwable.
     * 
     * @param message the message
     * @param throwable the throwable
     */
    void error(String message, Throwable throwable);

    /**
     * Return true if a error message will be logged.
     * 
     * @return true if message will be logged
     */
    boolean isErrorEnabled();

    /**
     * Get the child logger with specified name.
     * 
     * @param name the name of child logger
     * @return the child logger
     */
    Logger getChildLogger(String name);
}
