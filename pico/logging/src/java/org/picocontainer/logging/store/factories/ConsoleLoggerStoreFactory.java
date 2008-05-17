/*
 * Copyright (C) PicoContainer Organization. All rights reserved.            
 * ------------------------------------------------------------------------- 
 * The software in this package is published under the terms of the BSD      
 * style license a copy of which has been included with this distribution in 
 * the LICENSE.txt file.                                                     
 */ 
package org.picocontainer.logging.store.factories;

import java.util.Map;

import org.picocontainer.logging.loggers.ConsoleLogger;
import org.picocontainer.logging.store.LoggerStore;
import org.picocontainer.logging.store.stores.ConsoleLoggerStore;

/**
 * This is a basic factory for ConsoleLoggerStore.
 * 
 * @author Peter Donald
 */
public class ConsoleLoggerStoreFactory extends AbstractLoggerStoreFactory {
    /**
     * Creates a LoggerStore from a given set of configuration parameters.
     * 
     * @param config the Map of parameters for the configuration of the store
     * @return the LoggerStore
     * @throws Exception if unable to create the LoggerStore
     */
    protected LoggerStore doCreateLoggerStore(final Map<String,Object> config) throws Exception {
        final int level = ConsoleLogger.LEVEL_INFO;
        return new ConsoleLoggerStore(level);
    }
}
