package org.picocontainer.web.sample.jqueryemail;

import javax.jdo.PersistenceManager;
import javax.jdo.Query;
import java.util.Collection;
import java.util.logging.Logger;

public class UserStore {

    private PersistenceManager pm;
    private final QueryStore queryStore;

    public UserStore(PersistenceManager pm, QueryStore queryStore) {
        this.pm = pm;
        this.queryStore = queryStore;
    }

    public User getUser(String name) {
        Query query = queryStore.get("GU");
        if (query == null) {
            query = pm.newQuery(User.class.getName(), "name == user_name");
            query.declareImports("import java.lang.String");
            query.declareParameters("String user_name");
            queryStore.put("GU", query);
        }
        Collection<User> messages = (Collection<User>) query.execute(name);
        if (messages != null && messages.size() > 0) {
            return messages.iterator().next();
        } else {
            return null;
        }
    }
}
