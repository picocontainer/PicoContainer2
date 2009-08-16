package org.picocontainer.web.sample.ajaxemail;

import java.util.Collection;

import org.picocontainer.web.sample.ajaxemail.persistence.Persister;

public class UserStore {

    private transient Persister pm;
    private transient final QueryStore queryStore;

    public UserStore(Persister pm, QueryStore queryStore) {
        this.pm = pm;
        this.queryStore = queryStore;
    }

    public User getUser(String name) {
        Query query = queryStore.get("GU");
        if (query == null) {
            query = pm.newQuery(User.class, "name == user_name");
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
