package org.picocontainer.web.sample.jqueryemailui;

import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;

public class MessageStore {

    private Map<String, Map<Integer, MessageData>> messagesIn = new HashMap<String, Map<Integer, MessageData>>();
    private Map<String, Map<Integer, MessageData>> messagesOut = new HashMap<String, Map<Integer, MessageData>>();

    static final String GIL_BATES = "Gil Bates";
    static final String BEEVE_SALMER = "Beeve Salmer";

    public MessageStore() {

        Map<Integer, MessageData> messages = new HashMap<Integer, MessageData>();

        messages.put(1, new MessageData(1, "J Query", GIL_BATES, "Nice Example", "Very nice example application you've created", false));
        messages.put(2, new MessageData(2, "Needie Joe", GIL_BATES, "Give me Money!", "You're one of the most rich people in the world, help me out", false));
        messages.put(3, new MessageData(3, "LotteryWinner", GIL_BATES, "You've Won the Lottery", "Just send us $2000 and we'll send you the money.", false));
        messages.put(4, new MessageData(4, "Barbara Smith", GIL_BATES, "Leaving Early on Friday", "I have to take my son to the doctor.", false));
        messages.put(5, new MessageData(5, "Amy Jones", GIL_BATES, "Status Report", "Please find my status report for the week here.", false));
        messages.put(6, new MessageData(6, "Instant Millionaire", GIL_BATES, "HOT STOCK TIP!!", "Buy XWFX, Buy XWFX, Buy XWFX!", false));
        messages.put(7, new MessageData(7, "R. Benjamin Graham IV", GIL_BATES, "Meeting Moved to 4pm", "", false));
        messages.put(8, new MessageData(8, "Trudy Barker", GIL_BATES, "Coming in Late", "Not that you'll ever notice", false));
        messages.put(9, new MessageData(9, "Sammy Shaggs", GIL_BATES, "Lunch?", "Let's Do Lunch at McDonald's - you can buy!", false));
        messages.put(10, new MessageData(10, "Kate Robertson", GIL_BATES, "When are our raises coming?", "I need more money", false));

        messagesIn.put(GIL_BATES, messages);

        messages = new HashMap<Integer, MessageData>();
        messages.put(1, new MessageData(1, GIL_BATES, "Jeeves Sobs", "Nice OS", "You've made a great OS there Jeeves", false));

        messagesOut.put(GIL_BATES, messages);

        messages = new HashMap<Integer, MessageData>();
        messages.put(1, new MessageData(1, "Parah Salin", BEEVE_SALMER, "Job", "I need a job pls.", false));

        messagesIn.put(BEEVE_SALMER, messages);

        messages = new HashMap<Integer, MessageData>();
        messages.put(1, new MessageData(1, BEEVE_SALMER, "Jeeves Sobs", "Rubbish OS", "Your OS is just a joke", false));

        messagesOut.put(BEEVE_SALMER, messages);

    }

    public Map<Integer, MessageData> inboxFor(String name) {
        return messagesIn.get(name);  
    }

    public Map<Integer, MessageData> sentFor(String name) {
        return messagesOut.get(name);
    }
}
