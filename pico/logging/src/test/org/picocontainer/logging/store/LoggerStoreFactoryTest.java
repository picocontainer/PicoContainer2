package org.picocontainer.logging.store;

import java.util.HashMap;
import java.util.Properties;

import org.picocontainer.logging.loggers.ConsoleLogger;
import org.picocontainer.logging.store.factories.ConsoleLoggerStoreFactory;
import org.picocontainer.logging.store.factories.InitialLoggerStoreFactory;
import org.picocontainer.logging.store.factories.Jdk14LoggerStoreFactory;

/**
 * @author Mauro Talevi
 * @author Peter Donald
 */
public class LoggerStoreFactoryTest extends AbstractTest {
    public LoggerStoreFactoryTest(final String name) {
        super(name);
    }

    // InitialLoggerStoreFactory tests
    public void testInitialLoggerStoreFactoryUsingDefaults() throws Exception {
        final HashMap<String,Object> config = new HashMap<String,Object>();
        config.put(ClassLoader.class.getName(), ClassLoader.getSystemClassLoader().getParent());
        final InitialLoggerStoreFactory factory = new InitialLoggerStoreFactory();
        try {
            factory.createLoggerStore(config);
            fail("Expected to not be able to create LoggerStoreFactory as no type was specified or on ClassPath");
        } catch (final Exception e) {
        }
    }

    public void testInitialLoggerStoreFactoryUsingSpecifiedType() throws Exception {
        final HashMap<String,Object> config = new HashMap<String,Object>();
        config.put(InitialLoggerStoreFactory.INITIAL_FACTORY, ConsoleLoggerStoreFactory.class.getName());
        final InitialLoggerStoreFactory factory = new InitialLoggerStoreFactory();
        final LoggerStore store = factory.createLoggerStore(config);
        performConsoleTest(store, ConsoleLogger.LEVEL_DEBUG);
    }

    public void testInitialLoggerStoreFactoryWithInvalidType() throws Exception {
        final HashMap<String,Object> config = new HashMap<String,Object>();
        config.put(InitialLoggerStoreFactory.INITIAL_FACTORY, "Blah");
        final InitialLoggerStoreFactory factory = new InitialLoggerStoreFactory();
        try {
            factory.createLoggerStore(config);
            fail("Expected exception as invalid type specified");
        } catch (final Exception e) {
        }
    }

    public void TODOtestInitialLoggerStoreFactoryFromConfigurerClassLoader() throws Exception {
        Thread.currentThread().setContextClassLoader(null);
        final HashMap<String,Object> config = new HashMap<String,Object>();
        runFactoryTest(new InitialLoggerStoreFactory(), ConsoleLogger.LEVEL_DEBUG, config, "log4j-properties");
    }

    public void TODOtestInitialLoggerStoreFactoryFromSpecifiedClassLoader() throws Exception {
        Thread.currentThread().setContextClassLoader(null);
        final HashMap<String,Object> config = new HashMap<String,Object>();
        config.put(ClassLoader.class.getName(), InitialLoggerStoreFactory.class.getClassLoader());
        runFactoryTest(new InitialLoggerStoreFactory(), ConsoleLogger.LEVEL_DEBUG, config, "log4j-properties");
    }

    public void TODOtestInitialLoggerStoreFactoryFromContextClassLoader() throws Exception {
        Thread.currentThread().setContextClassLoader(InitialLoggerStoreFactory.class.getClassLoader());
        final HashMap<String,Object> config = new HashMap<String,Object>();
        runFactoryTest(new InitialLoggerStoreFactory(), ConsoleLogger.LEVEL_DEBUG, config, "log4j-properties");
    }

    // JDK14LoggerStoreFactory tests
    public void testJDK14LoggerStoreFactoryInvalidInput() throws Exception {
        runInvalidInputData(new Jdk14LoggerStoreFactory());
    }

    public void testJDK14LoggerStoreFactoryWithProperties() throws Exception {
        final Properties properties = new Properties();
        properties.load(getResource("logging.properties"));
        final HashMap<String,Object> config = new HashMap<String,Object>();
        config.put(Properties.class.getName(), properties);

        runFactoryTest(new Jdk14LoggerStoreFactory(), ConsoleLogger.LEVEL_DEBUG, config, "jdk14");
    }

    public void testJDK14LoggerStoreFactoryWithStreams() throws Exception {
        runStreamBasedFactoryTest("logging.properties", new Jdk14LoggerStoreFactory(), ConsoleLogger.LEVEL_DEBUG,
                "jdk14", new HashMap<String,Object>());
    }

}
