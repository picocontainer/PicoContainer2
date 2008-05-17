/*
 * Copyright (C) PicoContainer Organization. All rights reserved.            
 * ------------------------------------------------------------------------- 
 * The software in this package is published under the terms of the BSD      
 * style license a copy of which has been included with this distribution in 
 * the LICENSE.txt file.                                                     
 */ 
package org.picocontainer.logging.store;

import static org.junit.Assert.fail;

import java.util.Properties;

import org.junit.Test;
import org.picocontainer.logging.loggers.ConsoleLogger;
import org.picocontainer.logging.store.stores.ConsoleLoggerStore;
import org.picocontainer.logging.store.stores.Jdk14LoggerStore;
import org.picocontainer.logging.store.stores.Log4JLoggerStore;

/**
 * @author Mauro Talevi
 * @author Peter Donald
 */
public class LoggerStoreTest extends AbstractTest {

    @Test public void testNullRootLogger() throws Exception {
        final LoggerStore store = new MalformedLoggerStore();
        try {
            store.getLogger();
            fail("Expected to get an exception as no root logger is defined.");
        } catch (final Exception e) {
        }
    }

    // ConsoleLoggerStore tests
    @Test public void testConsoleLoggerStore() throws Exception {
        final LoggerStore store = new ConsoleLoggerStore(ConsoleLogger.LEVEL_DEBUG);
        performConsoleTest(store, ConsoleLogger.LEVEL_DEBUG);
    }

    @Test public void testConsoleLoggerStoreNoDebug() throws Exception {
        final LoggerStore store = new ConsoleLoggerStore(ConsoleLogger.LEVEL_DEBUG);
        performConsoleTest(store, ConsoleLogger.LEVEL_NONE);
    }

    // Log4JLoggerStore tests
    @Test public void testLog4JElementConfiguration() throws Exception {
        final LoggerStore store = new Log4JLoggerStore(buildElement(getResource("log4j.xml"),
                new org.apache.log4j.xml.Log4jEntityResolver(), null));
        runLoggerTest("log4j-xml", store, ConsoleLogger.LEVEL_DEBUG);
    }

    @Test public void testLog4JElementConfigurationNoDebug() throws Exception {
        final LoggerStore store = new Log4JLoggerStore(buildElement(getResource("log4j.xml"),
                new org.apache.log4j.xml.Log4jEntityResolver(), null));
        runLoggerTest("log4j-xml", store, ConsoleLogger.LEVEL_NONE);
    }

    @Test public void testLog4JElementConfigurationNoLog() throws Exception {
        final LoggerStore store = new Log4JLoggerStore(buildElement(getResource("log4j.xml"),
                new org.apache.log4j.xml.Log4jEntityResolver(), null));
        runLoggerTest("log4j-xml", store);
    }

    @Test public void testLog4JInputStreamConfiguration() throws Exception {
        final LoggerStore store = new Log4JLoggerStore(getResource("log4j.xml"));
        runLoggerTest("log4j-xml", store, ConsoleLogger.LEVEL_DEBUG);
    }

    @Test public void testLog4JInputStreamConfigurationNoDebug() throws Exception {
        final LoggerStore store = new Log4JLoggerStore(getResource("log4j.xml"));
        runLoggerTest("log4j-xml", store, ConsoleLogger.LEVEL_NONE);
    }

    @Test public void testLog4JInputStreamConfigurationNoLog() throws Exception {
        final LoggerStore store = new Log4JLoggerStore(getResource("log4j.xml"));
        runLoggerTest("log4j-xml", store);
    }

    @Test public void testLog4JPropertiesConfiguration() throws Exception {
        final Properties properties = new Properties();
        properties.load(getResource("log4j.properties"));
        final LoggerStore store = new Log4JLoggerStore(properties);
        runLoggerTest("log4j-properties", store, ConsoleLogger.LEVEL_DEBUG);
    }

    @Test public void testLog4JPropertiesConfigurationNoDebug() throws Exception {
        final Properties properties = new Properties();
        properties.load(getResource("log4j.properties"));
        final LoggerStore store = new Log4JLoggerStore(properties);
        runLoggerTest("log4j-properties", store, ConsoleLogger.LEVEL_NONE);
    }

    @Test public void testLog4JPropertiesConfigurationNoLog() throws Exception {
        final Properties properties = new Properties();
        properties.load(getResource("log4j.properties"));
        final LoggerStore store = new Log4JLoggerStore(properties);
        runLoggerTest("log4j-properties", store);
    }

    // JDK14LoggerStore tests
    @Test public void testJDK14Configuration() throws Exception {
        final LoggerStore store = new Jdk14LoggerStore(getResource("logging.properties"));
        runLoggerTest("jdk14", store, ConsoleLogger.LEVEL_DEBUG);
    }

    @Test public void testJDK14ConfigurationNoDebug() throws Exception {
        final LoggerStore store = new Jdk14LoggerStore(getResource("logging.properties"));
        runLoggerTest("jdk14", store, ConsoleLogger.LEVEL_NONE);
    }

    @Test public void testJDK14ConfigurationNoLog() throws Exception {
        final LoggerStore store = new Jdk14LoggerStore(getResource("logging.properties"));
        runLoggerTest("jdk14", store);
    }

}
