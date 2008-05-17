/*
 * Copyright (C) PicoContainer Organization. All rights reserved.
 * --------------------------------------------------------------------------
 * The software in this package is published under the terms of the BSD style
 * license a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.picocontainer.logging.store;

import java.io.IOException;
import java.util.Properties;

import javax.xml.parsers.ParserConfigurationException;

import org.junit.Test;
import org.picocontainer.logging.loggers.ConsoleLogger;
import org.picocontainer.logging.store.stores.ConsoleLoggerStore;
import org.picocontainer.logging.store.stores.JdkLoggerStore;
import org.picocontainer.logging.store.stores.Log4JLoggerStore;
import org.xml.sax.SAXException;

/**
 * @author Mauro Talevi
 * @author Peter Donald
 */
public class LoggerStoreTest extends AbstractTest {

    @Test(expected = LoggerNotFoundException.class)
    public void testNullRootLogger() {
        final LoggerStore store = new MalformedLoggerStore();
        store.getLogger();
    }

    // ConsoleLoggerStore tests
    @Test
    public void testConsoleLoggerStore() {
        final LoggerStore store = new ConsoleLoggerStore(ConsoleLogger.LEVEL_DEBUG);
        performConsoleTest(store, ConsoleLogger.LEVEL_DEBUG);
    }

    @Test
    public void testConsoleLoggerStoreNoDebug() {
        final LoggerStore store = new ConsoleLoggerStore(ConsoleLogger.LEVEL_DEBUG);
        performConsoleTest(store, ConsoleLogger.LEVEL_NONE);
    }

    // Log4JLoggerStore tests
    @Test
    public void testLog4JElementConfiguration() throws IOException, ParserConfigurationException, SAXException {
        final LoggerStore store = new Log4JLoggerStore(buildElement(
                getResource("org/picocontainer/logging/store/log4j.xml"), new org.apache.log4j.xml.Log4jEntityResolver(),
                null));
        runLoggerTest("log4j-xml", store, ConsoleLogger.LEVEL_DEBUG);
    }

    @Test
    public void testLog4JElementConfigurationNoDebug() throws IOException, ParserConfigurationException, SAXException {
        final LoggerStore store = new Log4JLoggerStore(buildElement(
                getResource("org/picocontainer/logging/store/log4j.xml"), new org.apache.log4j.xml.Log4jEntityResolver(),
                null));
        runLoggerTest("log4j-xml", store, ConsoleLogger.LEVEL_NONE);
    }

    @Test
    public void testLog4JElementConfigurationNoLog() throws IOException, ParserConfigurationException, SAXException {
        final LoggerStore store = new Log4JLoggerStore(buildElement(
                getResource("org/picocontainer/logging/store/log4j.xml"), new org.apache.log4j.xml.Log4jEntityResolver(),
                null));
        runLoggerTest("log4j-xml", store);
    }

    @Test
    public void testLog4JInputStreamConfiguration() throws IOException {
        final LoggerStore store = new Log4JLoggerStore(getResource("org/picocontainer/logging/store/log4j.xml"));
        runLoggerTest("log4j-xml", store, ConsoleLogger.LEVEL_DEBUG);
    }

    @Test
    public void testLog4JInputStreamConfigurationNoDebug() throws IOException {
        final LoggerStore store = new Log4JLoggerStore(getResource("org/picocontainer/logging/store/log4j.xml"));
        runLoggerTest("log4j-xml", store, ConsoleLogger.LEVEL_NONE);
    }

    @Test
    public void testLog4JInputStreamConfigurationNoLog() throws IOException {
        final LoggerStore store = new Log4JLoggerStore(getResource("org/picocontainer/logging/store/log4j.xml"));
        runLoggerTest("log4j-xml", store);
    }

    @Test
    public void testLog4JPropertiesConfiguration() throws IOException {
        final Properties properties = new Properties();
        properties.load(getResource("org/picocontainer/logging/store/log4j.properties"));
        final LoggerStore store = new Log4JLoggerStore(properties);
        runLoggerTest("log4j-properties", store, ConsoleLogger.LEVEL_DEBUG);
    }

    @Test
    public void testLog4JPropertiesConfigurationNoDebug() throws IOException {
        final Properties properties = new Properties();
        properties.load(getResource("org/picocontainer/logging/store/log4j.properties"));
        final LoggerStore store = new Log4JLoggerStore(properties);
        runLoggerTest("log4j-properties", store, ConsoleLogger.LEVEL_NONE);
    }

    @Test
    public void testLog4JPropertiesConfigurationNoLog() throws IOException {
        final Properties properties = new Properties();
        properties.load(getResource("org/picocontainer/logging/store/log4j.properties"));
        final LoggerStore store = new Log4JLoggerStore(properties);
        runLoggerTest("log4j-properties", store);
    }

    // JDKLoggerStore tests
    @Test
    public void testJDKConfiguration() throws IOException {
        final LoggerStore store = new JdkLoggerStore(getResource("org/picocontainer/logging/store/logging.properties"));
        runLoggerTest("jdk", store, ConsoleLogger.LEVEL_DEBUG);
    }

    @Test
    public void testJDKConfigurationNoDebug() throws IOException {
        final LoggerStore store = new JdkLoggerStore(getResource("org/picocontainer/logging/store/logging.properties"));
        runLoggerTest("jdk", store, ConsoleLogger.LEVEL_NONE);
    }

    @Test
    public void testJDKConfigurationNoLog() throws IOException {
        final LoggerStore store = new JdkLoggerStore(getResource("org/picocontainer/logging/store/logging.properties"));
        runLoggerTest("jdk", store);
    }

}
