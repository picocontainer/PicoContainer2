package org.picocontainer.logging.store.factories;

import java.io.InputStream;
import java.util.Map;
import java.util.Properties;
import org.picocontainer.logging.store.LoggerStore;
import org.picocontainer.logging.store.stores.Log4JLoggerStore;

/**
 * PropertyLog4JLoggerStoreFactory is an implementation of LoggerStoreFactory
 * for the Log4J Logger using a property configuration resource.
 * 
 * @author Mauro Talevi
 * @author Peter Donald
 */
public class PropertyLog4JLoggerStoreFactory extends AbstractLoggerStoreFactory {
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
            return new Log4JLoggerStore(properties);
        }

        final InputStream resource = getInputStream(config);
        if (null != resource) {
            return new Log4JLoggerStore(createPropertiesFromStream(resource));
        }

        return missingConfiguration();
    }

    private Properties createPropertiesFromStream(final InputStream resource) throws Exception {
        final Properties properties = new Properties();
        properties.load(resource);
        return properties;
    }
}
