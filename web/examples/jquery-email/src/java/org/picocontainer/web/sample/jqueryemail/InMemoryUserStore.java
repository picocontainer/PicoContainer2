package org.picocontainer.web.sample.jqueryemail;

import java.util.Map;
import java.util.HashMap;

public class InMemoryUserStore implements UserStore {

    Map<String, User> users = new HashMap<String, User>();

    {
        users.put(InMemoryMessageStore.GIL_BATES, new User(InMemoryMessageStore.GIL_BATES, "1234"));
        users.put(InMemoryMessageStore.BEEVE_SALMER, new User(InMemoryMessageStore.GIL_BATES, "1234"));
    }


    public User getUser(String name) {
        return users.get(name);
    }
}
