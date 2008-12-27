package org.picocontainer.web.sample.jqueryemailui;

public class Inbox extends Mailbox {

    public Inbox(InMemoryMessageStore store, User user) {
        super(store.inboxFor(user.getName()));
    }

}
