package org.picocontainer.web.sample.jqueryemail;

import javax.jdo.PersistenceManager;

/**
 * Inbox is a type of Mailbox for a user.
 */
public class Inbox extends Mailbox {

    public Inbox(PersistenceManager pm, User user, QueryStore queryStore) {
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
}
