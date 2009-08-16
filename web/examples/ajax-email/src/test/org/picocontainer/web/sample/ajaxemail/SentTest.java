package org.picocontainer.web.sample.ajaxemail;

import org.junit.Test;
import org.junit.Before;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import org.junit.runner.RunWith;
import org.picocontainer.web.sample.ajaxemail.Message;
import org.picocontainer.web.sample.ajaxemail.User;
import org.picocontainer.web.sample.ajaxemail.persistence.Persister;
import org.jmock.integration.junit4.JMock;
import org.jmock.Mockery;
import org.jmock.Expectations;

import java.util.Collection;
import java.util.ArrayList;

@RunWith(JMock.class)
public class SentTest {

    private Mockery mockery = new Mockery();
    private Collection<Message> data;
    private Persister pm;
    private User fred = new User("Fred", "password");
    private Query query;

    @Before
    public void setUp() {
        pm = mockery.mock(Persister.class);
        query = mockery.mock(Query.class);
        data = new ArrayList();
        mockery.checking(new Expectations(){{
            one(pm).newQuery(Message.class, "from == user_name");
    		will(returnValue(query));
            one(query).declareImports("import java.lang.String");
            one(query).declareParameters("String user_name");
            one(query).execute("Fred");
            will(returnValue(data));
        }});
    }

    @Test
    public void testInboxCallsRightStoreMethod() {
        Sent sent = new Sent(pm, fred, new QueryStore());
        assertEquals(0, sent.messages().length);    }

}