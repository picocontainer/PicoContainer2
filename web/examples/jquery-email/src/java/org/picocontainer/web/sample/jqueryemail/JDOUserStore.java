package org.picocontainer.web.sample.jqueryemail;

import javax.jdo.PersistenceManager;
import javax.jdo.Query;
import java.util.Collection;

public class JDOUserStore implements UserStore {

    private PersistenceManager pm;

    private static final String DEFAULT_QUERY = "SELECT FROM " + User.class.getName();

    public JDOUserStore(PersistenceManager pm) {
        this.pm = pm;
    }

    public User getUser(String name) {
        Query query = pm.newQuery("javax.jdo.query.JDOQL", DEFAULT_QUERY + " WHERE name = '" + name + "'");
        query.declareImports("import java.lang.String");
        query.declareParameters("String name");
        Collection<User> messages = (Collection<User>) query.execute();
        if (messages != null) {
            return messages.iterator().next();
        } else {
            return null;
        }
    }
}
