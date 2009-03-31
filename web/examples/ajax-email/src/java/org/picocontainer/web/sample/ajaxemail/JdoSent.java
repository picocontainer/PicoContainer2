package org.picocontainer.web.sample.ajaxemail;

import javax.jdo.PersistenceManager;

/**
 * Send is a type of Mailbox for a user.
 */
public class JdoSent extends JdoMailbox implements Sent {

    public JdoSent(PersistenceManager pm, User user, QueryStore queryStore) {
        super(pm, user, queryStore);
    }

    public Message send(String to, String subject, String message) {
        return pm.makePersistent(new Message(user.getName(), to, subject,
                message, false, System.currentTimeMillis()));
    }

    /**
     * A guard to prevent someone trying to read emails for others users (they are not logged in as)
     * @param message
     */
    protected void checkUser(Message message) {
        if (message.getTo().equals(user.getName())) {
            throwNotForThisUser();
        }
    }

    protected String fromOrTo() {
        return "from";
    }

    public String toString() {
        return "sent-for-" + user.getName();  
    }

}
