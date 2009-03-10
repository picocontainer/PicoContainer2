package org.picocontainer.web.sample.stub;

import java.io.Serializable;

public class AppScopeComp implements Serializable {

    private int counter;

    public String getCounter() {
        return "AppScoped:" + ++counter;
    }

}
