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

        @Override
        public Object getComponentKey() {
            return User.class;    
        }

        @Override
        public Class getComponentImplementation() {
            return User.class;    
        }

        @Override
        public Object provide(HttpServletRequest req) {
            try {
                return new User((String) super.provide(req));
            } catch (NotFound e) {
                e.printStackTrace();
                throw new NotLoggedIn();
            }
        }
    }

    public static class NotLoggedIn extends RuntimeException {
         NotLoggedIn() {
            super("not logged in");
        }
    }
}
