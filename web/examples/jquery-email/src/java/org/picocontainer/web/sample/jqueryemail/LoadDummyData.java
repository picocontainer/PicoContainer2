package org.picocontainer.web.sample.jqueryemail;

import javax.jdo.PersistenceManager;
import java.util.Map;
import java.util.logging.Logger;

public class LoadDummyData {
    public LoadDummyData(JdoMessageStore userStore, JdoMessageStore messageStore, PersistenceManager pm) {
        try {
            UserStore users1 = new InMemoryUserStore();
            MessageStore messages1 = new InMemoryMessageStore();
            User gill = users1.getUser("Gill Bates");
            pm.makePersistent(gill);
            User beeve = users1.getUser("Beeve Salmer");
            pm.makePersistent(beeve);

            Map<Integer, Message> messageMap = messageStore.inboxFor(gill);
            for ( Map.Entry<Integer, Message> entry : messages1.inboxFor(gill).entrySet() ) {
                messageMap.put(entry.getKey(), entry.getValue());
            }
            messageMap = messageStore.sentFor(gill);
            for ( Map.Entry<Integer, Message> entry : messages1.sentFor(gill).entrySet() ) {
                messageMap.put(entry.getKey(), entry.getValue());
            }

            messageMap = messageStore.inboxFor(beeve);
            for ( Map.Entry<Integer, Message> entry : messages1.inboxFor(beeve).entrySet() ) {
                messageMap.put(entry.getKey(), entry.getValue());
            }
            messageMap = messageStore.sentFor(beeve);
            for ( Map.Entry<Integer, Message> entry : messages1.sentFor(beeve).entrySet() ) {
                messageMap.put(entry.getKey(), entry.getValue());
            }
        } catch (Throwable e) {
            Logger.getAnonymousLogger().info("data load issue " + e.getMessage());
        }
    }
    
    public void doIt() {
    }
}
