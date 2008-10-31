
package org.picocontainer.web.sample.jqueryemailui;

import java.util.Calendar;
import java.util.Date;

/**
 * Make this follow standard JavaBean conventions to work properly with JSON
 * @author MAbernethy
 *
 */
public class MessageData {
    
    public int id;
    public String from;
    public String to;
    public String subject;
    public String message;
    public Date sentTime;
    public boolean read;
        
    public MessageData() {}
    
    public MessageData(int id, String from, String to, String subject, String message, boolean isRead)
    {
    	this.id = id;
    	this.from = from;
    	this.to = to;
    	this.subject = subject;
    	this.message = message;
    	this.read = isRead;
    	this.sentTime = Calendar.getInstance().getTime();
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

	public void setFrom(String from) {
		this.from = from;
	}

	public String getTo() {
		return to;
	}

	public void setTo(String to) {
		this.to = to;
	}

	public boolean isRead() {
		return read;
	}

	public void setRead(boolean read) {
		this.read = read;
	}

	public String getSubject() {
		return subject;
	}

	public void setSubject(String subject) {
		this.subject = subject;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public Date getSentTime() {
		return sentTime;
	}

	public void setSentTime(Date sentTime) {
		this.sentTime = sentTime;
	}
}
