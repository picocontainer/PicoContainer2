/*
 * Copyright (C) PicoContainer Organization. All rights reserved.
 * --------------------------------------------------------------------------
 * The software in this package is published under the terms of the BSD style
 * license a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.picocontainer.logging.store;

import java.io.IOException;
import java.util.HashMap;
import java.util.Properties;

import org.junit.Test;
import org.picocontainer.logging.loggers.ConsoleLogger;
import org.picocontainer.logging.store.factories.ConsoleLoggerStoreFactory;
import org.picocontainer.logging.store.factories.InitialLoggerStoreFactory;
import org.picocontainer.logging.store.factories.JdkLoggerStoreFactory;

/**
 * @author Mauro Talevi
 * @author Peter Donald
 */
public class LoggerStoreFactoryTest extends AbstractTest {

    // InitialLoggerStoreFactory tests
    @Test(expected = LoggerStoreCreationException.class)
    public void testInitialLoggerStoreFactoryUsingDefaults() {
        final HashMap<String, Object> config = new HashMap<String, Object>();
        config.put(ClassLoader.class.getName(), ClassLoader.getSystemClassLoader().getParent());
        final InitialLoggerStoreFactory factory = new InitialLoggerStoreFactory();
        factory.createLoggerStore(config);
    }

    @Test
    public void testInitialLoggerStoreFactoryUsingSpecifiedType() {
        final HashMap<String, Object> config = new HashMap<String, Object>();
        config.put(InitialLoggerStoreFactory.INITIAL_FACTORY, ConsoleLoggerStoreFactory.class.getName());
        final InitialLoggerStoreFactory factory = new InitialLoggerStoreFactory();
        final LoggerStore store = factory.createLoggerStore(config);
        performConsoleTest(store, ConsoleLogger.LEVEL_DEBUG);
    }

    @Test(expected = LoggerStoreCreationException.class)
    public void testInitialLoggerStoreFactoryWithInvalidType() {
        final HashMap<String, Object> config = new HashMap<String, Object>();
        config.put(InitialLoggerStoreFactory.INITIAL_FACTORY, "Blah");
        final InitialLoggerStoreFactory factory = new InitialLoggerStoreFactory();
        factory.createLoggerStore(config);
    }

    // @Test TODO
    public void testInitialLoggerStoreFactoryFromConfigurerClassLoader() throws IOException {
        Thread.currentThread().setContextClassLoader(null);
        final HashMap<String, Object> config = new HashMap<String, Object>();
        runFactoryTest(new InitialLoggerStoreFactory(), ConsoleLogger.LEVEL_DEBUG, config, "log4j-properties");
    }

    // @Test TODO
    public void testInitialLoggerStoreFactoryFromSpecifiedClassLoader() throws IOException {
        Thread.currentThread().setContextClassLoader(null);
        final HashMap<String, Object> config = new HashMap<String, Object>();
        config.put(ClassLoader.class.getName(), InitialLoggerStoreFactory.class.getClassLoader());
        runFactoryTest(new InitialLoggerStoreFactory(), ConsoleLogger.LEVEL_DEBUG, config, "log4j-properties");
    }

    // @Test TODO
    public void testInitialLoggerStoreFactoryFromContextClassLoader() throws IOException {
        Thread.currentThread().setContextClassLoader(InitialLoggerStoreFactory.class.getClassLoader());
        final HashMap<String, Object> config = new HashMap<String, Object>();
        runFactoryTest(new InitialLoggerStoreFactory(), ConsoleLogger.LEVEL_DEBUG, config, "log4j-properties");
    }

    // JDKLoggerStoreFactory tests
    @Test
    public void testJDKLoggerStoreFactoryInvalidInput() throws Exception {
        runInvalidInputData(new JdkLoggerStoreFactory());
    }

    @Test
    public void testJDKLoggerStoreFactoryWithProperties() throws IOException {
        final Properties properties = new Properties();
        properties.load(getResource("logging.properties"));
        final HashMap<String, Object> config = new HashMap<String, Object>();
        config.put(Properties.class.getName(), properties);
        runFactoryTest(new JdkLoggerStoreFactory(), ConsoleLogger.LEVEL_DEBUG, config, "jdk");
    }

    @Test
    public void testJDKLoggerStoreFactoryWithStreams() throws IOException {
        runStreamBasedFactoryTest("logging.properties", new JdkLoggerStoreFactory(), ConsoleLogger.LEVEL_DEBUG,
                "jdk", new HashMap<String, Object>());
    }

}
