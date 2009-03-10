package org.picocontainer.web.sample.stub;

import java.io.Serializable;

public class RequestScopeComp implements Serializable {

    private SessionScopeComp sessionScopeComp;
    private int counter;

    public RequestScopeComp(SessionScopeComp sessionScopeComp) {
        this.sessionScopeComp = sessionScopeComp;
    }

    public String getCounter() {
        return sessionScopeComp.getCounter() + ", RequestScoped:" + ++counter;
    }

}