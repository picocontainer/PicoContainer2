package org.picocontainer.web.sample.jqueryemail;

import javax.jdo.PersistenceManager;
import javax.jdo.Query;
import java.util.Map;
import java.util.HashMap;
import java.util.Collection;

public class JDOMessageStore implements MessageStore {

    private PersistenceManager pm;

    public JDOMessageStore(PersistenceManager pm) {
        this.pm = pm;
    }

    private static final String DEFAULT_QUERY = "SELECT FROM " + Message.class.getName();

    public Map<Integer, Message> inboxFor(User name) {
        return new JDOMessageDataMap("to", name.getName());
    }

    public Map<Integer, Message> sentFor(User name) {
        return new JDOMessageDataMap("from", name.getName());
    }

    private class JDOMessageDataMap extends HashMap {

        public Object put(Object key, Object val) {
            pm.makePersistent(val);
            return super.put(key, val);  
        }

        private JDOMessageDataMap(String fromTo, String who) {
            Query query = pm.newQuery("javax.jdo.query.JDOQL", DEFAULT_QUERY + " WHERE " + fromTo + " == \"" + who + "\"");
            query.declareImports("import java.lang.String");
            query.declareParameters("String " + fromTo);
            query.setOrdering("param1 ascending");
            Collection<Message> messages = (Collection<Message>) query.execute();
            for (Message messageData : messages) {
                super.put(messageData.getId(), messageData);
            }

        }
    }

}
