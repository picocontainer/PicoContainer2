
package org.picocontainer.web.sample.jqueryemail;

import java.util.Date;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;
import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.IdentityType;

@PersistenceCapable(identityType = IdentityType.APPLICATION, detachable = "true")
public class Message {

    @PrimaryKey
    @Persistent(valueStrategy = IdGeneratorStrategy.IDENTITY)
    public Long id;

    @Persistent
    public String from;

    @Persistent
    public String to;

    @Persistent
    public String subject;

    @Persistent
    public String message;
    
    @Persistent
    public Date sentTime;

    @Persistent
    public boolean read;

    public Message(String from, String to,
            String subject, String message, boolean isRead, long time) {
    	this.id = id;
    	this.from = from;
    	this.to = to;
    	this.subject = subject;
    	this.message = message;
    	this.read = isRead;
        this.sentTime = new Date(time);
    }

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
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
