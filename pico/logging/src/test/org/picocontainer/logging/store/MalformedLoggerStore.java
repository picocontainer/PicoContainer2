package org.picocontainer.logging.store;

import org.picocontainer.logging.Logger;
import org.picocontainer.logging.store.stores.AbstractLoggerStore;

/**
 * @author Peter Donald
 */
public class MalformedLoggerStore extends AbstractLoggerStore {
    protected Logger createLogger(String name) {
        return null;
    }

    public void close() {
    }
}
