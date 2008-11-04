package org.picocontainer.web.sample.jqueryemailui;

import java.util.ArrayList;
import java.util.List;

public class MailBox {

    private final List<MessageData> messages;

    public MailBox(List<MessageData> messages) {
        this.messages = messages;
    }

    public Object read(int msgId, String view) {

        for (int i = 0; i < messages.size(); i++) {
            if (messages.get(i).id == msgId) {
                messages.get(i).read = true;
                return messages.get(i);
            }
        }
        return null;
    }

    public Boolean delete(int msgId) {
        for (int i = 0; i < messages.size(); i++) {
            if (messages.get(i).id == msgId) {
                messages.get(i).read = true;
                return messages.remove(messages.get(i));
            }
        }
        return false;

    }

    public Object[] messages(int userID) {
        return messages.toArray();
    }

}
