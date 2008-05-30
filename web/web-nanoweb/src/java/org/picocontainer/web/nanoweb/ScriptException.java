package org.picocontainer.web.nanoweb;

import java.net.URL;

/**
 * @author Aslak Helles&oslash;y
 */
public class ScriptException extends Exception {
    private final URL scriptURL;

    public ScriptException(URL scriptURL, Exception e) {
        super(e);
        this.scriptURL = scriptURL;
    }

    public URL getScriptURL() {
        return scriptURL;
    }
}