package org.picocontainer.logging.store;

import org.picocontainer.logging.Logger;

/**
 * <p>
 * LoggerStore represents the logging hierarchy for a Logger, as defined by its
 * configuration.
 * </p>
 * <p>
 * The LoggerStore has an associated LoggerStoreFactory which also acts as a
 * configurator for the Logger.
 * </p>
 * <p>
 * Whenever an application has finished using the LoggerStore it will call the
 * close() method indicating that the logger hierarchy should also be shutdown.
 * </p>
 * 
 * @author Mauro Talevi
 * @author Peter Donald
 */
public interface LoggerStore {
    /**
     * Retrieves the root Logger from the store.
     * 
     * @return the Logger
     * @throws Exception if unable to retrieve Logger
     */
    Logger getLogger() throws Exception;

    /**
     * Retrieves a Logger hierarchy from the store for a given category name.
     * 
     * @param categoryName the name of the logger category.
     * @return the Logger
     * @throws Exception if unable to retrieve Logger
     */
    Logger getLogger(String categoryName) throws Exception;

    /**
     * Closes the LoggerStore and shuts down the logger hierarchy.
     */
    void close();
}
