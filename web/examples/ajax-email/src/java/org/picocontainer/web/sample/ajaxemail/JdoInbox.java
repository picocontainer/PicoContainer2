package org.picocontainer.web.sample.ajaxemail;

import javax.jdo.PersistenceManager;

/**
 * Inbox is a type of Mailbox for a user.
 */
public class JdoInbox extends JdoMailbox implements IInbox {

    public JdoInbox(PersistenceManager pm, User user, QueryStore queryStore) {
        super(pm, user, queryStore);
    }

    protected void checkUser(Message message) {
        if (!message.getTo().equals(user.getName())) {
            throwNotForThisUser();
        }
    }

    protected String fromOrTo() {
        return "to";
    }

    public String toString() {
        return "inbox-for-" + user.getName();
    }
    
}
