package org.picocontainer.web.sample.jqueryemail;

import org.junit.Test;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import org.junit.runner.RunWith;
import org.picocontainer.web.sample.jqueryemail.Message;
import org.picocontainer.web.sample.jqueryemail.MessageStore;
import org.picocontainer.web.sample.jqueryemail.Sent;
import org.picocontainer.web.sample.jqueryemail.User;

import static org.picocontainer.tck.MockFactory.mockeryWithCountingNamingScheme;
import org.picocontainer.ComponentMonitor;
import org.jmock.integration.junit4.JMock;
import org.jmock.Mockery;
import org.jmock.Expectations;
import org.jmock.lib.CamelCaseNamingScheme;

import java.util.Map;
import java.util.HashMap;

@RunWith(JMock.class)
public class SentTest {

    private Mockery mockery = new Mockery();

    @Test
    public void testSendingAMessageWorks() {

        final MessageStore store = mockery.mock(MessageStore.class);
        final User fred = new User("Fred", "password");

        final Map<Integer, Message> data = new HashMap<Integer, Message>();

        mockery.checking(new Expectations(){{
    		one(store).sentFor(fred);
    		will(returnValue(data));
    	}});

        Sent sent = new Sent(store, fred);
        long before = System.currentTimeMillis();

        Message md = sent.send("to","subj","message");

        long after = System.currentTimeMillis();
        assertEquals("to", md.getTo());
        assertEquals(1, md.getId());
        assertEquals("subj", md.getSubject());
        assertEquals("message", md.getMessage());
        assertEquals("Fred", md.getFrom());
        assertTrue(md.getSentTime().getTime() >= before);
        assertTrue(md.getSentTime().getTime() <= after);
        assertEquals(1, data.size());
        assertEquals(md, data.get(1));

    }

}
