package org.picocontainer.web.remoting;

public class ErrorReply {
    private boolean ERROR = true;
    private String message;

    public ErrorReply(String message) {
        this.message = message;
    }
}
