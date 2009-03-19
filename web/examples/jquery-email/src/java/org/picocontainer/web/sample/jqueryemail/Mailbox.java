package org.picocontainer.web.sample.jqueryemail;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Iterator;

/**
 * Abstract Mailbox
 */
public abstract class Mailbox {

    private final Map<Integer, Message> messages;
    public Mailbox(Map<Integer, Message> messages) {
        this.messages = messages;
    }

    protected Message addMessage(Message newMsg) {
        int highestId = 0;
        Iterator<Map.Entry<Integer, Message>> entryIterator = messages.entrySet().iterator();
        while (entryIterator.hasNext()) {
            Message messageData = entryIterator.next().getValue();
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
    public Message read(int msgId) {
        Message messageData = getMessage(msgId);
        messageData.read = true;
        return messageData;
    }

    private Message getMessage(int msgId) {
        Message md = messages.get(msgId);
        if (md == null) {
            throw new JQueryEmailException("no such message ID");
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
    public Message[] messages() {
        List<Message> list = new ArrayList<Message>();
        Iterator<Map.Entry<Integer, Message>> entryIterator = messages.entrySet().iterator();
        while (entryIterator.hasNext()) {
            list.add(entryIterator.next().getValue());
        }
        Message[] messages = new Message[list.size()];
        return list.toArray(messages);
    }

}
