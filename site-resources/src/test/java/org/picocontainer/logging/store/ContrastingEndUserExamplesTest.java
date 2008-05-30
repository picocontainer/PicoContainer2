package org.picocontainer.logging.store;

import org.junit.Test;
import org.picocontainer.logging.Logger;
import org.picocontainer.logging.loggers.NullLogger;

/**
 * Created by IntelliJ IDEA.
 * User: paul
 * Date: May 17, 2008
 * Time: 1:49:01 PM
 * To change this template use File | Settings | File Templates.
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
