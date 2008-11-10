package org.picocontainer.web.sample.jqueryemailui;

import org.picocontainer.web.StringFromCookie;

import javax.servlet.http.HttpServletRequest;

public class User {

    private String userName;

    public User(String userName) {
        this.userName = userName;
    }

    public String getName() {
        return userName;
    }

    public static class FromCookie extends StringFromCookie {
        public FromCookie() {
            super("userName");
        }

        public Object getComponentKey() {
            return User.class;    
        }

        public Class getComponentImplementation() {
            return User.class;    
        }

        public Object provide(HttpServletRequest req) {
            return new User((String) super.provide(req));
        }
    }
}
