package org.picocontainer.web.sample.jqueryemailui;

import java.util.ArrayList;

/**
 * This is our sample interface to our "Database".  This class obviously is not coded well, but 
 * since this is an article about client-side code, it doesn't really matter, now does it
 */
public class MessageDB {
		
	public static MessageSet lookupForUser(int userID) {
		return new MessageSet(messages);
	}
	
	public static void read(int messageID) {
		for (int i=0; i<messages.size(); i++)
		{
			if (messages.get(i).id == messageID)
				messages.get(i).read = true;
		}
	}
	
	public static void delete(int messageID) {
		for (int i=0; i<messages.size(); i++)
		{
			if (messages.get(i).id == messageID)
				messages.remove(messages.get(i));
		}
	}
	
	public static MessageData lookup(int messageID) {
		for (int i=0; i<messages.size(); i++)
		{
			if (messages.get(i).id == messageID)
				return messages.get(i);
		}
		return null;
	}
	
	// Use this as our "Database" for this demonstration application
	private static ArrayList<MessageData> messages = new ArrayList<MessageData>();
	
	static
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

}
