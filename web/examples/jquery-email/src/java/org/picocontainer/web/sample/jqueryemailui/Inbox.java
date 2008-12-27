package org.picocontainer.web.sample.jqueryemailui;

/**
 * Inbox is a type of Mailbox for a user.
 */
public class Inbox extends Mailbox {

    public Inbox(MessageStore store, User user) {
        super(store.inboxFor(user));
    }

}
