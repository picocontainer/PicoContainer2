package org.picocontainer.web.sample.ajaxemail;

import javax.jdo.*;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.ServletException;
import java.util.Collection;
import java.util.List;
import java.io.IOException;

public class JdoPersistenceManagerWrapper implements PersistenceManagerWrapper {


//    public static interface GoogleServices {
//        <T> T getService(Class<T> clazz);
//        <T> T getService(Class<T> clazz, String hint);
//    }
//
//    public static class FooServlet extends HttpServlet {
//
//        private final PersistenceManager pm;
//
//        /** GAE fills this in */
//        public FooServlet(GoogleServices services) {
//            this.pm = services.getService(PersistenceManager.class,
//                    "transactional");
//        }
//
//        /** But let us also stay compatible with non AppEngine servers */
//        public FooServlet() {
//            this.pm = JDOHelper.getPersistenceManagerFactory("transactional")
//            .getPersistenceManager();
//        }
//
//        protected void doGet(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) throws ServletException, IOException {
//        }
//    }

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
