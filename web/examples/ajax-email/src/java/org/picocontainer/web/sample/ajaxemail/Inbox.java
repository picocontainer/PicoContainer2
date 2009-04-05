package org.picocontainer.web.sample.ajaxemail;

import javax.jdo.PersistenceManager;

/**
 * Inbox is a type of Mailbox for a user.
 */
public class Inbox extends Mailbox {

    public Inbox(PersistenceManagerWrapper pm, User user, QueryStore queryStore) {
        super(pm, user, queryStore);
    }

    protected void checkUser(Message message) {
        if (!message.getTo().equals(getUserName())) {
            throwNotForThisUser();
        }
    }

    protected String fromOrTo() {
        return "to";
    }

    public String toString() {
        return "inbox-for-" + getUserName();
    }

}
