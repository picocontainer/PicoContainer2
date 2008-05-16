package org.picocontainer.logging.store;

import java.util.Properties;

import org.picocontainer.logging.loggers.ConsoleLogger;
import org.picocontainer.logging.store.stores.ConsoleLoggerStore;
import org.picocontainer.logging.store.stores.Jdk14LoggerStore;
import org.picocontainer.logging.store.stores.Log4JLoggerStore;

/**
 * @author Mauro Talevi
 * @author Peter Donald
 */
public class LoggerStoreTest extends AbstractTest {

    public LoggerStoreTest(final String name) {
        super(name);
    }

    public void testNullRootLogger() throws Exception {
        final LoggerStore store = new MalformedLoggerStore();
        try {
            store.getLogger();
            fail("Expected to get an exception as no root logger is defined.");
        } catch (final Exception e) {
        }
    }

    // ConsoleLoggerStore tests
    public void testConsoleLoggerStore() throws Exception {
        final LoggerStore store = new ConsoleLoggerStore(ConsoleLogger.LEVEL_DEBUG);
        performConsoleTest(store, ConsoleLogger.LEVEL_DEBUG);
    }

    public void testConsoleLoggerStoreNoDebug() throws Exception {
        final LoggerStore store = new ConsoleLoggerStore(ConsoleLogger.LEVEL_DEBUG);
        performConsoleTest(store, ConsoleLogger.LEVEL_NONE);
    }

    // Log4JLoggerStore tests
    public void testLog4JElementConfiguration() throws Exception {
        final LoggerStore store = new Log4JLoggerStore(buildElement(getResource("log4j.xml"),
                new org.apache.log4j.xml.Log4jEntityResolver(), null));
        runLoggerTest("log4j-xml", store, ConsoleLogger.LEVEL_DEBUG);
    }

    public void testLog4JElementConfigurationNoDebug() throws Exception {
        final LoggerStore store = new Log4JLoggerStore(buildElement(getResource("log4j.xml"),
                new org.apache.log4j.xml.Log4jEntityResolver(), null));
        runLoggerTest("log4j-xml", store, ConsoleLogger.LEVEL_NONE);
    }

    public void testLog4JElementConfigurationNoLog() throws Exception {
        final LoggerStore store = new Log4JLoggerStore(buildElement(getResource("log4j.xml"),
                new org.apache.log4j.xml.Log4jEntityResolver(), null));
        runLoggerTest("log4j-xml", store);
    }

    public void testLog4JInputStreamConfiguration() throws Exception {
        final LoggerStore store = new Log4JLoggerStore(getResource("log4j.xml"));
        runLoggerTest("log4j-xml", store, ConsoleLogger.LEVEL_DEBUG);
    }

    public void testLog4JInputStreamConfigurationNoDebug() throws Exception {
        final LoggerStore store = new Log4JLoggerStore(getResource("log4j.xml"));
        runLoggerTest("log4j-xml", store, ConsoleLogger.LEVEL_NONE);
    }

    public void testLog4JInputStreamConfigurationNoLog() throws Exception {
        final LoggerStore store = new Log4JLoggerStore(getResource("log4j.xml"));
        runLoggerTest("log4j-xml", store);
    }

    public void testLog4JPropertiesConfiguration() throws Exception {
        final Properties properties = new Properties();
        properties.load(getResource("log4j.properties"));
        final LoggerStore store = new Log4JLoggerStore(properties);
        runLoggerTest("log4j-properties", store, ConsoleLogger.LEVEL_DEBUG);
    }

    public void testLog4JPropertiesConfigurationNoDebug() throws Exception {
        final Properties properties = new Properties();
        properties.load(getResource("log4j.properties"));
        final LoggerStore store = new Log4JLoggerStore(properties);
        runLoggerTest("log4j-properties", store, ConsoleLogger.LEVEL_NONE);
    }

    public void testLog4JPropertiesConfigurationNoLog() throws Exception {
        final Properties properties = new Properties();
        properties.load(getResource("log4j.properties"));
        final LoggerStore store = new Log4JLoggerStore(properties);
        runLoggerTest("log4j-properties", store);
    }

    // JDK14LoggerStore tests
    public void testJDK14Configuration() throws Exception {
        final LoggerStore store = new Jdk14LoggerStore(getResource("logging.properties"));
        runLoggerTest("jdk14", store, ConsoleLogger.LEVEL_DEBUG);
    }

    public void testJDK14ConfigurationNoDebug() throws Exception {
        final LoggerStore store = new Jdk14LoggerStore(getResource("logging.properties"));
        runLoggerTest("jdk14", store, ConsoleLogger.LEVEL_NONE);
    }

    public void testJDK14ConfigurationNoLog() throws Exception {
        final LoggerStore store = new Jdk14LoggerStore(getResource("logging.properties"));
        runLoggerTest("jdk14", store);
    }

}
