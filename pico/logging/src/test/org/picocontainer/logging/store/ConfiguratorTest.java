/*
 * Copyright (C) PicoContainer Organization. All rights reserved.            
 * ------------------------------------------------------------------------- 
 * The software in this package is published under the terms of the BSD      
 * style license a copy of which has been included with this distribution in 
 * the LICENSE.txt file.                                                     
 */ 
package org.picocontainer.logging.store;
import static org.junit.Assert.fail;

import org.junit.Test;
import org.picocontainer.logging.loggers.ConsoleLogger;

/**
 * @author Mauro Talevi
 * @author Peter Donald
 */
public class ConfiguratorTest extends AbstractTest {

    @Test public void testInvalidConfiguratorType() throws Exception {
        try {
            Configurator.createLoggerStore("blah", "org/picocontainer/logging/store/logging.properties");
            fail("Expected exception as invalid type specified");
        } catch (final Exception e) {
        }
    }

    @Test public void testLog4JDOMConfigurator() throws Exception {
        runLoggerTest("log4j-xml", Configurator.createLoggerStore(Configurator.LOG4J_DOM, getResource("log4j.xml")),
                ConsoleLogger.LEVEL_DEBUG);
    }

    @Test public void testLog4JDOMConfiguratorNoDebug() throws Exception {
        runLoggerTest("log4j-xml", Configurator.createLoggerStore(Configurator.LOG4J_DOM, getResource("log4j.xml")),
                ConsoleLogger.LEVEL_NONE);
    }

    @Test public void testLog4JDOMConfiguratorNoLog() throws Exception {
        runLoggerTest("log4j-xml", Configurator.createLoggerStore(Configurator.LOG4J_DOM, getResource("log4j.xml")));
    }

    @Test public void testLog4JPropertyConfigurator() throws Exception {
        runLoggerTest("log4j-properties", Configurator.createLoggerStore(Configurator.LOG4J_PROPERTY,
                getResource("log4j.properties")), ConsoleLogger.LEVEL_DEBUG);
    }

    @Test public void testLog4JPropertyConfiguratorNoDebug() throws Exception {
        runLoggerTest("log4j-properties", Configurator.createLoggerStore(Configurator.LOG4J_PROPERTY,
                getResource("log4j.properties")), ConsoleLogger.LEVEL_NONE);
    }

    @Test public void testLog4JPropertyConfiguratorNoLog() throws Exception {
        runLoggerTest("log4j-properties", Configurator.createLoggerStore(Configurator.LOG4J_PROPERTY,
                getResource("log4j.properties")));
    }

    @Test public void testJDK14Configurator() throws Exception {
        runLoggerTest("jdk14", Configurator.createLoggerStore(Configurator.JDK14, getResource("logging.properties")),
                ConsoleLogger.LEVEL_DEBUG);
    }

    @Test public void testJDK14ConfiguratorNoDebug() throws Exception {
        runLoggerTest("jdk14", Configurator.createLoggerStore(Configurator.JDK14, getResource("logging.properties")),
                ConsoleLogger.LEVEL_NONE);
    }

    @Test public void testJDK14ConfiguratorNoLog() throws Exception {
        runLoggerTest("jdk14", Configurator.createLoggerStore(Configurator.JDK14, getResource("logging.properties")));
    }

}
