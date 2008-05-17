/*
 * Copyright (C) PicoContainer Organization. All rights reserved.
 * --------------------------------------------------------------------------
 * The software in this package is published under the terms of the BSD style
 * license a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.picocontainer.logging.store;

import java.io.IOException;

import org.junit.Test;
import org.picocontainer.logging.loggers.ConsoleLogger;

/**
 * @author Mauro Talevi
 * @author Peter Donald
 */
public class ConfiguratorTest extends AbstractTest {

    @Test(expected = LoggerStoreCreationException.class)
    public void testInvalidConfiguratorType() throws IOException {
        Configurator.createLoggerStore("blah", "org/picocontainer/logging/store/logging.properties");
    }

    @Test
    public void testLog4JDOMConfigurator() throws IOException {
        runLoggerTest("log4j-xml", Configurator.createLoggerStore(Configurator.LOG4J_DOM,
                getResource("org/picocontainer/logging/store/log4j.xml")), ConsoleLogger.LEVEL_DEBUG);
    }

    @Test
    public void testLog4JDOMConfiguratorNoDebug() throws IOException {
        runLoggerTest("log4j-xml", Configurator.createLoggerStore(Configurator.LOG4J_DOM,
                getResource("org/picocontainer/logging/store/log4j.xml")), ConsoleLogger.LEVEL_NONE);
    }

    @Test
    public void testLog4JDOMConfiguratorNoLog() throws IOException {
        runLoggerTest("log4j-xml", Configurator.createLoggerStore(Configurator.LOG4J_DOM,
                getResource("org/picocontainer/logging/store/log4j.xml")));
    }

    @Test
    public void testLog4JPropertyConfigurator() throws IOException {
        runLoggerTest("log4j-properties", Configurator.createLoggerStore(Configurator.LOG4J_PROPERTY,
                getResource("org/picocontainer/logging/store/log4j.properties")), ConsoleLogger.LEVEL_DEBUG);
    }

    @Test
    public void testLog4JPropertyConfiguratorNoDebug() throws IOException {
        runLoggerTest("log4j-properties", Configurator.createLoggerStore(Configurator.LOG4J_PROPERTY,
                getResource("org/picocontainer/logging/store/log4j.properties")), ConsoleLogger.LEVEL_NONE);
    }

    @Test
    public void testLog4JPropertyConfiguratorNoLog() throws IOException {
        runLoggerTest("log4j-properties", Configurator.createLoggerStore(Configurator.LOG4J_PROPERTY,
                getResource("org/picocontainer/logging/store/log4j.properties")));
    }

    @Test
    public void testJDKConfigurator() throws IOException {
        runLoggerTest("jdk", Configurator.createLoggerStore(Configurator.JDK,
                getResource("org/picocontainer/logging/store/logging.properties")), ConsoleLogger.LEVEL_DEBUG);
    }

    @Test
    public void testJDKConfiguratorNoDebug() throws IOException {
        runLoggerTest("jdk", Configurator.createLoggerStore(Configurator.JDK,
                getResource("org/picocontainer/logging/store/logging.properties")), ConsoleLogger.LEVEL_NONE);
    }

    @Test
    public void testJDKConfiguratorNoLog() throws IOException {
        runLoggerTest("jdk", Configurator.createLoggerStore(Configurator.JDK,
                getResource("org/picocontainer/logging/store/logging.properties")));
    }

}
