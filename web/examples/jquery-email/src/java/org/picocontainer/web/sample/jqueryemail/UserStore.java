package org.picocontainer.web.sample.jqueryemail;

import javax.jdo.PersistenceManager;
import javax.jdo.Query;
import java.util.Collection;
import java.util.logging.Logger;

public class UserStore {

    private PersistenceManager pm;

    public UserStore(PersistenceManager pm) {
        this.pm = pm;
    }

    public User getUser(String name) {
        Query query = pm.newQuery("javax.jdo.query.JDOQL", "SELECT FROM " + User.class.getName() + " WHERE name == \"" + name + "\"");
//        query.declareImports("import java.lang.String");
//        query.declareParameters("String name");
        Collection<User> messages = (Collection<User>) query.execute();
        if (messages != null && messages.size() > 0) {
            return messages.iterator().next();
        } else {
            Logger.getAnonymousLogger().info("for [" + name + "]");
            return null;
        }
    }
}
