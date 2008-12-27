package org.picocontainer.web.sample.jqueryemailui;

/**
 * Send is a type of Mailbox for a user.
 */
public class Sent extends Mailbox {

    private final User user;

    public Sent(MessageStore store, User user) {
        super(store.sentFor(user));
        this.user = user;
    }

    // Inherit methods from Mailbox

    /**
     * Send a message
     * @param to whom to
     * @param subject what subjct
     * @param message the message body
     * @return the resulting message
     */
    public MessageData send(String to, String subject, String message) {
        return super.addMessage(new MessageData(0, user.getName(), to, subject, message, false, System.currentTimeMillis()));
    }

}
