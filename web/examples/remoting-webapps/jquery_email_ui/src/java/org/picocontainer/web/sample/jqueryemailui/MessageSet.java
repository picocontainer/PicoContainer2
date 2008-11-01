
package org.picocontainer.web.sample.jqueryemailui;
import java.util.ArrayList;

public class MessageSet {
    
    private ArrayList<MessageData> internalList;
    
    public MessageSet(ArrayList<MessageData> list) {
    	internalList = list;
    }
    	
    public MessageSet() {
    	internalList = new ArrayList<MessageData>();
    }
        
    public void add(MessageData obj) {
    	internalList.add(obj);
    }

    public void add(int pos, MessageData obj) {
    	internalList.add(pos,obj);
    }

    public void remove(MessageData obj) {
    	internalList.remove(obj);
    }
    
    public MessageData get(int index) {
        return (MessageData)internalList.get(index);
    }

    public int size() {
        return internalList.size();
    }
    
    public int getUnreadCount() {
        int unread = 0;
        for (int ct=0; ct<size(); ct++) {
            if (!get(ct).read) {
            	unread++;
            }
        }        
        return unread;
    }     
    
    public int getReadCount() {
        int read = 0;
        for (int ct=0; ct<size(); ct++) {
            if (get(ct).read) {
            	read++;
            }
        }        
        return read;
    }       
        
}
