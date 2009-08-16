package org.picocontainer.web.sample.ajaxemail.persistence;

import javax.jdo.*;

import org.picocontainer.web.sample.ajaxemail.Message;
import org.picocontainer.web.sample.ajaxemail.Query;
import org.picocontainer.web.sample.ajaxemail.User;

import java.util.Collection;
import java.util.List;
import java.util.ArrayList;

public class InMemoryPersister implements Persister {

    private List<Message> messages = new ArrayList<Message>();
    private List<User> users = new ArrayList<User>();
    private long msgCtr = 0;

    public void makePersistent(Object persistent) {
        if (persistent instanceof Message) {

            Message message = (Message) persistent;
            message.setId(++msgCtr);
            messages.add(message);
        } else {
            users.add((User) persistent);
        }
    }

    public void beginTransaction() {
    }

    public void commitTransaction() {
    }

    public Query newQuery(final Class<?> clazz, final String query) {

        return new Query() {
            public Object execute(Object arg) {
                if (clazz == Message.class) {
                    List<Message> retVal = new ArrayList<Message>();
                    if (query.equals("id == message_id")) {
                        for (Message message : messages) {
                            if (message.getId() == (Long) arg) {
                                retVal.add(message);
                            }
                        }
                    } else if (query.equals("from == user_name")) {
                        for (Message message : messages) {
                            if (message.getFrom().equals((String) arg)) {
                                retVal.add(message);
                            }
                        }
                    } else if (query.equals("to == user_name")) {
                        for (Message message : messages) {
                            if (message.getTo().equals((String) arg)) {
                                retVal.add(message);
                            }
                        }
                    } else if (query.equals("id > -1")) {
                        retVal = messages;
                    }
                    return retVal;
                } else {
                    List<User> retVal = new ArrayList<User>();
                    for (User user : users) {
                        if (user.getName().equals((String) arg)) {
                            retVal.add(user);
                        }
                    }
                    return retVal;
                }
            }

            public void declareImports(String imports) {
            }

            public void declareParameters(String parameters) {
            }

        };
    }

    public void deletePersistent(Object persistent) {
        if (persistent instanceof Message) {
            messages.remove((Message) persistent);
        } else {
            users.remove((User) persistent);
        }
    }

}