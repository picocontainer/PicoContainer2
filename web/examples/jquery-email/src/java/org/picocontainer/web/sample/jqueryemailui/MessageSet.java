
package org.picocontainer.web.sample.jqueryemailui;
import java.util.ArrayList;

public class MessageSet {
    
    private ArrayList<MessageData> internalList;

    public Object[] toArray() {
        return internalList.toArray();
    }

    public MessageSet(ArrayList<MessageData> list) {
    	internalList = list;
    }

    public int size() {
        return internalList.size();
    }

}
