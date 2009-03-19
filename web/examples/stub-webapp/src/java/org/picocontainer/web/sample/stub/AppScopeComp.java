package org.picocontainer.web.sample.stub;

import java.io.Serializable;
import java.util.logging.Logger;

public class AppScopeComp implements Serializable {

    private int counter;

    public AppScopeComp() {
        Logger logger = Logger.getLogger(this.getClass().getName());
        logger.info("ac-i");
    }

    public String getCounter() {
        Logger logger = Logger.getLogger(this.getClass().getName());
        logger.info("a-gc-1 " + counter + " " + System.identityHashCode(this));
        ++counter;
        logger.info("a-gc-2 " + counter);
        return "AppScoped:" + counter;
    }

    public String toString() {
        return "tos-" + counter;
    }
}
