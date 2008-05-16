package org.picocontainer.logging.store.stores;

import org.picocontainer.logging.Logger;
import org.picocontainer.logging.loggers.ConsoleLogger;

/**
 * ConsoleLoggerStore extends AbstractLoggerStore to provide the implementation
 * specific that just writes to console.
 * 
 * @author Mauro Talevi
 */
public class ConsoleLoggerStore extends AbstractLoggerStore {
    /**
     * Creates a <code>ConsoleLoggerStore</code> using the specified Logger
     * level.
     * 
     * @param level the debug level of ConsoleLoggerStore
     * @throws Exception if fails to create or configure Logger
     */
    public ConsoleLoggerStore(final int level) throws Exception {
        setRootLogger(new ConsoleLogger(level));
    }

    /**
     * Creates new ConsoleLogger for the given category.
     */
    protected Logger createLogger(final String name) {
        return getRootLogger().getChildLogger(name);
    }

    /**
     * Closes the LoggerStore and shuts down the logger hierarchy.
     */
    public void close() {
    }
}
