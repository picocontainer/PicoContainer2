package org.picocontainer.web.sample.stub;

import java.io.Serializable;

public class SessionScopeComp implements Serializable {

    private AppScopeComp appScopeComp;
    private int counter;

    public SessionScopeComp(AppScopeComp appScopeComp) {
        this.appScopeComp = appScopeComp;
    }

    public String getCounter() {
        return appScopeComp.getCounter() + ", SessionScoped:" + ++counter;
    }

}