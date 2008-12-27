package org.picocontainer.web.sample.jqueryemail;

import org.junit.Test;
import org.junit.Before;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import org.junit.runner.RunWith;
import org.picocontainer.web.sample.jqueryemailui.User;
import org.picocontainer.web.sample.jqueryemailui.MessageStore;
import org.picocontainer.web.sample.jqueryemailui.MessageData;
import org.picocontainer.web.sample.jqueryemailui.Inbox;
import org.picocontainer.web.sample.jqueryemailui.Mailbox;
import org.jmock.integration.junit4.JMock;
import org.jmock.Mockery;
import org.jmock.Expectations;

import java.util.Map;
import java.util.HashMap;

public class MailboxTest {

    private Map<Integer, MessageData> data = new HashMap<Integer, MessageData>();
    private User fred = new User("Fred");
    private MessageData md = new MessageData(2, "Fred", "to", "subj", "message", false, 12345);

    @Before
    public void setUp() {

        data.put(2, md);

    }

    @Test
    public void testReadingOfMessages() {
        Mailbox mailbox = new Mailbox(data) {};
        MessageData[] messages = mailbox.messages();
        assertEquals(1, messages.length);
        assertEquals(md, messages[0]);
        verifyMessage(messages[0], false);
    }

    @Test
    public void testReadOfSingleMessageFlipsReadFlag() {
        Mailbox mailbox = new Mailbox(data) {};
        MessageData message = mailbox.read(2);
        assertEquals(md, message);
        verifyMessage(message, true);
    }

    @Test
    public void testReadOfMissingMessageCausesException() {
        Mailbox mailbox = new Mailbox(data) {};
        try {
            MessageData message = mailbox.read(22222);
        } catch (Exception e) {
            assertEquals("no such message ID", e.getMessage());
        }
    }

    @Test
    public void testDeleteOfSingleMessage() {
        Mailbox mailbox = new Mailbox(data) {};
        assertEquals(1, data.size());
        mailbox.delete(2);
        assertEquals(0, data.size());
    }

    @Test
    public void testDeleteOfMissingMessageCausesException() {
        Mailbox mailbox = new Mailbox(data) {};
        try {
            mailbox.delete(22222);
        } catch (Exception e) {
            assertEquals("no such message ID", e.getMessage());
        }
    }


    private void verifyMessage(MessageData md, boolean read) {
        assertEquals("to", md.getTo());
        assertEquals(2, md.getId());
        assertEquals("subj", md.getSubject());
        assertEquals("message", md.getMessage());
        assertEquals("Fred", md.getFrom());
        assertEquals(read, md.isRead());
        assertEquals(12345, md.getSentTime().getTime());
    }

}