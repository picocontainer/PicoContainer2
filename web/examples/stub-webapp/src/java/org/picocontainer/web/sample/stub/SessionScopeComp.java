package org.picocontainer.web.sample.stub;

import java.io.Serializable;
import java.util.logging.Logger;

public class SessionScopeComp implements Serializable {

    private AppScopeComp appScopeComp;
    private int counter;

    public SessionScopeComp(AppScopeComp appScopeComp) {
        this.appScopeComp = appScopeComp;
        Logger logger = Logger.getLogger(this.getClass().getName());
        logger.info("sc-i");        
    }

    public String getCounter() {
        Logger logger = Logger.getLogger(this.getClass().getName());
        logger.info("s-gc");
        return appScopeComp.getCounter() + ", SessionScoped:" + ++counter;
    }

}