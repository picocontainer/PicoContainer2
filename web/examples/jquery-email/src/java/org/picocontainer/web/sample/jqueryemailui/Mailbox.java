package org.picocontainer.web.sample.jqueryemailui;

import java.util.ArrayList;

public class Mailbox {

    // Use this as our "Database" for this demonstration application
    private ArrayList<MessageData> messages = new ArrayList<MessageData>();

    {
        messages.add(new MessageData(1, "J Query", "Gil Bates", "Nice Example", "Very nice example application you've created", false));
        messages.add(new MessageData(2, "Needie Joe", "Gil Bates", "Give me Money!", "You're one of the most rich people in the world, help me out", false));
        messages.add(new MessageData(3, "LotteryWinner", "Gil Bates", "You've Won the Lottery", "Just send us $2000 and we'll send you the money.", false));
        messages.add(new MessageData(4, "Barbara Smith", "Gil Bates", "Leaving Early on Friday", "I have to take my son to the doctor.", false));
        messages.add(new MessageData(5, "Amy Jones", "Gil Bates", "Status Report", "Please find my status report for the week here.", false));
        messages.add(new MessageData(6, "Instant Millionaire", "Gil Bates", "HOT STOCK TIP!!", "Buy XWFX, Buy XWFX, Buy XWFX!", false));
        messages.add(new MessageData(7, "R. Benjamin Graham IV", "Gil Bates", "Meeting Moved to 4pm", "", false));
        messages.add(new MessageData(8, "Trudy Barker", "Gil Bates", "Coming in Late", "Not that you'll ever notice", false));
        messages.add(new MessageData(9, "Sammy Shaggs", "Gil Bates", "Lunch?", "Let's Do Lunch at McDonald's - you can buy!", false));
        messages.add(new MessageData(10, "Kate Robertson", "Gil Bates", "When are our raises coming?", "I need more money", false));
    }

    public Object read(String msgId, String view) {
        int messageId = Integer.parseInt(msgId);
        if (view.equals("inbox"))
        {
            MessageDB.read(messageId);
        }
        return MessageDB.lookup(messageId);
    }

    public Boolean delete(String delId) {
        if (delId != null)
        {
            MessageDB.delete(Integer.parseInt(delId));
        }
        return true;

    }

    private String userName = "Gil Bates";

    public Boolean send(String to, String subject, String message) {
        MessageData msg = new MessageData();
        msg.to = to;
        msg.subject = subject;
        msg.message = message;
        msg.from = userName;
        // Send the message here - nothing happens in demo
        // MessageAction.send(msg);
        return true;
    }

    public MessageSet messages(int userID) {
		return new MessageSet(messages);
	}


}
