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
import org.jmock.integration.junit4.JMock;
import org.jmock.Mockery;
import org.jmock.Expectations;

import java.util.Map;
import java.util.HashMap;
import java.util.HashSet;

@RunWith(JMock.class)
public class InboxTest {

    private Mockery mockery = new Mockery();
    private Map<Integer, MessageData> data;
    private MessageStore store;
    private User fred = new User("Fred");

    @Before
    public void setUp() {
        store = mockery.mock(MessageStore.class);
        data = mockery.mock(Map.class);
        mockery.checking(new Expectations(){{
    		one(store).inboxFor(fred);
    		will(returnValue(data));
            one(data).entrySet();
            will(returnValue(new HashSet()));
    	}});
    }

    @Test
    public void testInboxCallsRightStoreMethod() {
        Inbox inbox = new Inbox(store, fred);
        assertEquals(0, inbox.messages().length);
    }

}