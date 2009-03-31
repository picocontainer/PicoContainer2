package org.picocontainer.web.sample.ajaxemail;

public interface Sent extends Mailbox {

    /**
     * Send a message
     *
     * @param to  the recipient
     * @param subject the message subject
     * @param message the message body
     * @return the resulting message
     */
    Message send(String to, String subject, String message);

}
