package org.picocontainer.web.sample.ajaxemail;

import org.junit.Test;
import org.junit.Before;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import org.picocontainer.web.sample.ajaxemail.Message;
import org.picocontainer.web.sample.ajaxemail.User;
import org.picocontainer.web.sample.ajaxemail.persistence.Persister;
import org.jmock.Mockery;
import org.jmock.Expectations;

import java.util.List;
import java.util.ArrayList;

import static junit.framework.Assert.fail;

public class MailboxTest {

    private Mockery mockery = new Mockery();

    private List<Message> data;
    private User fred = new User("Fred", "password");
    private Message message = new Message("Fred", "to", "subj", "message", false, 12345);
    private Persister pm;
    private Query query;

    @Before
    public void setUp() {

        data = new ArrayList<Message>();
        data.add(message);
        message.setId(2L);
        pm = mockery.mock(Persister.class);
        query = mockery.mock(Query.class);

    }

    @Test
    public void testReadingOfMessages() {
        mockery.checking(new Expectations(){{
    		one(pm).newQuery(Message.class, "XXX == user_name");
    		will(returnValue(query));
            one(query).declareImports("import java.lang.String");
            one(query).declareParameters("String user_name");
            one(query).execute("Fred");
            will(returnValue(data));
        }});

        Mailbox mailbox = new MyMailbox(pm, fred);
        Message[] messages = mailbox.messages();
        assertEquals(1, messages.length);
        assertEquals(message, messages[0]);
        verifyMessage(message, false, null);
    }

    @Test
    public void testReadOfSingleMessageFlipsReadFlag() {
        mockery.checking(new Expectations(){{
            one(pm).newQuery(Message.class, "id == message_id");
    		will(returnValue(query));
            one(pm).beginTransaction();
            one(pm).commitTransaction();
            one(query).declareImports("import java.lang.Long");
            one(query).declareParameters("Long message_id");                        
            one(query).execute(2L);
            will(returnValue(data));
        }});

        Mailbox mailbox = new MyMailbox(pm, fred);
        assertEquals(message, mailbox.read(2));
        verifyMessage(message, true, "message");
    }

    @Test
    public void testReadOfMissingMessageCausesException() {
        mockery.checking(new Expectations(){{
            one(pm).newQuery(Message.class, "id == message_id");
    		will(returnValue(query));
            one(pm).beginTransaction();
            one(pm).commitTransaction();
            one(query).declareImports("import java.lang.Long");
            one(query).declareParameters("Long message_id");
            one(query).execute(22222L);
            will(returnValue(new ArrayList()));
        }});

        Mailbox mailbox = new MyMailbox(pm, fred);
        try {
            Message message = mailbox.read(22222);
            fail();
        } catch (AjaxEmailException e) {
            assertEquals("no such message ID", e.getMessage());
        }
    }

    @Test
    public void testDeleteOfSingleMessage() {
        mockery.checking(new Expectations(){{
            one(pm).newQuery(Message.class, "id == message_id");
            will(returnValue(query));
            one(query).declareImports("import java.lang.Long");
            one(query).declareParameters("Long message_id");
            one(query).execute(2L);
            will(returnValue(data));
            one(pm).deletePersistent(message);
        }});

        Mailbox mailbox = new MyMailbox(pm, fred);
        mailbox.delete(2);
    }

    @Test
    public void testDeleteOfMissingMessageCausesException() {
        mockery.checking(new Expectations(){{
            one(pm).newQuery(Message.class, "id == message_id");
            will(returnValue(query));
            one(query).declareImports("import java.lang.Long");
            one(query).declareParameters("Long message_id");
            one(query).execute(22222L);
            will(returnValue(null));
        }});
        Mailbox mailbox = new MyMailbox(pm, fred);
        try {
            mailbox.delete(22222);
        } catch (AjaxEmailException e) {
            assertEquals("no such message ID", e.getMessage());
        }
    }


    private void verifyMessage(Message m, boolean read, String messageVal) {
        assertEquals("to", m.getTo());
        assertEquals(2L, (long) m.getId());
        assertEquals("subj", m.getSubject());
        assertEquals(messageVal, m.getMessage());
        assertEquals("Fred", m.getFrom());
        assertEquals(read, m.isRead());
        assertEquals(12345, m.getSentTime().getTime());
    }

    private class MyMailbox extends Mailbox {
        public MyMailbox(Persister pm, User user) {
            super(pm, user, new QueryStore());
        }

        protected void checkUser(Message message) {
        }

        protected String fromOrTo() {
            return "XXX";
        }
    }
}