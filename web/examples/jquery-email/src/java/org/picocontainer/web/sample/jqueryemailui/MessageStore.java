package org.picocontainer.web.sample.jqueryemailui;

import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;

public class MessageStore {

    private Map<String, List<MessageData>> messagesIn = new HashMap<String, List<MessageData>>();
    private Map<String, List<MessageData>> messagesOut = new HashMap<String, List<MessageData>>();
    private static final String GIL_BATES = "Gil Bates";
    private static final String BEEVE_SALMER = "Beeve Salmer";

    public MessageStore() {

        List<MessageData> messages = new ArrayList<MessageData>();

        messages.add(new MessageData(1, "J Query", GIL_BATES, "Nice Example", "Very nice example application you've created", false));
        messages.add(new MessageData(2, "Needie Joe", GIL_BATES, "Give me Money!", "You're one of the most rich people in the world, help me out", false));
        messages.add(new MessageData(3, "LotteryWinner", GIL_BATES, "You've Won the Lottery", "Just send us $2000 and we'll send you the money.", false));
        messages.add(new MessageData(4, "Barbara Smith", GIL_BATES, "Leaving Early on Friday", "I have to take my son to the doctor.", false));
        messages.add(new MessageData(5, "Amy Jones", GIL_BATES, "Status Report", "Please find my status report for the week here.", false));
        messages.add(new MessageData(6, "Instant Millionaire", GIL_BATES, "HOT STOCK TIP!!", "Buy XWFX, Buy XWFX, Buy XWFX!", false));
        messages.add(new MessageData(7, "R. Benjamin Graham IV", GIL_BATES, "Meeting Moved to 4pm", "", false));
        messages.add(new MessageData(8, "Trudy Barker", GIL_BATES, "Coming in Late", "Not that you'll ever notice", false));
        messages.add(new MessageData(9, "Sammy Shaggs", GIL_BATES, "Lunch?", "Let's Do Lunch at McDonald's - you can buy!", false));
        messages.add(new MessageData(10, "Kate Robertson", GIL_BATES, "When are our raises coming?", "I need more money", false));

        messagesIn.put(GIL_BATES, messages);

        messages = new ArrayList<MessageData>();
        messages.add(new MessageData(1, GIL_BATES, "Jeeves Sobs", "Nice OS", "You've made a great OS there Jeeves", false));

        messagesOut.put(GIL_BATES, messages);

        messages = new ArrayList<MessageData>();
        messages.add(new MessageData(1, "Parah Salin", BEEVE_SALMER, "Job", "I need a job pls.", false));

        messagesIn.put(BEEVE_SALMER, messages);

        messages = new ArrayList<MessageData>();
        messages.add(new MessageData(1, BEEVE_SALMER, "Jeeves Sobs", "Rubbish OS", "Your OS is just a joke", false));

        messagesOut.put(BEEVE_SALMER, messages);

    }

    public List<MessageData> inboxFor(String name) {
        return messagesIn.get(name);  
    }

    public List<MessageData> sentFor(String name) {
        return messagesOut.get(name);
    }
}
