package org.picocontainer.web.sample.jqueryemailui;

import java.util.ArrayList;
import java.util.List;
import java.util.Date;

public class Mailbox {

    private final List<MessageData> messages;

    public Mailbox(List<MessageData> messages) {
        this.messages = messages;
    }

    protected MessageData addMessage(MessageData newMsg) {
        int highestId = 0;
        for (int i = 0; i < messages.size(); i++) {
            if (messages.get(i).id > highestId) {
                highestId = messages.get(i).id;
            }
        }
        newMsg.setId(++highestId);
        newMsg.setSentTime(new Date(System.currentTimeMillis()));

        messages.add(newMsg);
        return newMsg;
    }

    public Object read(int msgId) {

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
