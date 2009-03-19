package org.picocontainer.web.sample.jqueryemail;

import java.util.Map;
import java.util.HashMap;

public class InMemoryMessageStore implements MessageStore {

    private Map<String, Map<Integer, Message>> messagesIn = new HashMap<String, Map<Integer, Message>>();
    private Map<String, Map<Integer, Message>> messagesOut = new HashMap<String, Map<Integer, Message>>();

    static final String GIL_BATES = "Gil Bates";
    static final String BEEVE_SALMER = "Beeve Salmer";

    private long time;

    public InMemoryMessageStore() {

        time = System.currentTimeMillis();

        Map<Integer, Message> messages = new HashMap<Integer, Message>();

        messages.put(1, new Message(1, "J Query", GIL_BATES, "Nice Example", "Very nice example application you've created", false, time() ));
        messages.put(2, new Message(2, "Needie Joe", GIL_BATES, "Give me Money!", "You're one of the most rich people in the world, help me out", false, time()));
        messages.put(3, new Message(3, "LotteryWinner", GIL_BATES, "You've Won the Lottery", "Just send us $2000 and we'll send you the money.", false, time()));
        messages.put(4, new Message(4, "Barbara Smith", GIL_BATES, "Leaving Early on Friday", "I have to take my son to the doctor.", false, time()));
        messages.put(5, new Message(5, "Amy Jones", GIL_BATES, "Status Report", "Please find my status report for the week here.", false, time()));
        messages.put(6, new Message(6, "Instant Millionaire", GIL_BATES, "HOT STOCK TIP!!", "Buy XWFX, Buy XWFX, Buy XWFX!", false, time()));
        messages.put(7, new Message(7, "R. Benjamin Graham IV", GIL_BATES, "Meeting Moved to 4pm", "", false, time()));
        messages.put(8, new Message(8, "Trudy Barker", GIL_BATES, "Coming in Late", "Not that you'll ever notice", false, time()));
        messages.put(9, new Message(9, "Sammy Shaggs", GIL_BATES, "Lunch?", "Let's Do Lunch at McDonald's - you can buy!", false, time()));
        messages.put(10, new Message(10, "Kate Robertson", GIL_BATES, "When are our raises coming?", "I need more money", false, time()));

        messagesIn.put(GIL_BATES, messages);

        messages = new HashMap<Integer, Message>();
        messages.put(1, new Message(1, GIL_BATES, "Jeeves Sobs", "Nice OS", "You've made a great OS there Jeeves", false, time()));

        messagesOut.put(GIL_BATES, messages);

        messages = new HashMap<Integer, Message>();
        messages.put(1, new Message(1, "Parah Salin", BEEVE_SALMER, "Job", "I need a job pls.", false, time()));

        messagesIn.put(BEEVE_SALMER, messages);

        messages = new HashMap<Integer, Message>();
        messages.put(1, new Message(1, BEEVE_SALMER, "Jeeves Sobs", "Rubbish OS", "Your OS is just a joke", false, time()));

        messagesOut.put(BEEVE_SALMER, messages);

    }

    private long time() {
        time = time - 1800000;
        return time;
    }

    public Map<Integer, Message> inboxFor(User user) {
        return messagesIn.get(user.getName());
    }

    public Map<Integer, Message> sentFor(User user) {
        return messagesOut.get(user.getName());
    }
}
