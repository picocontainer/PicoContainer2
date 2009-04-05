package org.picocontainer.web.sample.ajaxemail;

import javax.jdo.*;
import java.util.Collection;
import java.util.List;

public class JdoPersistenceManagerWrapper implements PersistenceManagerWrapper {
    
    PersistenceManager pm = JDOHelper.getPersistenceManagerFactory("transactional").getPersistenceManager();

    public void makePersistent(Object persistent) {
        pm.makePersistent(persistent);
    }

    public void beginTransaction() {
        pm.currentTransaction().begin();
    }

    public void commitTransaction() {
        pm.currentTransaction().commit();        
    }

    public Query newQuery(Class<?> clazz, String query) {
        final javax.jdo.Query qry = pm.newQuery(clazz, query);
        return new Query() {
            public Object execute(Object arg) {
                if (arg == null) {
                    return qry.execute();                    
                } else {
                    return qry.execute(arg);
                }

            }

            public void declareImports(String imports) {
                qry.declareImports(imports);
            }

            public void declareParameters(String parameters) {
                qry.declareParameters(parameters);
            }

        };
    }

    public void deletePersistent(Object persistent) {
        pm.deletePersistent(persistent);
    }

}
