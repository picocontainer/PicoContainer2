package org.picocontainer.web.sample.jqueryemailui;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Iterator;

/**
 * Abstract Mailbox
 */
public abstract class Mailbox {

    private final Map<Integer, MessageData> messages;
    public Mailbox(Map<Integer, MessageData> messages) {
        this.messages = messages;
    }

    protected MessageData addMessage(MessageData newMsg) {
        int highestId = 0;
        Iterator<Map.Entry<Integer, MessageData>> entryIterator = messages.entrySet().iterator();
        while (entryIterator.hasNext()) {
            MessageData messageData = entryIterator.next().getValue();
            if (messageData.id > highestId) {
                highestId = messageData.id;
            }
        }
        newMsg.setId(++highestId);
        messages.put(newMsg.getId(), newMsg);
        return newMsg;
    }

    /**
     * Read a message (flip its read flag if not already)
     * @param msgId the message to read
     * @return the message
     */
    public MessageData read(int msgId) {
        MessageData messageData = getMessage(msgId);
        messageData.read = true;
        return messageData;
    }

    private MessageData getMessage(int msgId) {
        MessageData md = messages.get(msgId);
        if (md == null) {
            throw new MailAppException("no such message ID");
        }
        return md;
    }

    /**
     * Delete a message
     * @param msgId the message to delete
     */
    public void delete(int msgId) {
        getMessage(msgId);
        messages.remove(msgId);
    }

    /**
     * List the messages for the user
     * @return the messages
     */
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
