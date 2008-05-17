/*
 * Copyright (C) PicoContainer Organization. All rights reserved.            
 * ------------------------------------------------------------------------- 
 * The software in this package is published under the terms of the BSD      
 * style license a copy of which has been included with this distribution in 
 * the LICENSE.txt file.                                                     
 */ 
package org.picocontainer.logging.store.factories;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.Map;
import java.util.Properties;
import org.picocontainer.logging.store.LoggerStore;
import org.picocontainer.logging.store.stores.Jdk14LoggerStore;

/**
 * Jdk14LoggerStoreFactory is an implementation of LoggerStoreFactory for the
 * Jkd14 Logger.
 * 
 * @author Mauro Talevi
 * @author Peter Donald
 */
public class Jdk14LoggerStoreFactory extends AbstractLoggerStoreFactory {
    /**
     * Creates a LoggerStore from a given set of configuration parameters.
     * 
     * @param config the Map of parameters for the configuration of the store
     * @return the LoggerStore
     * @throws Exception if unable to create the LoggerStore
     */
    protected LoggerStore doCreateLoggerStore(final Map<String,Object> config) throws Exception {
        final Properties properties = (Properties) config.get(Properties.class.getName());
        if (null != properties) {
            final ByteArrayOutputStream output = new ByteArrayOutputStream();
            properties.store(output, "");
            final ByteArrayInputStream input = new ByteArrayInputStream(output.toByteArray());
            return new Jdk14LoggerStore(input);
        }
        final InputStream resource = getInputStream(config);
        if (null != resource) {
            return new Jdk14LoggerStore(resource);
        }
        return missingConfiguration();
    }
}
