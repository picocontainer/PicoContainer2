package org.picocontainer.web.sample.jqueryemail;

import javax.jdo.PersistenceManager;

/**
 * Send is a type of Mailbox for a user.
 */
public class Sent extends Mailbox {

    public Sent(PersistenceManager pm, User user, QueryStore queryStore) {
        super(pm, user, queryStore);
    }

    /**
     * Send a message
     *
     * @param to  the recipient
     * @param subject the message subject
     * @param message the message body
     * @return the resulting message
     */
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
}
