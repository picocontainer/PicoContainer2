package org.picocontainer.logging.store;

import org.junit.Test;
import org.picocontainer.logging.Logger;
import org.picocontainer.logging.loggers.NullLogger;

/**
 * 
 * @author Paul Hammant
 */
public class ContrastingEndUserExamplesTest {

    public static class DemoComponentOne {
        private final Logger logger;

        public DemoComponentOne(Logger logger) {
            this.logger = logger;
        }
    }

    @Test
    public void simpleInjectionOfLogger() {

        DemoComponentOne dc1 = new DemoComponentOne(new NullLogger());

    }

    public static class DemoComponentTwo {
        private final Logger logger;

        public DemoComponentTwo(LoggerStore loggerStore) {
            this.logger = loggerStore.getLogger("someLogger");
        }
    }

    @Test
    public void moreSophisticatedCase() {

        DemoComponentTwo dc2 = new DemoComponentTwo(new LoggerStore() {
            public Logger getLogger() {
                return new NullLogger();
            }

            public Logger getLogger(String categoryName) {
                return getLogger();
            }

            public void close() {
            }
        });
    }


}
