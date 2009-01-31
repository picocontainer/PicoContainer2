
package org.picocontainer.web.sample.jqueryemail;

import java.util.Date;

public class MessageData {
    
    public int id;
    public String from;
    public String to;
    public String subject;
    public String message;
    public Date sentTime;
    public boolean read;

    public MessageData(int id, String from, String to,
            String subject, String message, boolean isRead, long time) {
    	this.id = id;
    	this.from = from;
    	this.to = to;
    	this.subject = subject;
    	this.message = message;
    	this.read = isRead;
        this.sentTime = new Date(time);
    }

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getFrom() {
		return from;
	}

    public String getTo() {
		return to;
	}

    public boolean isRead() {
		return read;
	}

    public String getSubject() {
		return subject;
	}

    public String getMessage() {
		return message;
	}

    public Date getSentTime() {
		return sentTime;
	}

}
