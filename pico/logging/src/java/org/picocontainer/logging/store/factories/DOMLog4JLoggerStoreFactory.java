/*
 * Copyright (C) PicoContainer Organization. All rights reserved.            
 * ------------------------------------------------------------------------- 
 * The software in this package is published under the terms of the BSD      
 * style license a copy of which has been included with this distribution in 
 * the LICENSE.txt file.                                                     
 */ 
package org.picocontainer.logging.store.factories;

import java.io.InputStream;
import java.util.Map;
import org.picocontainer.logging.store.LoggerStore;
import org.picocontainer.logging.store.stores.Log4JLoggerStore;
import org.w3c.dom.Element;

/**
 * DOMLog4JLoggerStoreFactory is an implementation of LoggerStoreFactory for the
 * Log4J Logger using a DOM configuration resource.
 * 
 * @author Mauro Talevi
 * @author Peter Donald
 */
public class DOMLog4JLoggerStoreFactory extends AbstractLoggerStoreFactory {
    /**
     * Creates a LoggerStore from a given set of configuration parameters.
     * 
     * @param config the Map of parameters for the configuration of the store
     * @return the LoggerStore
     * @throws Exception if unable to create the LoggerStore
     */
    protected LoggerStore doCreateLoggerStore(final Map<String,Object> config) throws Exception {
        final Element element = (Element) config.get(Element.class.getName());
        if (null != element) {
            return new Log4JLoggerStore(element);
        }

        final InputStream resource = getInputStream(config);
        if (null != resource) {
            return new Log4JLoggerStore(resource);
        }
        return missingConfiguration();
    }
}
