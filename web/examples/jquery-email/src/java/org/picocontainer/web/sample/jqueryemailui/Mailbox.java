package org.picocontainer.web.sample.jqueryemailui;

import java.util.ArrayList;
import java.util.List;
import java.util.Date;
import java.util.Map;
import java.util.Set;
import java.util.Collections;
import java.util.Iterator;

public abstract class Mailbox {

    private final Map<Integer, MessageData> messages;

    public Mailbox(Map<Integer, MessageData> messages) {
        this.messages = messages;
    }

    protected MessageData addMessage(MessageData newMsg) {
        int highestId = 0;
        Iterator<Map.Entry<Integer, MessageData>> entryIterator = messages.entrySet().iterator();
        while (entryIterator.hasNext()) {
            if (entryIterator.next().getValue().id > highestId) {
                highestId = entryIterator.next().getValue().id;
            }
        }
        newMsg.setId(++highestId);
        newMsg.setSentTime(new Date(System.currentTimeMillis()));
        return messages.put(newMsg.getId(), newMsg);
    }

    public MessageData read(int msgId) {
        MessageData messageData = messages.get(msgId);
        messageData.read = true;
        return messageData;
    }

    public Boolean delete(int msgId) {
        return messages.remove(messages.get(msgId)) != null;
    }

    public MessageData[] messages() {
        List<MessageData> list = new ArrayList<MessageData>();
        Iterator<Map.Entry<Integer, MessageData>> entryIterator = messages.entrySet().iterator();
        while (entryIterator.hasNext()) {
            list.add(entryIterator.next().getValue());
        }
        MessageData[] messages = new MessageData[list.size()];
        return list.toArray(messages);
    }

}
