package org.picocontainer.web.sample.ajaxemail;

import java.util.Collection;
import java.util.List;

public interface Query {
    
    Object execute(Object arg);

    void declareImports(String imports);

    void declareParameters(String parameters);

}
