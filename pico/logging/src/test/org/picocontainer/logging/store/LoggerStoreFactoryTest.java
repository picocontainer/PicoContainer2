/*
 * Copyright (C) PicoContainer Organization. All rights reserved.            
 * ------------------------------------------------------------------------- 
 * The software in this package is published under the terms of the BSD      
 * style license a copy of which has been included with this distribution in 
 * the LICENSE.txt file.                                                     
 */ 
package org.picocontainer.logging.store;

import static org.junit.Assert.fail;

import java.util.HashMap;
import java.util.Properties;

import org.junit.Test;
import org.picocontainer.logging.loggers.ConsoleLogger;
import org.picocontainer.logging.store.factories.ConsoleLoggerStoreFactory;
import org.picocontainer.logging.store.factories.InitialLoggerStoreFactory;
import org.picocontainer.logging.store.factories.Jdk14LoggerStoreFactory;

/**
 * @author Mauro Talevi
 * @author Peter Donald
 */
public class LoggerStoreFactoryTest extends AbstractTest {

    // InitialLoggerStoreFactory tests
    @Test public void testInitialLoggerStoreFactoryUsingDefaults() throws Exception {
        final HashMap<String,Object> config = new HashMap<String,Object>();
        config.put(ClassLoader.class.getName(), ClassLoader.getSystemClassLoader().getParent());
        final InitialLoggerStoreFactory factory = new InitialLoggerStoreFactory();
        try {
            factory.createLoggerStore(config);
            fail("Expected to not be able to create LoggerStoreFactory as no type was specified or on ClassPath");
        } catch (final Exception e) {
        }
    }

    @Test public void testInitialLoggerStoreFactoryUsingSpecifiedType() throws Exception {
        final HashMap<String,Object> config = new HashMap<String,Object>();
        config.put(InitialLoggerStoreFactory.INITIAL_FACTORY, ConsoleLoggerStoreFactory.class.getName());
        final InitialLoggerStoreFactory factory = new InitialLoggerStoreFactory();
        final LoggerStore store = factory.createLoggerStore(config);
        performConsoleTest(store, ConsoleLogger.LEVEL_DEBUG);
    }

    @Test public void testInitialLoggerStoreFactoryWithInvalidType() throws Exception {
        final HashMap<String,Object> config = new HashMap<String,Object>();
        config.put(InitialLoggerStoreFactory.INITIAL_FACTORY, "Blah");
        final InitialLoggerStoreFactory factory = new InitialLoggerStoreFactory();
        try {
            factory.createLoggerStore(config);
            fail("Expected exception as invalid type specified");
        } catch (final Exception e) {
        }
    }

    //@Test TODO
    public void testInitialLoggerStoreFactoryFromConfigurerClassLoader() throws Exception {
        Thread.currentThread().setContextClassLoader(null);
        final HashMap<String,Object> config = new HashMap<String,Object>();
        runFactoryTest(new InitialLoggerStoreFactory(), ConsoleLogger.LEVEL_DEBUG, config, "log4j-properties");
    }

    //@Test TODO
    public void testInitialLoggerStoreFactoryFromSpecifiedClassLoader() throws Exception {
        Thread.currentThread().setContextClassLoader(null);
        final HashMap<String,Object> config = new HashMap<String,Object>();
        config.put(ClassLoader.class.getName(), InitialLoggerStoreFactory.class.getClassLoader());
        runFactoryTest(new InitialLoggerStoreFactory(), ConsoleLogger.LEVEL_DEBUG, config, "log4j-properties");
    }

    //@Test TODO
    public void testInitialLoggerStoreFactoryFromContextClassLoader() throws Exception {
        Thread.currentThread().setContextClassLoader(InitialLoggerStoreFactory.class.getClassLoader());
        final HashMap<String,Object> config = new HashMap<String,Object>();
        runFactoryTest(new InitialLoggerStoreFactory(), ConsoleLogger.LEVEL_DEBUG, config, "log4j-properties");
    }

    // JDK14LoggerStoreFactory tests
    @Test public void testJDK14LoggerStoreFactoryInvalidInput() throws Exception {
        runInvalidInputData(new Jdk14LoggerStoreFactory());
    }

    @Test public void testJDK14LoggerStoreFactoryWithProperties() throws Exception {
        final Properties properties = new Properties();
        properties.load(getResource("logging.properties"));
        final HashMap<String,Object> config = new HashMap<String,Object>();
        config.put(Properties.class.getName(), properties);

        runFactoryTest(new Jdk14LoggerStoreFactory(), ConsoleLogger.LEVEL_DEBUG, config, "jdk14");
    }

    @Test public void testJDK14LoggerStoreFactoryWithStreams() throws Exception {
        runStreamBasedFactoryTest("logging.properties", new Jdk14LoggerStoreFactory(), ConsoleLogger.LEVEL_DEBUG,
                "jdk14", new HashMap<String,Object>());
    }

}
